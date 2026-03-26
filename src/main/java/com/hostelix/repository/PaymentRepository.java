package com.hostelix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelix.enums.PaymentStatus;
import com.hostelix.model.Payment;
import com.hostelix.model.Resident;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	// 1. Search & Filter Payments
	@Query("SELECT p FROM Payment p " + "JOIN p.resident r " + "JOIN r.bed b " + "JOIN b.room rm "
			+ "WHERE r.hostel.hostelId = :hostelId " + "AND (:search IS NULL OR "
			+ "     LOWER(r.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     p.transactionId LIKE CONCAT('%', :search, '%') OR "
			+ "     rm.roomNumber LIKE CONCAT('%', :search, '%')) "
			+ "AND (:status IS NULL OR p.paymentStatus = :status) " + "AND (:month IS NULL OR p.billingMonth = :month) "
			+ "ORDER BY p.paymentDate DESC")
	Page<Payment> findFilteredPayments(@Param("hostelId") Long hostelId, @Param("search") String search,
			@Param("status") PaymentStatus status, @Param("month") String month, Pageable pageable);

	// 2. Sum of successful payments for a month
	@Query("SELECT SUM(p.amount) FROM Payment p " + "WHERE p.resident.hostel.hostelId = :hostelId "
			+ "AND p.billingMonth = :currentMonth " + "AND p.paymentStatus = 'SUCCESS'")
	Double sumMonthlyRevenue(@Param("hostelId") Long hostelId, @Param("currentMonth") String currentMonth);

	// 3. Count residents who paid this month
	@Query("SELECT COUNT(DISTINCT p.resident.residentId) FROM Payment p "
			+ "WHERE p.resident.hostel.hostelId = :hostelId " + "AND p.billingMonth = :currentMonth "
			+ "AND p.paymentStatus = 'SUCCESS'")
	Long countPaidResidents(@Param("hostelId") Long hostelId, @Param("currentMonth") String currentMonth);

	boolean existsByResidentAndBillingMonth(Resident resident, String billingMonth);

	@Query("SELECT p FROM Payment p WHERE p.resident.residentId = :residentId "
			+ "AND p.billingMonth = :billingMonth AND p.paymentStatus = :status")
	Optional<Payment> findByResidentResidentIdAndBillingMonthAndPaymentStatus(@Param("residentId") Long residentId,
			@Param("billingMonth") String billingMonth, @Param("status") PaymentStatus status);

	@Query("SELECT p FROM Payment p WHERE p.resident.residentId = :residentId " + "AND p.billingMonth = :billingMonth")
	Optional<Payment> findByResidentIdAndBillingMonth(Long residentId, String billingMonth);

	List<Payment> findByResidentResidentIdOrderByCreatedAtDesc(Long residentId);
	
}
