package com.hostelix.service;

import java.util.List;

import com.hostelix.dto.BedResponseDTO;

public interface BedService {
    List<BedResponseDTO> getAvailableBedsForRoom(Long roomId);
}
