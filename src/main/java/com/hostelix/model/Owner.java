package com.hostelix.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "owners")
@SQLDelete(sql = "UPDATE owners SET is_deleted = true WHERE owner_id=?")
public class Owner extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ownerId;
	
	@Column(nullable = false)
	private String ownerName;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<Hostel> hostels=new ArrayList<>();
}
