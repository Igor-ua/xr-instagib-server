package com.newerth.core.entities;


import com.fasterxml.jackson.annotation.JsonView;
import com.newerth.core.View;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Component
@Entity
@Table(name = "map_stats")
public class MapStats implements Serializable {

	@Id
	@GeneratedValue
	@JsonView(View.Summary.class)
	@Column(name = "id")
	private long id;

	@Column(name = "map_name")
	@JsonView(View.Summary.class)
	private String mapName;

	@Column(name = "team_one_score")
	@JsonView(View.Summary.class)
	private int teamOneScore;

	@Column(name = "team_two_score")
	@JsonView(View.Summary.class)
	private int teamTwoScore;

	@Column(name = "map_ts")
	@JsonView(View.Summary.class)
	private Date mapTimeStamp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getTeamOneScore() {
		return teamOneScore;
	}

	public void setTeamOneScore(int teamOneScore) {
		this.teamOneScore = teamOneScore;
	}

	public int getTeamTwoScore() {
		return teamTwoScore;
	}

	public void setTeamTwoScore(int teamTwoScore) {
		this.teamTwoScore = teamTwoScore;
	}

	public Date getMapTimeStamp() {
		return mapTimeStamp;
	}

	public void setMapTimeStamp(Date mapTimeStamp) {
		this.mapTimeStamp = mapTimeStamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapStats mapStats = (MapStats) o;

		return id == mapStats.id && (mapName != null ? mapName.equals(mapStats.mapName) : mapStats.mapName == null) &&
				(mapTimeStamp != null ? mapTimeStamp.equals(mapStats.mapTimeStamp) : mapStats.mapTimeStamp == null);
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (mapName != null ? mapName.hashCode() : 0);
		result = 31 * result + (mapTimeStamp != null ? mapTimeStamp.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MapStats{" +
				"id=" + id +
				", mapName='" + mapName + '\'' +
				", teamOneScore=" + teamOneScore +
				", teamTwoScore=" + teamTwoScore +
				", mapTimeStamp=" + mapTimeStamp +
				'}';
	}
}