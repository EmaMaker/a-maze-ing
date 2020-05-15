package com.emamaker.amazeing.manager.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkCommon {


	// This registers objects that are going to be sent over the network.
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(JustConnected.class);
		kryo.register(LoginAO.class);
		kryo.register(LoginAO2.class);
		kryo.register(ConnectionRefused.class);
		kryo.register(LoginUUID.class);
		kryo.register(AddNewPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(UpdatePlayerTransform.class);
		kryo.register(UpdatePlayerTransformServer.class);
		kryo.register(StartGame.class);
		kryo.register(EndGame.class);
		kryo.register(UpdateMap.class);
		kryo.register(UpdateSettings.class);
	}

	//Login stuff
	public static class JustConnected {
	}
	public static class LoginAO {
	}
	public static class LoginAO2 {
		String uuid;
	}
	public static class ConnectionRefused {
		String uuid;
	}
	public static class LoginUUID {
		String uuid;
	}

	//Player stuff
	public static class AddNewPlayer {
		String uuid;
	}
	public static class RemovePlayer {
		String uuid;
	}
	public static class UpdatePlayerTransform {
		String uuid;
		float tx, ty, tz, rx, ry, rz, rw;
	}
	public static class UpdatePlayerTransformServer {
		String uuid;
		float tx, ty, tz, rx, ry, rz, rw;
	}

	public static class StartGame{
		//Use this to notify clients of a newly started game
		//A Run-lenght-encoded representation of the map can be appended, this can be avoided but it's not recommended
		String map;
	}
	public static class EndGame{
		//Use this to notify clients when a game ends
	}
	public static class UpdateMap{
		//Use this to notify clients of a modification of the map
		//Run-lenght-encoded representation of the map
		String map;
	}
	
	public static class UpdateSettings {
		int index;
		String value;
	}
	
}
