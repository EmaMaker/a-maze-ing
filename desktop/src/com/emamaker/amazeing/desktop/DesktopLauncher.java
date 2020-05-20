package com.emamaker.amazeing.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.AMazeIng.Platform;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new AMazeIng(Platform.DESKTOP), config);
	}
}
