package com.hostelix.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hostelix.dto.BedResponseDTO;
import com.hostelix.model.Bed;
import com.hostelix.repository.BedRepository;
import com.hostelix.service.BedService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BedServiceImplementation implements BedService {
    private final BedRepository bedRepository;
    @Override
    public List<BedResponseDTO> getAvailableBedsForRoom(Long roomId) {
        return mapToBedResponse(bedRepository.findAvailableBedsByRoomId(roomId));
    }
    
    private List<BedResponseDTO> mapToBedResponse(List<Bed> beds){
    	List<BedResponseDTO> response = new ArrayList<>();
    	for(Bed bed : beds) {
    		response.add(new BedResponseDTO(bed.getBedId(), bed.getBedNumber(), bed.getIsOccupied()));
    	}
    	return response;
    }
}
