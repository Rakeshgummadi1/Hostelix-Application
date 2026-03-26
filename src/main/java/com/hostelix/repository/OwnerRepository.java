package com.hostelix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hostelix.model.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long>{

	Optional<Owner> findByEmail(String email);
	
	boolean existsByEmail(String email);
}
