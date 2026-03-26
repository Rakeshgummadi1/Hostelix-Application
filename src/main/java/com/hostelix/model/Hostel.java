package com.hostelix.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "hostels")
@SQLDelete(sql = "UPDATE hostels SET is_deleted = true WHERE hostel_id=?")
public class Hostel extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hostelId;
    private String name;
    private String location;
    
    // mappedBy: Points to the 'hostel' field in the Floor entity
    // cascade: Deleting a hostel will delete all its floors and nested entities
    
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("floorNumber ASC")
    private List<Floor> floors=new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
    		name="hostel_amenities",
    		joinColumns = @JoinColumn(name = "hostel_id"),
    		inverseJoinColumns = @JoinColumn(name = "amenity_id")
    		
    		)
    private Set<Amenity> amenities=new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;
    
    // NEW: Bi-directional link from Hostel to its Residents
    // If you delete a hostel, cascade rule deletes its residents
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residents = new ArrayList<>(); 
}
