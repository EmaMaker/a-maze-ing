package com.emamaker.amazeing.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.controllers.Controller;
import com.emamaker.amazeing.maze.settings.MazeSettings;

public class PlayerUtils {
	
	/*Utility function to add and remove players from arrays when organizing as game*/

	public static boolean togglePlayer(MazePlayerLocal p, Set<MazePlayer> players) {
		if (alreadyAddedPlayer(p, players)) {
			p.dispose();
			players.remove(p);
			return false;
		}
		if(players.size() < MazeSettings.MAXPLAYERS) {
			players.add(p);
			return true;
		}
		return false;
	}

	public static boolean togglePlayer(MazePlayerLocal p, ArrayList<MazePlayer> players) {
		HashSet<MazePlayer> players2 = new HashSet<>(players);
		if (alreadyAddedPlayer(p, players2)) {
			p.dispose();
			players.remove(p);
			return false;
		}
		if(players.size() < MazeSettings.MAXPLAYERS) {
			players.add(p);
			return true;
		}
		return false;
	}

	public static boolean togglePlayerWithKeys(Set<MazePlayer> players, int... keys) {
		if (alreadyAddedPlayerWithKeys(players, keys)) {
			getPlayerWithKeys(players, keys).dispose();
			players.remove(getPlayerWithKeys(players, keys));
			return false;
		}
		if (players.size() < MazeSettings.MAXPLAYERS) {
			players.add(new MazePlayerLocal(keys));
			return true;
		}
		return false;
	}

	public static boolean togglePlayerWithKeys(ArrayList<MazePlayer> players, int... keys) {
		HashSet<MazePlayer> players2 = new HashSet<>(players);
		if (alreadyAddedPlayerWithKeys(players2, keys)) {
			getPlayerWithKeys(players2, keys).dispose();
			players.remove(getPlayerWithKeys(players2, keys));
			return false;
		}
		if (players.size() < MazeSettings.MAXPLAYERS) {
			players.add(new MazePlayerLocal(keys));
			return true;
		}
		return false;
	}

	public static boolean alreadyAddedPlayerWithKeys(Set<MazePlayer> players, int... keys) {
		return getPlayerWithKeys(players, keys) != null;
	}

	public static boolean alreadyAddedPlayer(MazePlayerLocal p, Set<MazePlayer> players) {
		return players.contains(p);
	}

	public static MazePlayerLocal getPlayerWithKeys(Set<MazePlayer> players, int... keys) {
		for (MazePlayer p : players) {
			if (p instanceof MazePlayerLocal) {
				for (int k : keys) {
					if (((MazePlayerLocal) p).kup == k || ((MazePlayerLocal) p).kdown == k
							|| ((MazePlayerLocal) p).ksx == k || ((MazePlayerLocal) p).kdx == k)
						return (MazePlayerLocal) p;
				}
			}
		}
		return null;
	}

	public static boolean alreadyAddedPlayerWithCtrl(Controller ctrl, Set<MazePlayer> players) {
		return getPlayerWithCtrl(ctrl, players) != null;
	}

	public static MazePlayerLocal getPlayerWithCtrl(Controller ctrl, Set<MazePlayer> players) {
		for (MazePlayer p : players) {
			if (p instanceof MazePlayerLocal) {
				if (((MazePlayerLocal) p).ctrl == ctrl)
					return (MazePlayerLocal) p;
			}
		}
		return null;
	}
}
