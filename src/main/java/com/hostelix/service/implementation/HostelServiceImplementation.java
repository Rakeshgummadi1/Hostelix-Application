package com.hostelix.service.implementation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hostelix.dto.BedResponseDTO;
import com.hostelix.dto.FloorConfigRequest;
import com.hostelix.dto.FloorResponseDTO;
import com.hostelix.dto.HostelResponseDTO;
import com.hostelix.dto.OccupancyStatsDTO;
import com.hostelix.dto.RoomConfigRequest;
import com.hostelix.dto.RoomFilterRequest;
import com.hostelix.dto.RoomResponseDTO;
import com.hostelix.dto.pgconfig.HostelConfigRequest;
import com.hostelix.enums.RoomType;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.exceptions.UserNotFoundException;
import com.hostelix.model.Bed;
import com.hostelix.model.Floor;
import com.hostelix.model.Hostel;
import com.hostelix.model.Owner;
import com.hostelix.model.Room;
import com.hostelix.repository.BedRepository;
import com.hostelix.repository.FloorRepository;
import com.hostelix.repository.HostelRepository;
import com.hostelix.repository.OwnerRepository;
import com.hostelix.repository.RoomRepository;
import com.hostelix.service.HostelService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HostelServiceImplementation implements HostelService {

	private final HostelRepository hostelRepository;
	private final OwnerRepository ownerRepository;
	private final FloorRepository floorRepository;
	private final RoomRepository roomRepository;
	private final BedRepository bedRepository;

	private HostelResponseDTO mapToHostelResponseDTO(Hostel hostel) {
		HostelResponseDTO response = new HostelResponseDTO();
		response.setHostelId(hostel.getHostelId());
		response.setName(hostel.getName());
		response.setLocation(hostel.getLocation());
		response.setFloors(hostel.getFloors().stream().map(floor -> {
			FloorResponseDTO floorResponseDTO = new FloorResponseDTO();
			floorResponseDTO.setFloorId(floor.getFloorId());
			floorResponseDTO.setFloorName(floor.getFloorName());
			floorResponseDTO.setFloorNumber(floor.getFloorNumber());
			floorResponseDTO.setRooms(floor.getRooms().stream().map(room -> {
				long total = room.getBeds().size();
				long occupied = room.getBeds().stream().filter(Bed::getIsOccupied).count();
				String status = "Empty";
				if (occupied == total && total > 0)
					status = "Full";
				else if (occupied > 0)
					status = "Available";
				RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
				roomResponseDTO.setRoomId(room.getRoomId());
				roomResponseDTO.setRoomNumber(room.getRoomNumber());
				roomResponseDTO.setRoomType(room.getRoomType().name());
				roomResponseDTO.setTotalBedCount(total);
				roomResponseDTO.setOccupiedBedCount(occupied);
				roomResponseDTO.setStatus(status);
				roomResponseDTO.setBeds(room.getBeds().stream().map(bed -> {
					BedResponseDTO bedResponseDTO = new BedResponseDTO();
					bedResponseDTO.setBedId(bed.getBedId());
					bedResponseDTO.setBedNumber(bed.getBedNumber());
					bedResponseDTO.setIsOccupied(bed.getIsOccupied());
					return bedResponseDTO;
				}).toList());
				return roomResponseDTO;
			}).toList());
			return floorResponseDTO;
		}).toList());
		return response;
	}

	@Override
	public HostelResponseDTO SaveConfiguration(Long ownerId, HostelConfigRequest hostelConfigRequest) {
		Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Owner not found"));

		Hostel hostel = hostelRepository.findByOwner(owner).orElse(new Hostel());

		hostel.setName(hostelConfigRequest.getName());
		hostel.setOwner(owner);
		hostel.setLocation(hostelConfigRequest.getLocation());
		hostel = hostelRepository.save(hostel);

		if (hostelConfigRequest.getFloors() != null) {
			for (FloorConfigRequest floorDTO : hostelConfigRequest.getFloors()) {
				if (floorDTO.getFloorNumber() == null)
					continue;
				Floor floor = floorRepository.findFloorByHostelAndFloorNumber(hostel, floorDTO.getFloorNumber())
						.orElse(new Floor());
				floor.setFloorName(floorDTO.getFloorName());
				floor.setFloorNumber(floorDTO.getFloorNumber());
				floor.setHostel(hostel);
				floor = floorRepository.save(floor);

				if (!hostel.getFloors().contains(floor)) {
					hostel.getFloors().add(floor);
				}
				if (floorDTO.getRooms() != null) {
					for (RoomConfigRequest roomDTO : floorDTO.getRooms()) {
						if (roomDTO.getRoomNumber() == null)
							continue;
						Room room = roomRepository.findRoomByFloorAndRoomNumber(floor, roomDTO.getRoomNumber())
								.orElse(new Room());
						room.setRoomNumber(roomDTO.getRoomNumber());

						RoomType newType = RoomType.valueOf(roomDTO.getRoomType().toUpperCase());

						if (room.getRoomId() == null || !newType.equals(room.getRoomType())) {
							room.setRoomNumber(roomDTO.getRoomNumber());
							room.setRoomType(newType);
							room.setFloor(floor);
							room = roomRepository.save(room);

							if (!floor.getRooms().contains(room)) {
								floor.getRooms().add(room);
							}
							syncBeds(room);
						}
					}
				}
			}
		}

		return mapToHostelResponseDTO(hostelRepository.findById(hostel.getHostelId()).get());

	}

	private HostelResponseDTO mapToFilteredHostelResponse(Hostel hostel, List<Room> filteredRooms) {
		HostelResponseDTO response = new HostelResponseDTO();
		response.setHostelId(hostel.getHostelId());
		response.setName(hostel.getName());

		// Group filtered rooms by floor ID for efficient lookup
		Map<Long, List<Room>> roomsByFloor = filteredRooms.stream()
				.collect(Collectors.groupingBy(r -> r.getFloor().getFloorId()));
		response.setFloors(hostel.getFloors().stream().filter(f -> roomsByFloor.containsKey(f.getFloorId())) // Only
																												// show
																												// floors
																												// with
																												// results
				.map(floor -> {
					FloorResponseDTO fDto = new FloorResponseDTO();
					fDto.setFloorId(floor.getFloorId());
					fDto.setFloorName(floor.getFloorName());
					fDto.setFloorNumber(floor.getFloorNumber());
					fDto.setRooms(roomsByFloor.get(floor.getFloorId()).stream().map(this::calculateRoomMetrics)
							.collect(Collectors.toList()));
					return fDto;
				}).collect(Collectors.toList()));

		return response;
	}

	private RoomResponseDTO calculateRoomMetrics(Room room) {
		long total = room.getBeds().size();
		long occupied = room.getBeds().stream().filter(Bed::getIsOccupied).count();
		String status = "Empty";
		if (occupied == total && total > 0)
			status = "Full";
		else if (occupied > 0)
			status = "Available";
		return RoomResponseDTO.builder().roomId(room.getRoomId()).roomNumber(room.getRoomNumber())
				.roomType(room.getRoomType().name()).totalBedCount(total).occupiedBedCount(occupied).status(status)
				.beds(room.getBeds().stream().map(bed -> {
					BedResponseDTO bDto = new BedResponseDTO();
					bDto.setBedId(bed.getBedId());
					bDto.setBedNumber(bed.getBedNumber());
					bDto.setIsOccupied(bed.getIsOccupied());
					return bDto;
				}).collect(Collectors.toList())).build();
	}

	private void syncBeds(Room room) {
		int targetCount = getBedCountFromType(room.getRoomType());
		List<Bed> existingBeds = room.getBeds();

		if (existingBeds.size() < targetCount) {
			int toAdd = targetCount - existingBeds.size();
			for (int i = 0; i < toAdd; i++) {
				Bed bed = new Bed();
				bed.setIsOccupied(false);
				bed.setRoom(room);
				int bedSeq = existingBeds.size() + i + 1;
				bed.setBedNumber("B" + bedSeq);
				bed = bedRepository.save(bed);

				room.getBeds().add(bed);
			}
		}
	}

	private int getBedCountFromType(RoomType roomType) {
		return switch (roomType) {
		case SINGLE -> 1;
		case DOUBLE -> 2;
		case TRIPLE -> 3;
		case FOUR_SHARING -> 4;
		case FIVE_SHARING -> 5;
		};
	}

	@Override
	public HostelResponseDTO getHostelStatus(Long ownerId, RoomFilterRequest filters) {
		Owner owner = ownerRepository.findById(ownerId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

		Hostel hostel = hostelRepository.findByOwner(owner)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel not configured"));

		RoomType type = null;
		if (filters.getRoomType() != null && !filters.getRoomType().isEmpty()) {
			try {
				type = RoomType.valueOf(filters.getRoomType().toUpperCase());
			} catch (IllegalArgumentException e) {
				// Log it or handle invalid room type strings
				type = null;
			}
		}

		// Shifted filtering to Repository layer for performance
		List<Room> filteredRooms = roomRepository.findFilteredRooms(hostel.getHostelId(), filters.getSearch(),
				type, filters.getStatus());
		return mapToFilteredHostelResponse(hostel, filteredRooms);
	}

	@Override
	public OccupancyStatsDTO getOccupancyStats(Long ownerId) {
		Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Owner not found"));
		Hostel hostel = hostelRepository.findByOwner(owner)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel Not Configured"));
		long totalRooms = roomRepository.countByHostelId(ownerId);
		long totalBeds = bedRepository.countByHostelId(hostel.getHostelId());
		long occupiedBeds = bedRepository.countOccupiedByHostel(hostel.getHostelId());

		double percentage = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0;

		return OccupancyStatsDTO.builder().totalBeds(totalBeds).totalRooms(totalRooms).occupiedBeds(occupiedBeds)
				.availableBeds(totalBeds - occupiedBeds).occupencyPercentage(Math.round(percentage * 10) / 10.0)
				.build();
	}

	@Override
	public HostelResponseDTO getHostelConfig(Long ownerId) {
		Owner owner = ownerRepository.findById(ownerId)
				.orElseThrow(() -> new UserNotFoundException("No Owmer Found with Id " + ownerId));
		Hostel hostel = hostelRepository.findByOwner(owner)
				.orElseThrow(() -> new ResourceNotFoundException("Pg Not configured for the owner"));

		return mapToHostelResponseDTO(hostel);
	}

}
