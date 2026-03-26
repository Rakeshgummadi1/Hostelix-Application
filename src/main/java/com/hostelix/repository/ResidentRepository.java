package com.hostelix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelix.model.Payment;
import com.hostelix.model.Resident;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

	Optional<Resident> findByPhoneNumberAndIsDeletedFalse(String phoneNumber);

	// 1. Validation Queries
	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Resident r WHERE r.phoneNumber = :phoneNumber AND r.isDeleted = false")
	boolean checkPhoneNumberExists(@Param("phoneNumber") String phoneNumber);

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Resident r WHERE r.aadharNumber = :aadharNumber AND r.isDeleted = false")
	boolean checkAadharExists(@Param("aadharNumber") String aadharNumber);

	// 2. Fetch & Search/Filter Query
	@Query("SELECT r FROM Resident r " + "JOIN r.bed b " + "JOIN b.room rm " + "JOIN rm.floor f " + "JOIN f.hostel h "
			+ "WHERE h.hostelId = :hostelId " + "AND r.isDeleted = false " + "AND (:search IS NULL OR "
			+ "     LOWER(r.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     r.phoneNumber LIKE CONCAT('%', :search, '%') OR "
			+ "     LOWER(rm.roomNumber) LIKE LOWER(CONCAT('%', :search, '%'))" + ") " + "AND (:sharing IS NULL OR "
			+ "     (SELECT COUNT(bd) FROM Bed bd WHERE bd.room = rm) = :sharing" + ")")
	org.springframework.data.domain.Page<Resident> findFilteredResidents(@Param("hostelId") Long hostelId,
			@Param("search") String search, @Param("sharing") Integer sharing, Pageable pageable);

	// Logic: Sum of monthlyFees for all active residents
	// who do NOT have a SUCCESS payment in the current billing month.
	@Query("SELECT SUM(r.monthlyFees) FROM Resident r " + "WHERE r.hostel.hostelId = :hostelId "
			+ "AND r.isDeleted = false " + "AND r.residentId NOT IN ("
			+ "    SELECT p.resident.residentId FROM Payment p " + "    WHERE p.billingMonth = :currentMonth "
			+ "    AND p.paymentStatus = 'SUCCESS'" + ")")
	Double calculatePendingDues(@Param("hostelId") Long hostelId, @Param("currentMonth") String currentMonth);

	@Query("SELECT r FROM Resident r WHERE DAY(r.joiningDate) = :day AND r.isDeleted = false")
	List<Resident> findByJoiningDay(@Param("day") int day);

	List<Resident> findByIsDeletedFalse();

	@Query("SELECT r FROM Resident r WHERE r.hostel.hostelId = :hostelId "
			+ "AND (:floorId IS NULL OR r.bed.room.floor.floorId = :floorId) "
			+ "AND (:roomId IS NULL OR r.bed.room.roomId = :roomId) " + "AND r.isDeleted = false")
	List<Resident> findByHostelAndFloorAndRoom(@Param("hostelId") Long hostelId, @Param("floorId") Long floorId,
			@Param("roomId") Long roomId);
}
