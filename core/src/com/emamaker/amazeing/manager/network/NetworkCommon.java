package com.emamaker.amazeing.manager.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkCommon {
	// This registers objects that are going to be sent over the network.
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(ClientLoginAO.class);
		kryo.register(ServerLoginUUID.class);
		kryo.register(ClientLoginAO2.class);
		kryo.register(ServerLoginAO2.class);

		kryo.register(RemovePlayer.class);
		kryo.register(PlayerRemoved.class);
		
		kryo.register(String[].class);
		kryo.register(GameStatusUpdate.class);
		kryo.register(UpdateMap.class);
		
		kryo.register(PowerUpUpdate.class);
		kryo.register(PowerUpAssignRequest.class);
		kryo.register(PowerUpAssignRequestOk.class);
		kryo.register(PowerUpUseRequest.class);
		kryo.register(PowerUpUseRequestOk.class);
		kryo.register(PowerUpRevokeRequest.class);
		kryo.register(PowerUpRevokeRequestOk.class);
		
		kryo.register(UpdatePlayerPosition.class);
		kryo.register(UpdateForcedPlayerPosition.class);
		kryo.register(UpdateForcedPlayerPositionOk.class);
		
		kryo.register(ServerClosed.class);
	}

	
	/** PLAYER PACKETS **/
	/*LOGIN*/
	public static class ClientLoginAO {
	}
	public static class ServerLoginUUID {
		public String uuid;
	}
	public static class ClientLoginAO2 {
		public String uuid;
	}
	public static class ServerLoginAO2 {
		public String uuid;
	}
	/*REMOVAL*/
	public static class RemovePlayer{
		public String uuid;
	}
	
	public static class PlayerRemoved{
		public String uuid;
	}
	/*PLAYER POSITION UPDATE*/
	public static class UpdatePlayerPosition{
		public String uuid;
		public float px, py, pz;
	}
	
	public static class UpdateForcedPlayerPosition{
		public String uuid;
		public float px, py, pz;
	}
	
	public static class UpdateForcedPlayerPositionOk{
		public String uuid;
	}
	
	/** SERVER TO CLIENT UPDATES **/
	//Game Status is used to broadcast to clients infos about the game and players
	public static class GameStatusUpdate{
		public boolean gameStarted;
		public boolean anyoneWon;
		public String[] playersUUIDs;
	}
	
	//Run-lenght encoding of the map
	public static class UpdateMap{
		public String map;
	}
	
	/** POWER UPS **/
	public static class PowerUpUpdate {
		public String[] powerups;
	}
	public static class PowerUpAssignRequest{
		public String uuid, pupname;
		public boolean immediateUse;
	}
	public static class PowerUpAssignRequestOk{
		public String uuid;
	}
	public static class PowerUpUseRequest{
		public String uuid, pupname;
	}
	public static class PowerUpUseRequestOk{
		public String uuid;
		public boolean canuse;
	}
	public static class PowerUpRevokeRequest{
		public String uuid;
	}
	public static class PowerUpRevokeRequestOk{
		public String uuid;
	}
	
	/** MISC **/
	public static class ServerClosed{ }
	
}

class ConnectionPlayer extends Connection {
	public String uuid;
}
