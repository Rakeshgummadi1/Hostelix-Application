package com.hostelix.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hostelix.dto.RoomResponse;
import com.hostelix.model.Room;
import com.hostelix.repository.RoomRepository;
import com.hostelix.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImplementation implements RoomService {
	private final RoomRepository roomRepository;

	@Override
	public List<RoomResponse> getRoomsWithAvailableBeds(Long hostelId) {
		return mapToResponse(roomRepository.findAvailableRoomsByHostelId(hostelId));
	}

	private List<RoomResponse> mapToResponse(List<Room> rooms) {
		List<RoomResponse> response = new ArrayList<>();
		for (Room room : rooms) {
			response.add(new RoomResponse(room.getRoomId(), room.getRoomNumber(), room.getRoomType()));
		}
		return response;
	}
}
