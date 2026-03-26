package com.hostelix.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hostelix.enums.PaymentStatus;
import com.hostelix.repository.PaymentRepository;
import com.hostelix.repository.ResidentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyBillingJob {

    private final ResidentRepository residentRepository;
    private final PaymentRepository paymentRepository;

    @Scheduled(cron = "0 44 15 * * *") // Runs at 1 AM every day
    @Transactional
    public void generateMonthlyDues() {
        int todayDay = LocalDate.now().getDayOfMonth();
        String currentMonth = String.format("%02d-%d", 
            LocalDate.now().getMonthValue(), 
            LocalDate.now().getYear());

        // 1. Find all active residents whose anniversary day is today or has passed
        // This 'Catch-up' logic ensures that if the app was down on their specific day,
        // it bills them as soon as it's active again.
        List<Resident> residents = residentRepository.findByIsDeletedFalse();

        for (Resident resident : residents) {
            int joiningDay = resident.getJoiningDate().getDayOfMonth();
            
            // Logic: If today is the anniversary or past it, and no record exists for this month
            if (todayDay >= joiningDay) {
                boolean alreadyBilled = paymentRepository.existsByResidentAndBillingMonth(resident, currentMonth);
                
                if (!alreadyBilled) {
                    Payment due = new Payment();
                    due.setResident(resident);
                    due.setAmount(resident.getMonthlyFees());
                    due.setBillingMonth(currentMonth);
                    due.setPaymentStatus(PaymentStatus.PENDING);
                    due.setPaymentDate(LocalDate.now());
                    paymentRepository.save(due);
                    log.info("Billing Catch-up: Generated pending due for resident: {} for month: {}", resident.getFullName(), currentMonth);
                }
            }
        }
    }
}
