package com.hostelix.service.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hostelix.client.OtpClient;
import com.hostelix.dto.LoginResponse;
import com.hostelix.dto.OtpSendRequest;
import com.hostelix.dto.OtpVerifyRequest;
import com.hostelix.dto.ResidentListResponse;
import com.hostelix.dto.ResidentLoginRequest;
import com.hostelix.dto.ResidentRegistrationRequest;
import com.hostelix.dto.ResidentUpdateRequest;
import com.hostelix.enums.PaymentStatus;
import com.hostelix.exceptions.DuplicateResourceException;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.exceptions.UserNotFoundException;
import com.hostelix.exceptions.ValidationException;
import com.hostelix.model.Bed;
import com.hostelix.model.Hostel;
import com.hostelix.model.Payment;
import com.hostelix.model.Resident;
import com.hostelix.repository.BedRepository;
import com.hostelix.repository.HostelRepository;
import com.hostelix.repository.PaymentRepository;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.ResidentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResidentServiceImplementation implements ResidentService {

	private final ResidentRepository residentRepository;
	private final OtpClient otpClient;
	private final BedRepository bedRepository;
	private final HostelRepository hostelRepository;
	private final PaymentRepository paymentRepository;

	@Override
	public LoginResponse login(ResidentLoginRequest loginRequest) {
		Resident resident = residentRepository.findByPhoneNumberAndIsDeletedFalse(loginRequest.getPhoneNumber())
				.orElseThrow(() -> new UserNotFoundException(
						"No Active Resident Found with this phone Number :" + loginRequest.getPhoneNumber()));
		return new LoginResponse("RESIDENT", mapToResidentListResponse(resident));
	}

	@Override
	public void sendOtp(String phoneNumber) {
		// Step 1: Check resident exists first
		residentRepository.findByPhoneNumberAndIsDeletedFalse(phoneNumber).orElseThrow(
				() -> new UserNotFoundException("No Active Resident Found with this phone Number :" + phoneNumber));

		// Step 2: Ask notification-service to send OTP
		otpClient.sendOtp(new OtpSendRequest(phoneNumber));

	}

	@Override
	public LoginResponse verifyOtpAndLogin(String phoneNumber, String otp) {
		// Step 1: Verify OTP via notification-service
		otpClient.verifyOtp(new OtpVerifyRequest(phoneNumber, otp));
		// Step 2: Reuse existing login logic
		return login(new ResidentLoginRequest(phoneNumber));
	}

	@Override
	@Transactional
	public ResidentListResponse onboardResident(ResidentRegistrationRequest request) {
	    // 1. Validate uniqueness
	    if (residentRepository.checkPhoneNumberExists(request.getPhoneNumber())) {
	        throw new DuplicateResourceException("Resident with this phone number already exists.");
	    }
	    if (residentRepository.checkAadharExists(request.getAadharNumber())) {
	        throw new DuplicateResourceException("Resident with this Aadhar already exists.");
	    }
	    // 2. Validate Bed constraints
	    Bed bed = bedRepository.findById(request.getBedId())
	            .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));
	    if (Boolean.TRUE.equals(bed.getIsOccupied())) {
	        throw new ValidationException("This bed is already occupied!");
	    }
	    // 3. Fetch Hostel
	    Hostel hostel = hostelRepository.findById(request.getHostelId())
	            .orElseThrow(() -> new ResourceNotFoundException("Hostel not found"));
	    // 4. Create Entity
	    Resident resident = new Resident();
	    resident.setFullName(request.getFullName());
	    resident.setEmail(request.getEmail());
	    resident.setPhoneNumber(request.getPhoneNumber());
	    resident.setAadharNumber(request.getAadharNumber());
	    resident.setMonthlyFees(request.getMonthlyFees());
	    resident.setJoiningDate(LocalDate.now());
	    
	    // 5. Link specific relationships
	    resident.setBed(bed);
	    resident.setHostel(hostel);
	    bed.setResident(resident);
	    bed.setIsOccupied(true);
	    // 6. Save Resident and Bed
	    residentRepository.save(resident);
	    bedRepository.save(bed);
	    // --- NEW LOGIC: Generate 1st Month Pending Payment ---
	    createInitialPendingPayment(resident);
	    // -----------------------------------------------------
	    return mapToResidentListResponse(resident);
	}
	/**
	 * Helper method to create the first PENDING record
	 */
	private void createInitialPendingPayment(Resident resident) {
	    String currentMonth = String.format("%02d-%d", 
	        LocalDate.now().getMonthValue(), 
	        LocalDate.now().getYear());
	    Payment firstMonthDue = new Payment();
	    firstMonthDue.setResident(resident);
	    firstMonthDue.setAmount(resident.getMonthlyFees());
	    firstMonthDue.setBillingMonth(currentMonth);
	    firstMonthDue.setPaymentStatus(PaymentStatus.PENDING);
	    firstMonthDue.setPaymentDate(LocalDate.now()); // Set today as the "Due Created Date"
	    
	    // Inject PaymentRepository into this service to call .save()
	    paymentRepository.save(firstMonthDue);
	}

	@Override
	public Page<ResidentListResponse> getResidents(Long hostelId, String search, Integer sharing, int page, int size) {
		// TODO Auto-generated method stub
		// 1. Create Pagination request (0-indexed page)
		Pageable pageable = PageRequest.of(page, size);
		// 2. Fetch filtered & paginated page
		Page<Resident> residentPage = residentRepository.findFilteredResidents(hostelId, search, sharing, pageable);
		// 3. Process and Map to DTO
		return residentPage.map(resident -> {
			// Count total beds in the room to derive "Sharing Type"
			int sharingCapacity = resident.getBed().getRoom().getBeds().size();
			return ResidentListResponse.builder().residentId(resident.getResidentId()).fullName(resident.getFullName())
					.phoneNumber(resident.getPhoneNumber()).email(resident.getEmail())
					.monthlyFees(resident.getMonthlyFees())
					.aadharNumber(resident.getAadharNumber())
					// Traverse relationships safely
					.roomNumber(resident.getBed().getRoom().getRoomNumber()).bedNumber(resident.getBed().getBedNumber())
					.sharing(sharingCapacity).build();
		});

	}

	@Override
	public ResidentListResponse getResidentById(Long residentId) {
		Resident resident = residentRepository.findById(residentId)
				.orElseThrow(() -> new ResourceNotFoundException("Resident not found"));
		return mapToResidentListResponse(resident);
	}

	private ResidentListResponse mapToResidentListResponse(Resident resident) {
		// Count total beds in the room to derive "Sharing Type"
		int sharingCapacity = 0;
		String roomNo = "N/A";
		String bedNo = "N/A";
		if (resident.getBed() != null) {
			bedNo = resident.getBed().getBedNumber();
			if (resident.getBed().getRoom() != null) {
				roomNo = resident.getBed().getRoom().getRoomNumber();
				sharingCapacity = resident.getBed().getRoom().getBeds().size();
			}
		}
		return ResidentListResponse.builder().residentId(resident.getResidentId()).fullName(resident.getFullName())
				.phoneNumber(resident.getPhoneNumber()).email(resident.getEmail())
				.aadharNumber(resident.getAadharNumber()).monthlyFees(resident.getMonthlyFees()).roomNumber(roomNo)
				.bedNumber(bedNo).sharing(sharingCapacity).joiningDate(resident.getJoiningDate()).build();
	}

	@Override
	@Transactional
	public Resident updateResident(Long residentId, ResidentUpdateRequest request) {
		Resident resident = residentRepository.findById(residentId)
				.orElseThrow(() -> new ResourceNotFoundException("Resident not found"));
		resident.setFullName(request.getFullName());
		resident.setEmail(request.getEmail());
		resident.setPhoneNumber(request.getPhoneNumber());
		resident.setAadharNumber(request.getAadharNumber());
		resident.setMonthlyFees(request.getMonthlyFees());
		return residentRepository.save(resident);
	}

	@Override
	@Transactional
	public void vacateResident(Long residentId) {
		Resident resident = residentRepository.findById(residentId)
				.orElseThrow(() -> new ResourceNotFoundException("Resident not found"));
		// Unlink the bed
		if (resident.getBed() != null) {
			Bed bed = resident.getBed();
			bed.setResident(null);
			bed.setIsOccupied(false);
			bedRepository.save(bed);
		}
		// Soft delete the resident
		residentRepository.delete(resident);
	}
	
	public LoginResponse getById(String phoneNumber) {
		Resident byPhoneNumberAndIsDeletedFalse = residentRepository.findByPhoneNumberAndIsDeletedFalse(phoneNumber).get();
		
		return new LoginResponse("Resident", mapToResidentListResponse(byPhoneNumberAndIsDeletedFalse));
	}
	
	@Override
	public List<ResidentListResponse> searchResidents(Long hostelId, Long floorId, Long roomId) {
	    return residentRepository.findByHostelAndFloorAndRoom(hostelId, floorId, roomId)
	            .stream()
	            .map(this::mapToResidentListResponse)
	            .collect(Collectors.toList());
	}

}
