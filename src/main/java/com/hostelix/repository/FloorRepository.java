package com.hostelix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelix.model.Floor;
import com.hostelix.model.Hostel;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

	@Query("SELECT floor FROM Floor floor WHERE floor.hostel = :hostel AND floor.floorNumber = :floorNumber")
	Optional<Floor> findFloorByHostelAndFloorNumber(@Param("hostel") Hostel hostel,
			@Param("floorNumber") Integer floorNumber);
}
