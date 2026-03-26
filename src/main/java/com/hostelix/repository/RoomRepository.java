package com.hostelix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelix.enums.RoomType;
import com.hostelix.model.Floor;
import com.hostelix.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	@Query("SELECT room FROM Room room where room.floor = :floor AND room.roomNumber = :roomNumber")
	Optional<Room> findRoomByFloorAndRoomNumber(@Param("floor") Floor floor, @Param("roomNumber") String roomNumber);
	@Query("SELECT COUNT(room) FROM Room room where room.floor.hostel.hostelId=:hostelId")
	long countByHostelId(@Param("hostelId") Long hostelId);
	
	 @Query("SELECT r FROM Room r JOIN r.floor f JOIN f.hostel h " +
	           "WHERE h.hostelId = :hostelId " +
	           "AND (:search IS NULL OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
	           "AND (:roomType IS NULL OR r.roomType = :roomType) " +
	           "AND (:status IS NULL " +
	           "     OR (:status = 'Empty' AND (SELECT count(b) FROM Bed b WHERE b.room = r AND b.isOccupied = true) = 0) " +
	           "     OR (:status = 'Available' AND (SELECT count(b) FROM Bed b WHERE b.room = r AND b.isOccupied = true) < (SELECT count(allB) FROM Bed allB WHERE allB.room = r)) " +
	           "     OR (:status = 'Full' AND (SELECT count(b) FROM Bed b WHERE b.room = r AND b.isOccupied = true) = (SELECT count(allB) FROM Bed allB WHERE allB.room = r) AND (SELECT count(anyB) FROM Bed anyB WHERE anyB.room = r) > 0) " +
	           ")")
	    List<Room> findFilteredRooms(
	            @Param("hostelId") Long hostelId, 
	            @Param("search") String search, 
	            @Param("roomType") RoomType roomType, 
	            @Param("status") String status);
	 
	 @Query("SELECT DISTINCT r FROM Room r JOIN r.beds b WHERE r.floor.hostel.hostelId = :hostelId AND b.isOccupied = false")
	    List<Room> findAvailableRoomsByHostelId(@Param("hostelId") Long hostelId);
}
