package com.newerth.core.entities;

import com.fasterxml.jackson.annotation.JsonView;
import com.newerth.core.View;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

@Component
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "accuracy_stats")
public class AccuracyStats implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonView(View.Summary.class)
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "player_uid", referencedColumnName = "uid", nullable = false)
	@JsonView(View.Summary.class)
	private Player player;

	@Column(name = "shots")
	@JsonView(View.Summary.class)
	private int shots;

	@Column(name = "hits")
	@JsonView(View.Summary.class)
	private int hits;

	@Column(name = "accuracy_percent")
	@JsonView(View.Summary.class)
	@Min(0)
	@Max(100)
	private int accuracyPercent;

	@Column(name = "game_ts")
	@JsonView(View.Summary.class)
	private Date gameTimeStamp;

	public AccuracyStats() {
	}

	public AccuracyStats(Player player) {
		this.player = player;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getShots() {
		return shots;
	}

	public void setShots(int shots) {
		this.shots = shots;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public int getAccuracyPercent() {
		return accuracyPercent;
	}

	public void setAccuracyPercent(int accuracyPercent) {
		this.accuracyPercent = accuracyPercent;
	}

	public Date getGameTimeStamp() {
		return gameTimeStamp;
	}

	public void setGameTimeStamp(Date gameTimeStamp) {
		this.gameTimeStamp = gameTimeStamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccuracyStats that = (AccuracyStats) o;

		return (id != null ? id.equals(that.id) : that.id == null) &&
				(player != null ? player.equals(that.player) : that.player == null);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (player != null ? player.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AccuracyStats{" +
				"player=" + player +
				", shots=" + shots +
				", hits=" + hits +
				", accuracyPercent=" + accuracyPercent +
				", gameTimeStamp=" + gameTimeStamp +
				'}';
	}
}