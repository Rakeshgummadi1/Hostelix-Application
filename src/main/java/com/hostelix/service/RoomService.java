package com.hostelix.service;

import java.util.List;

import com.hostelix.dto.RoomResponse;

public interface RoomService {
    List<RoomResponse> getRoomsWithAvailableBeds(Long hostelId);
}
