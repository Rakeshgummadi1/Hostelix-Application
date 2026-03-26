package com.hostelix.service.implementation;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.hostelix.dto.ResidentProfileDTO;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.model.Resident;
import com.hostelix.model.Room;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.ResidentProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResidentProfileServiceImpl implements ResidentProfileService {
    private final ResidentRepository residentRepository;
    @Override
    public ResidentProfileDTO getResidentProfile(Long residentId) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resident not found with ID: " + residentId));
        Room room = resident.getBed() != null 
                ? resident.getBed().getRoom() 
                : null;
        return ResidentProfileDTO.builder()
                .residentId(resident.getResidentId())
                .fullName(resident.getFullName())
                .email(resident.getEmail())
                .phone(resident.getPhoneNumber())
                .aadharNumber(maskAadhar(resident.getAadharNumber()))
                // Stay Info
                .hostelName(resident.getHostel() != null 
                        ? resident.getHostel().getName() : null)
                .hostelLocation(resident.getHostel() != null 
                        ? resident.getHostel().getLocation() : null)
                .roomNumber(room != null 
                        ? room.getRoomNumber() : null)
                .floorName(room != null && room.getFloor() != null 
                        ? room.getFloor().getFloorName() : null)
                .sharingType(room != null 
                        ? room.getRoomType().name() : null)
                .monthlyRent(resident.getMonthlyFees())
                .joiningDate(resident.getJoiningDate() != null
                        ? resident.getJoiningDate()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                        : null)
                .build();
    }
    /**
     * Masks Aadhar number for privacy: "1234 5678 9012" → "XXXX XXXX 9012"
     */
    private String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() < 4) return "XXXX XXXX XXXX";
        return "XXXX XXXX " + aadhar.substring(aadhar.length() - 4);
    }
}
