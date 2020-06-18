package com.emamaker.amazeing.utils;

import java.nio.ByteBuffer; 
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;

public class MathUtils extends net.dermetfan.gdx.math.MathUtils {

	public static float vectorDistance(Vector3 v1, Vector3 v2) {
		return (float) Math
				.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
	}

	public static Vector3 toScreenCoords(Vector3 vec) {
		// This exta variable is needed otherwise the cam.project will convert the
		// vector in argument from world coordinates to screen coordinates
		Vector3 v1 = new Vector3(vec);
		return AMazeIng.getMain().world.cam.project(v1);
	}

	public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static float pythagoreanTheorem(float a, float b) {
		return (float) Math.sqrt(a * a + b * b);
	}

	public static Color getRandomColor(String s) {
		return getRandomColor(UUID.fromString(s));
	}

	/**
	 * Method return a random color.
	 * https://stackoverflow.com/questions/38394240/how-to-generate-color-code-with-uuid-in-java
	 */
	public static Color getRandomColor(UUID u) {

		byte[] bytes = UUID2Bytes(u);

		float r = Math.abs(bytes[0]) / 255f;
		float g = Math.abs(bytes[1]) / 255f;
		float b = Math.abs(bytes[2]) / 255f;
		
		float value = (r+g+b) / 3; //this is your value between 0 and 1
		float minHue = min(r, g, b);
		float maxHue = max(r, g, b);
		float hue = value*maxHue + (1-value)*minHue; 
		Color c = new Color(java.awt.Color.HSBtoRGB(hue, 1, 1f));

		return new Color(c);
	}

	public static float min(float... fs) {
		float r = Float.MAX_VALUE;
		for(float f : fs) {
			if(f < r) r = f;
		}
		return r;
	}
	
	public static float max(float... fs) {
		float r = Float.MIN_VALUE;
		for(float f : fs) {
			if(f > r) r = f;
		}
		return r;
	}

	public static byte[] UUID2Bytes(UUID uuid) {

		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
	}

	public static class Constants {

		public static int NETWORK_ACTION_TIMEOUT_MILLIS = 7000;
		public static int COMMUNICATION_TIMEOUT_MILLIS = 100000;
		public static int CLIENT_POS_PERIODIC_UPDATE = 1000;

	}
}
