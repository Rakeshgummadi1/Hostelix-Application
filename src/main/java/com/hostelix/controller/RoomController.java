package com.hostelix.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.RoomResponse;
import com.hostelix.model.Room;
import com.hostelix.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hostels/{hostelId}/rooms")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoomController {
	private final RoomService roomService;

	// Call this FIRST to populate the Room dropdown
	@GetMapping("/available")
	public ResponseEntity<List<RoomResponse>> getAvailableRoomsForHostel(@PathVariable Long hostelId) {
		return ResponseEntity.ok(roomService.getRoomsWithAvailableBeds(hostelId));
	}
	
	
}
