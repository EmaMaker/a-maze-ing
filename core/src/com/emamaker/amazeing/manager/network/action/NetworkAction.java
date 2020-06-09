package com.emamaker.amazeing.manager.network.action;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.network.GameClient;
import com.emamaker.amazeing.manager.network.GameServer;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.utils.MathUtils.Constants;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public abstract class NetworkAction {

	/*
	 * NetworkAction network structure. A NetworkAction has a listener to be started
	 * and another one to be ended Once started (because the listener received the
	 * trigger package or because it has be done manually with .startAction) an
	 * action adds a new instance of itself to the pendingActions list of the parent
	 * NetworkHandler. The NetworkHandler calls the update method of the action The
	 * new instance needs the endlistener to be attached to its parent, so it can be
	 * removed from the parent's pendingActions list by instance Generally a
	 * NetworkAction always has a responsePacket If the startPacket is null, the
	 * action has no start listener and must be started manually If the startPacket
	 * is null, the action has no end listener and must be endeed manually, most of
	 * the time using the oneTime boolean If an action has the oneTime flag set to
	 * true, once called the resolveAction() method for the irst time, it's
	 * automatically ended
	 * 
	 * Every child of this class MUST override the newInstace() method, so that
	 * adding to pendingActions can be done easily. Reflection could be used to
	 * automatically do this, but some NetworkActions require a particular
	 * constructor to pass arguments to the new instance
	 */

	protected Object incomingMsg, endMsg;
	protected Object startPacket, responsePacket, endPacket;
	protected Connection incomingConnection, endConnection;
	protected NetworkHandler parent;
	protected boolean oneTime;

	protected long startTime;
	boolean tookTimeout = false;
	boolean usingTimeout = true;
	boolean oneAtTheTime = true;
	
	//A list of actions that prevent the action to be started inside alreadyPending method
	//(e.g NACLoginAO cannot be started if there's still a NACLoginAO2 running)
	
	public AMazeIng main = AMazeIng.getMain();

	Listener startListener = new Listener() {
		@Override
		public void received(Connection arg0, Object arg1) {
			super.received(arg0, arg1);
			if (startPacket != null && arg1.getClass().equals(startPacket.getClass())) {
//				System.out.println("Received start packet " + arg1 + " for " + this);
				startAction(arg0, arg1);
			}
		}
	};
	Listener endListener = new Listener() {
		@Override
		public void received(Connection arg0, Object arg1) {
			super.received(arg0, arg1);
			if (endPacket != null && arg1.getClass().equals(endPacket.getClass())) {
//				System.out.println("Received end packet " + arg1 + " for " + this);
				responseReceived(arg0, arg1);
			}
		}
	};;

	protected NetworkAction(NetworkHandler parent_, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		this.incomingConnection = c;
		this.incomingMsg = incomingMsg_;
		this.responsePacket = responsePacket_;
		this.endPacket = endPacket_;
		this.parent = parent_;
		this.oneTime = oneTime;

		tookTimeout = false;
		usingTimeout = true;
//		setMaskActions(maskActions);
//		update();
	}

	public NetworkAction(NetworkHandler parent_, Object startPacket_, Object responsePacket_, Object endPacket_,
			boolean oneTime_) {
		this.parent = parent_;
		this.startPacket = startPacket_;
		this.responsePacket = responsePacket_;
		this.endPacket = endPacket_;
		this.oneTime = oneTime_;

		if (client() != null)
			client().client.addListener(startListener);
		if (server() != null)
			server().server.addListener(startListener);
		
//		setMaskActions(maskActions_);
	}

	public void startAction() {
		this.startAction(incomingConnection, incomingMsg);
	}

	public void startAction(Connection conn, Object msg) {
		this.incomingConnection = conn;
		this.incomingMsg = msg;
//		System.out.println("Starting action: " + this);

		if (oneAtTheTime) {
			if (!parent.alreadyPending(this))
				parent.addToPending(newInstance());
//			else
//				addToQueue();
		} else {
			parent.addToPending(newInstance());
		}
	}

	public void update() {
		if (!tookTimeout) {
			tookTimeout = true;
			startTime = System.currentTimeMillis();
		}
		resolveAction();
		if (oneTime)
			responseReceived(null, null);
		if (System.currentTimeMillis() - startTime > Constants.NETWORK_ACTION_TIMEOUT_MILLIS && usingTimeout)
			detachFromParent();
	}

	public void resolveAction() {
//		System.out.println("Resolving action for " + this);
	}

	public void responseReceived(Connection conn, Object msg) {
		this.endConnection = conn;
		this.endMsg = msg;
		detachFromParent();
//		System.out.println("Response received for " + this);
	}

	public void detachFromParent() {
		parent.removeFromPending(this);

		if (client() != null)
			client().client.removeListener(endListener);
		if (server() != null)
			server().server.removeListener(startListener);
	}

	public abstract NetworkAction newInstance();

	public GameClient client() {
		try {
			return (GameClient) parent;
		} catch (Exception e) {
//			System.out.println("Calling client, but this is server");
			return null;
		}
	}

	public GameServer server() {
		try {
			return (GameServer) parent;
		} catch (Exception e) {
//			System.out.println("Calling server, but this is client");
			return null;
		}
	}

	public void enableTimeout(boolean b) {
		this.usingTimeout = b;
	}

	public void setOneAtTheTime(boolean b) {
		this.oneAtTheTime = b;
	}

	public void onParentClosing() {
		responseReceived(null, null);
	}
	
	public void addToQueue() {
		parent.todoActions.add(newInstance());
	}
	
	public void registerEndListener() {
		if (client() != null)
			client().client.addListener(endListener);
		if (server() != null)
			server().server.addListener(endListener);
	}
	

}