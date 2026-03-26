package com.hostelix.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "floors")
@SQLDelete(sql = "UPDATE floors SET is_deleted = true WHERE floor_id=?")
public class Floor extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long floorId;
	private String floorName;
	private Integer floorNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hostel_id")
	private Hostel hostel;

	@OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Room> rooms = new ArrayList<>();
}
