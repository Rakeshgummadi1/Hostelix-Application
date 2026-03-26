package com.hostelix.model;

import org.hibernate.annotations.SQLDelete;

import com.hostelix.enums.ComplaintCategory;
import com.hostelix.enums.ComplaintPriority;
import com.hostelix.enums.ComplaintStatus;
import com.hostelix.enums.ComplaintType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "complaints")
@SQLDelete(sql = "UPDATE complaints SET is_deleted = true WHERE complaint_id=?")
public class Complaint extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(unique = true, nullable = false)
    private String ticketId; // User-friendly ID for tracking

    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    private ComplaintPriority priority;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    private String resolutionNotes;

    // Owning side: Linked to the resident who raised it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    // ADDED: Crucial for owner-side hostel filtering
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;
}

