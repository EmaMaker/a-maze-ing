package com.emamaker.amazeing.utils;

import com.badlogic.gdx.math.Vector3;

public class MathUtils extends net.dermetfan.gdx.math.MathUtils {

	public static float vectorDistance(Vector3 v1, Vector3 v2) {
		return (float) Math.sqrt((v1.x - v2.x)*(v1.x - v2.x) + (v1.y - v2.y)*(v1.y - v2.y) + (v1.z - v2.z)*(v1.z - v2.z));
	}
}
