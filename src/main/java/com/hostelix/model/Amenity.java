package com.hostelix.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "amenities")
@SQLDelete(sql = "UPDATE amenities SET is_deleted = true WHERE amenity_id=?")
public class Amenity extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long amenityId;
	private String amenityName;
	
	@ManyToMany(mappedBy = "amenities")
	private Set<Hostel> hostels=new HashSet<>();
}
