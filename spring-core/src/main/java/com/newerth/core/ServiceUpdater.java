package com.newerth.core;

import com.newerth.core.dao.JPAAccuracyDAO;
import com.newerth.core.dao.JPAPlayerDAO;
import com.newerth.core.entities.AccuracyStats;
import com.newerth.core.entities.LastAccuracyStats;
import com.newerth.core.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for changing information in the DB
 */
@Service
public class ServiceUpdater {

	private JPAPlayerDAO playerDAO;
	private JPAAccuracyDAO accuracyDAO;
	private ServiceInfo info;

	@Autowired
	private void setPlayerDAO(JPAPlayerDAO jpaPlayerDAO) {
		this.playerDAO = jpaPlayerDAO;
	}

	@Autowired
	private void setAccuracyDAO(JPAAccuracyDAO jpaAccuracyDAO) {
		this.accuracyDAO = jpaAccuracyDAO;
	}

	@Autowired
	private void setInfo(ServiceInfo serviceInfo){
		this.info = serviceInfo;
	}

	/**
	 * Saves new player or updates old player if he exists
	 */
	public boolean saveOrUpdatePlayer(Player player) {
		Player p = info.findPlayer(player.getUid());
		if (p != null) {
			player.setId(p.getId());
		}
		try {
			playerDAO.save(player);
			return true;
		} catch (RuntimeException e) {
			// ignored; fix it
		}
		return false;
	}

	/**
	 * Saves or updates the list of the players
	 */
	public boolean saveOrUpdatePlayers(List<Player> players) {
		players.forEach(player -> {
			Player p = info.findPlayer(player.getUid());
			if (p != null) {
				player.setId(p.getId());
			}
		});
		try {
			playerDAO.save(players);
			return true;
		} catch (RuntimeException e) {
			// ignored; fix it
		}
		return false;
	}

	/**
	 * Saves or updates accuracy for the player
	 */
	public boolean saveOrUpdateAccuracy(AccuracyStats accuracy) {
		saveOrUpdatePlayer(accuracy.getPlayer());
		AccuracyStats as = info.findPlayerAccuracy(accuracy.getPlayer().getUid());
		if (as != null) {
			accuracy.setPlayer(info.findPlayer(as.getPlayer().getUid()));
		}
		try {
			accuracyDAO.save(accuracy);
			return true;
		} catch (RuntimeException e) {
			// ignored; fix it
		}
		return false;
	}

	/**
	 * Saves or updates last accuracy for the player
	 */
	public boolean saveOrUpdateLastAccuracy(LastAccuracyStats lastAccuracy) {
		saveOrUpdatePlayer(lastAccuracy.getPlayer());
		LastAccuracyStats las = info.findPlayerLastAccuracy(lastAccuracy.getPlayer().getUid());
		if (las != null) {
			lastAccuracy.setPlayer(info.findPlayer(las.getPlayer().getUid()));
		}
		try {
			accuracyDAO.save(lastAccuracy);
			return true;
		} catch (RuntimeException e) {
			// ignored; fix it
		}
		return false;
	}
}