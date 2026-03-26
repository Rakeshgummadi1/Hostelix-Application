package com.hostelix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hostelix.model.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

	// For Owner: See all announcements for their hostel
	List<Announcement> findByHostelHostelIdOrderByCreatedAtDesc(Long hostelId);

	// For Resident: See global OR their floor OR their room OR direct to them
	@Query("SELECT a FROM Announcement a WHERE a.hostel.hostelId = :hostelId " + "AND (a.target = 'GLOBAL' "
			+ "OR (a.target = 'FLOOR' AND a.floorId = :floorId) " + "OR (a.target = 'ROOM' AND a.roomId = :roomId) "
			+ "OR (a.target = 'RESIDENT' AND a.residentId = :residentId)) " + "ORDER BY a.createdAt DESC")
	List<Announcement> findRelevantAnnouncements(Long hostelId, Long floorId, Long roomId, Long residentId);
}
