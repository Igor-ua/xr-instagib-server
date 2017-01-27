package com.newerth.core.entities;

import com.fasterxml.jackson.annotation.JsonView;
import com.newerth.core.View;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Entity
@Table(name = "accuracy_stats")
public class AccuracyStats implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonView(View.Summary.class)
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "player_uid", referencedColumnName = "uid", unique = true)
	@JsonView(View.Summary.class)
	private Player player;
	//----Last accuracy stats---------------------------------------------------------------
	@Column(name = "last_shots")
	@JsonView(View.Summary.class)
	private int lastShots;

	@Column(name = "last_hits")
	@JsonView(View.Summary.class)
	private int lastHits;

	@Column(name = "last_frags")
	@JsonView(View.Summary.class)
	private int lastFrags;

	@Column(name = "last_accuracy_percent")
	@JsonView(View.Summary.class)
	@Min(0)
	@Max(100)
	private int lastAccuracyPercent;
	//----Accumulated accuracy stats--------------------------------------------------------
	@Column(name = "accumulated_shots")
	@JsonView(View.Summary.class)
	private int accumulatedShots;

	@Column(name = "accumulated_hits")
	@JsonView(View.Summary.class)
	private int accumulatedHits;

	@Column(name = "accumulated_frags")
	@JsonView(View.Summary.class)
	private int accumulatedFrags;

	@Column(name = "accumulated_accuracy_percent")
	@JsonView(View.Summary.class)
	@Min(0)
	@Max(100)
	private int accumulatedAccuracyPercent;
	//--------------------------------------------------------------------------------------
	@Column(name = "game_ts")
	@JsonView(View.Summary.class)
	private Date gameTimeStamp;

	@Transient
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	private class Updater {
		private int shots;
		private int hits;
		private int frags;
	}

	@Transient
	private Updater updater;
	//--------------------------------------------------------------------------------------

	public AccuracyStats() {
		this.updater = new Updater();
	}

	//----Getters---------------------------------------------------------------------------
	public Long getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public int getLastShots() {
		return lastShots;
	}

	public int getLastHits() {
		return lastHits;
	}

	public int getLastAccuracyPercent() {
		return lastAccuracyPercent;
	}

	public int getLastFrags() {
		return lastFrags;
	}

	public int getAccumulatedShots() {
		return accumulatedShots;
	}

	public int getAccumulatedHits() {
		return accumulatedHits;
	}

	public int getAccumulatedFrags() {
		return accumulatedFrags;
	}

	public int getAccumulatedAccuracyPercent() {
		return accumulatedAccuracyPercent;
	}

	//----Setters---------------------------------------------------------------------------
	void setStats(int shots, int hits, int frags) {
		setLastShots(shots);
		setLastHits(hits);
		setLastFrags(frags);
		if (hits > shots) {
			throw new IllegalArgumentException("Shots more than hits: [shots: " + shots + ", hits: " + hits + "]");
		}
		makeLastAccuracyPercent();
	}

	private void setLastShots(int shots) {
		this.lastShots = shots;
		updater.shots = shots;
	}

	private void setLastHits(int hits) {
		this.lastHits = hits;
		updater.hits = hits;
	}

	private void setLastFrags(int frags) {
		this.lastFrags = frags;
		updater.frags = frags;
	}

	private void makeLastAccuracyPercent() {
		this.lastAccuracyPercent = calculateAccuracy(this.lastShots, this.lastHits);
	}

	private int calculateAccuracy(int shots, int hits) {
		int result = 0;
		if (hits > 0 && shots > 0) {
			result = (int) Math.ceil((double) hits * 100 / shots);
		}
		return result;
	}

	@PrePersist
	@PreUpdate
	private void accumulatedStatsUpdater() {
		accumulatedShots += updater.shots;
		accumulatedHits += updater.hits;
		accumulatedFrags += updater.frags;
		accumulatedAccuracyPercent = calculateAccuracy(accumulatedShots, accumulatedHits);
		this.gameTimeStamp = new Date();
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
				"[lastShots: " + lastShots +
				", lastHits: " + lastHits +
				", lastFrags: " + lastFrags +
				", lastAccuracyPercent: " + lastAccuracyPercent + "], [" +
				", accumulatedShots: " + accumulatedShots +
				", accumulatedHits: " + accumulatedHits +
				", accumulatedFrags" + accumulatedFrags +
				", accumulatedAccuracyPercent: " + accumulatedAccuracyPercent + "], [" +
				", gameTimeStamp=" + sdf.format(gameTimeStamp) + "]" +
				'}';
	}
}