package com.hostelix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.BedResponseDTO;
import com.hostelix.model.Bed;
import com.hostelix.service.BedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms/{roomId}/beds")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BedController {
	private final BedService bedService;

	// Call this SECOND (after user selects a room) to populate the Bed dropdown
	@GetMapping("/available")
	public ResponseEntity<List<BedResponseDTO>> getAvailableBedsForRoom(@PathVariable Long roomId) {
		return ResponseEntity.ok(bedService.getAvailableBedsForRoom(roomId));
	}
}
