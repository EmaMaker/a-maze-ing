package com.emamaker.amazeing.player.powerups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PowerUps {

//	public static PowerUp bigBomb = new PowerUpBigBomb("BIG BOMB", new Texture(Gdx.files.internal("data/powerups/bomb_big.png"))));
//	public static PowerUp speed = new PowerUpSpeed("SPEED", new Texture(Gdx.files.internal("data/powerups/speed.png"))));
//	public static PowerUp bigSpeed = new PowerUpBigSpeed("FASTER SPEED", new Texture(Gdx.files.internal("data/powerups/big_speed.png"))));
//	public static PowerUp createWall = new PowerUpCreateWall("CREATE WALL", new Texture(Gdx.files.internal("data/powerups/wall.png"))));
//	public static PowerUp floorHole = new PowerUpFloorHole("FLOOR HOLE", new Texture(Gdx.files.internal("data/powerups/floor_hole.png"))));

	public static ArrayList<PowerUp> powerups = new ArrayList<>();
	static HashMap<String, PowerUp> powerupLookup = new HashMap<>();
	static Random rand = new Random();

	public PowerUps() {
		powerups.add(new PowerUpBomb());
		powerups.add(new PowerUpBigBomb());
		powerups.add(new PowerUpGiveBallAndChain());
		powerups.add(new PowerUpGiveSlug());
		powerups.add(new PowerUpFeather());
		
		for(PowerUp p : powerups) {
			powerupLookup.put(p.name, p);
		}
	}

	public static PowerUp pickRandomPU() {
		try {
			return powerups.get(rand.nextInt(powerups.size())).getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PowerUp pickByName(String name) {
		try {
			return powerupLookup.get(name).getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
