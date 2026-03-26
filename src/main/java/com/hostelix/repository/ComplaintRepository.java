package com.hostelix.repository;

import com.hostelix.dto.PendingResidentDTO;
import com.hostelix.enums.ComplaintCategory;
import com.hostelix.enums.ComplaintStatus;
import com.hostelix.enums.ComplaintType;
import com.hostelix.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
	@Query("SELECT c FROM Complaint c WHERE c.hostel.hostelId = :hostelId "
			+ "AND (:status IS NULL OR c.status = :status) " + "AND (:category IS NULL OR c.category = :category) "
			+ "AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.ticketId) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<Complaint> findAllWithFilters(Long hostelId, ComplaintStatus status, ComplaintCategory category, String search,
			Pageable pageable);

	@Query("SELECT c FROM Complaint c WHERE c.resident.residentId = :residentId "
			+ "AND (:status IS NULL OR c.status = :status)")
	Page<Complaint> findByResidentIdAndStatus(Long residentId, ComplaintStatus status, Pageable pageable);

	@Query("SELECT new com.hostelix.dto.PendingResidentDTO(c.resident.residentId, c.resident.fullName, c.resident.bed.room.roomNumber, COUNT(c)) "
			+ "FROM Complaint c " + "WHERE c.hostel.hostelId = :hostelId AND c.status != 'RESOLVED' "
			+ "GROUP BY c.resident.residentId, c.resident.fullName, c.resident.bed.room.roomNumber")
	List<PendingResidentDTO> findResidentsWithPendingComplaints(Long hostelId);

	long countByHostelHostelIdAndStatus(Long hostelId, ComplaintStatus status);
	
	long countByResidentResidentIdAndStatusNot(Long residentId, ComplaintStatus status);
}
