package com.emamaker.amazeing.manager.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkCommon {


	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(LoginAO.class);
		kryo.register(LoginAO2.class);
		kryo.register(LoginUUID.class);
		kryo.register(AddNewPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(UpdatePlayerTransform.class);
		kryo.register(UpdatePlayerTransformServer.class);
		kryo.register(StartGame.class);
		kryo.register(UpdateMap.class);
	}

	//Login stuff
	static public class LoginAO {
	}
	static public class LoginAO2 {
		String uuid;
	}
	static public class LoginUUID {
		String uuid;
	}

	//Player stuff
	static public class AddNewPlayer {
		String uuid;
	}
	static public class RemovePlayer {
		String uuid;
	}
	static public class UpdatePlayerTransform {
		String uuid;
		float tx, ty, tz, rx, ry, rz, rw;
	}
	static public class UpdatePlayerTransformServer {
		String uuid;
		float tx, ty, tz, rx, ry, rz, rw;
	}
	
	static public class StartGame{
		//Use this to notify clients of a newly started game
		//A Run-lenght-encoded representation of the map can be appended, this can be avoided but it's not recommended
		String map;
	}
	static public class UpdateMap{
		//Run-lenght-encoded representation of the map
		String map;
	}

}
