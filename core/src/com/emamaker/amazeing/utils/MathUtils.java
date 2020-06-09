package com.emamaker.amazeing.utils;

import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;

public class MathUtils extends net.dermetfan.gdx.math.MathUtils {

	public static float vectorDistance(Vector3 v1, Vector3 v2) {
		return (float) Math
				.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
	}
	
	
	public static Vector3 toScreenCoords(Vector3 vec) {
		//This exta variable is needed otherwise the cam.project will convert the vector in argument from world coordinates to screen coordinates
		Vector3 v1 = new Vector3(vec);
		return AMazeIng.getMain().world.cam.project(v1);
	}
	
	public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	public static class Constants {

		public static int NETWORK_ACTION_TIMEOUT_MILLIS = 7000;
		public static int COMMUNICATION_TIMEOUT_MILLIS = 100000;
		public static int CLIENT_POS_PERIODIC_UPDATE = 1000;

	}
}
