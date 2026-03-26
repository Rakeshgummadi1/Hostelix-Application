package com.hostelix.service.implementation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hostelix.dto.AnnouncementDTO;
import com.hostelix.dto.ComplaintSummaryDTO;
import com.hostelix.dto.PaymentSummaryDTO;
import com.hostelix.dto.ResidentDashboardDTO;
import com.hostelix.dto.RoommateDTO;
import com.hostelix.dto.StayInfoDTO;
import com.hostelix.enums.ComplaintStatus;
import com.hostelix.enums.PaymentStatus;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.model.Announcement;
import com.hostelix.model.Payment;
import com.hostelix.model.Resident;
import com.hostelix.model.Room;
import com.hostelix.repository.AnnouncementRepository;
import com.hostelix.repository.ComplaintRepository;
import com.hostelix.repository.PaymentRepository;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.ResidentDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResidentDashboardServiceImpl implements ResidentDashboardService {
	private final ResidentRepository residentRepository;
	private final PaymentRepository paymentRepository;
	private final ComplaintRepository complaintRepository;
	private final AnnouncementRepository announcementRepository;

	@Override
	public ResidentDashboardDTO getDashboardSummary(Long residentId) {
		Resident resident = residentRepository.findById(residentId)
				.orElseThrow(() -> new ResourceNotFoundException("Resident not found"));
		return ResidentDashboardDTO.builder().stayInfo(buildStayInfo(resident))
				.paymentSummary(buildPaymentSummary(resident)).complaintSummary(buildComplaintSummary(resident))
				.latestAnnouncement(getLatestRealAnnouncement(resident)).roommates(getRoommates(resident)).build();
	}

	private StayInfoDTO buildStayInfo(Resident r) {
		Room room = r.getBed().getRoom();
		return StayInfoDTO.builder().roomNumber(room.getRoomNumber()).floorName(room.getFloor().getFloorName())
				// FIXED: Changed getSharingType() to getRoomType()
				.sharingType(room.getRoomType() + "-Sharing").hostelName(r.getHostel().getName()).build();
	}

	private PaymentSummaryDTO buildPaymentSummary(Resident r) {
		String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-YYYY"));
		Optional<Payment> currentPayment = paymentRepository.findByResidentIdAndBillingMonth(r.getResidentId(),
				currentMonth);
		double due = currentPayment.isEmpty() ? r.getMonthlyFees() : 0.0;
		String status = currentPayment.isPresent() && currentPayment.get().getPaymentStatus() == PaymentStatus.PAID
				? "PAID"
				: "PENDING";
		return PaymentSummaryDTO.builder().amountDue(due).status(status).dueDate("05 " + currentMonth)
				.billingMonth(currentMonth).build();
	}

	private ComplaintSummaryDTO buildComplaintSummary(Resident r) {
		long activeCount = complaintRepository.countByResidentResidentIdAndStatusNot(r.getResidentId(),
				ComplaintStatus.RESOLVED);

		String latestStatus = "NONE";
		if (activeCount > 0) {
			latestStatus = "IN_PROGRESS";
		}
		return ComplaintSummaryDTO.builder().activeCount(activeCount).latestTicketStatus(latestStatus).build();
	}

	private List<RoommateDTO> getRoommates(Resident r) {
		Room room = r.getBed().getRoom();
		return room.getBeds().stream().filter(
				bed -> bed.getResident() != null && !bed.getResident().getResidentId().equals(r.getResidentId()))
				.map(bed -> RoommateDTO.builder().name(bed.getResident().getFullName())
						.id("RS-" + bed.getResident().getResidentId()).build())
				.collect(Collectors.toList());
	}

	private AnnouncementDTO getLatestRealAnnouncement(Resident r) {
		Long hostelId = r.getHostel().getHostelId();
		Long floorId = r.getBed().getRoom().getFloor().getFloorId();
		Long roomId = r.getBed().getRoom().getRoomId();
		// Fetch using the targeted query in AnnouncementRepository
		List<Announcement> announcements = announcementRepository.findRelevantAnnouncements(hostelId, floorId, roomId,
				r.getResidentId());
		if (announcements.isEmpty()) {
			return null; // Frontend handles null by showing "No recent updates"
		}
		Announcement latest = announcements.get(0);

		// Format the date for the UI
		String formattedDate = latest.getCreatedAt().toLocalDate().toString();
		return AnnouncementDTO.builder().title(latest.getTitle()).description(latest.getDescription())
				.type(latest.getType().toString()).date(formattedDate).build();
	}
}
