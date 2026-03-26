package com.hostelix.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "residents")
@SQLDelete(sql = "UPDATE residents SET is_deleted = true WHERE resident_id=?")
public class Resident extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long residentId;
    private String fullName;
    private String phoneNumber;
    private String email;          // Personal email of resident
    private String aadharNumber;   // For ID verification
    private Double monthlyFees;
    private LocalDate joiningDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id") // Foreign key column in 'residents' table
    private Hostel hostel;
    
    @OneToOne(mappedBy = "resident", cascade = CascadeType.ALL)
    private Bed bed;
    
    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();
}
