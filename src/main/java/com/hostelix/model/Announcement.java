package com.hostelix.model;

import com.hostelix.enums.AnnouncementTarget;
import com.hostelix.enums.AnnouncementType;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "announcements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long announcementId;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false, length = 2000)
	private String description;
	@Enumerated(EnumType.STRING)
	private AnnouncementType type;
	@Enumerated(EnumType.STRING)
	private AnnouncementTarget target;
	private Long floorId; // Used if target is FLOOR
	private Long roomId; // Used if target is ROOM
	private Long residentId; // Used if target is RESIDENT
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hostel_id", nullable = false)
	private Hostel hostel;
}
