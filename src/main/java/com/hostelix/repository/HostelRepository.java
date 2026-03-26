package com.hostelix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelix.model.Hostel;
import com.hostelix.model.Owner;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long>{

	@Query("SELECT hostel FROM Hostel hostel WHERE hostel.owner = :owner")
	Optional<Hostel> findByOwner(@Param("owner") Owner owner);
}
