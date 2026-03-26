package com.hostelix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelix.model.Bed;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

	@Query("SELECT COUNT(bed) FROM Bed bed WHERE bed.room.floor.hostel.hostelId=:hostelId")
	long countByHostelId(@Param("hostelId") long hostelId);
	
	@Query("SELECT COUNT(bed) FROM Bed bed WHERE bed.room.floor.hostel.hostelId=:hostelId AND bed.isOccupied = true")
	long countOccupiedByHostel(Long hostelId);
	
	 @Query("SELECT b FROM Bed b WHERE b.room.roomId = :roomId AND b.isOccupied = false")
	    List<Bed> findAvailableBedsByRoomId(@Param("roomId") Long roomId);
}
