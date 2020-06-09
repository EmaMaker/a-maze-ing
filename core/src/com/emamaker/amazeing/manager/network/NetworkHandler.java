package com.emamaker.amazeing.manager.network;

import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateForcedPlayerPosition;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerPosition;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.MazePlayer;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class NetworkHandler {

	public ConcurrentHashMap<String, MazePlayer> players = new ConcurrentHashMap<String, MazePlayer>();
	public GameManager gameManager;
	public AMazeIng main = AMazeIng.getMain();

	// A List of all actions that can be done by the handler
	public CopyOnWriteArrayList<NetworkAction> actions = new CopyOnWriteArrayList<>();
	// A list of the actions that are currently in resolution and are waiting for a
	// response
	public CopyOnWriteArrayList<NetworkAction> pendingActions = new CopyOnWriteArrayList<>();
	// Some actions (such as the first step of login) cannot be done in multiple
	// instances at the same time
	// Actions of this type have to be stored here
	public CopyOnWriteArrayList<NetworkAction> todoActions = new CopyOnWriteArrayList<>();
	CopyOnWriteArrayList<NetworkAction> deletePending = new CopyOnWriteArrayList<>();

	int port;
	boolean running = false;

	long time = 0;
	int UPDATE_PERIOD = 750;

	/*
	 * Everything is done with the use of UDP and actions. Basically an Action will
	 * send a specific UDP package to one end to another and repeat the same action
	 * until a response is received, look in the NetworkAction class for more info A
	 * NetworkHandler provides functionality to the NetworkActions: it updates and
	 * manages the adding and removal of NetworkActions
	 */

	public void update() {
		if (isRunning()) {
			updatePending();

			if (gameManager != null)
				gameManager.update();

			if (gameManager != null && System.currentTimeMillis() - time > UPDATE_PERIOD) {
				if (gameManager.gameStarted)
					periodicGameUpdate();
				else
					periodicNonGameUpdate();
				time = System.currentTimeMillis();
			}
		}
	}

	public void updatePending() {
	    //System.out.println(Arrays.toString(todoActions.toArray()));
		for(NetworkAction a : todoActions) {
			if(!alreadyPending(a)) {
			    System.out.println(a);
				addToPending(a);
				todoActions.remove(a);
			}
		}
		
		//		if (!todoActions.isEmpty()) {
//			System.out.println("Actions queue: " + Arrays.toString(todoActions.toArray()));
//			if (!alreadyPending(todoActions.peek())) {
//				todoActions.peek().startAction(null, null);
//				System.out.println("Getting action from queue: " + todoActions.peek());
//				addToPending(todoActions.remove());
//			}
//		}

		for (NetworkAction n : pendingActions)
			n.update();
	}

	/*
	 * Unluckily, we can't check if a specific action is already pending. But we can
	 * check if there's another type of the same action running. NetworkActions can
	 * override the startAction method to be started even if there's another one
	 * already running (e.g. Player Removal)
	 */
	public boolean alreadyPending(NetworkAction act) {
		for (NetworkAction a : pendingActions) {
			if (a.getClass().isAssignableFrom(act.getClass())) {
//				System.out.println("Already pending " + act);
				return true;
			}
			/*for(int i = 0; i < act.maskActions.length; i++){
			    if(a.getClass().isAssignableFrom(a.maskActions[i].getClass())) return true;
            }*/

		}
		return false;
	}

	// Actions can be removed from pending by instance
	public void removeFromPending(NetworkAction act) {
		pendingActions.remove(act);
//		System.out.println("Delete from pending " + act);
	}

	public void addToPending(NetworkAction a) {
	    a.registerEndListener();
		pendingActions.add(a);
	}

	public void periodicGameUpdate() {
	}

	public void periodicNonGameUpdate() {
	}

	public void registerActions() {
	}

	public abstract boolean startGame();

	public abstract void stop();

	public boolean isRunning() {
		return running;
	}

	public NetworkAction getActionByClass(Class<?> clas) {
		for (NetworkAction a : actions) {
			if (a.getClass() == clas) {
				return a;
			}
		}
		return null;
	}

	public void startDefaultActions() {
	}

	public Object updatePlayer(String uuid, MazePlayer p, boolean force) {
		if (force) {
			UpdateForcedPlayerPosition pu = new UpdateForcedPlayerPosition();
			Vector3 pos = p.getPos();
			pu.px = pos.x;
			pu.py = pos.y;
			pu.pz = pos.z;
			pu.uuid = uuid;
//			System.out.println("Forcing position update to all clients for player " + uuid + " in pos " + pos.toString());
			return pu;
		} else {
			UpdatePlayerPosition pu = new UpdatePlayerPosition();
			Vector3 pos = p.getPos();
			pu.px = pos.x;
			pu.py = pos.y;
			pu.pz = pos.z;
			pu.uuid = uuid;
//			System.out.println("Sending position update to all clients for player " + uuid + " in pos " + pos.toString());
			return pu;
		}
	}

}
