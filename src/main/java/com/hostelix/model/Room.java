package com.hostelix.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.hostelix.enums.RoomType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
@SQLDelete(sql = "UPDATE rooms SET is_deleted = true WHERE room_id=?")
public class Room extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;
	private String roomNumber;
	@Enumerated(EnumType.STRING)
	private RoomType roomType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "floor_id")
	private Floor floor;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bed> beds = new ArrayList<>();
}
