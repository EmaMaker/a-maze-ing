package com.emamaker.amazeing.player.powerups;

import java.util.Random;

public class PowerUps {

//	public static PowerUp bigBomb = new PowerUpBigBomb("BIG BOMB", new Texture(Gdx.files.internal("data/powerups/bomb_big.png"))));
//	public static PowerUp speed = new PowerUpSpeed("SPEED", new Texture(Gdx.files.internal("data/powerups/speed.png"))));
//	public static PowerUp bigSpeed = new PowerUpBigSpeed("FASTER SPEED", new Texture(Gdx.files.internal("data/powerups/big_speed.png"))));
//	public static PowerUp createWall = new PowerUpCreateWall("CREATE WALL", new Texture(Gdx.files.internal("data/powerups/wall.png"))));
//	public static PowerUp floorHole = new PowerUpFloorHole("FLOOR HOLE", new Texture(Gdx.files.internal("data/powerups/floor_hole.png"))));

	static Random rand = new Random();

	public PowerUps() {
	}

	public static PowerUp pickRandomPU() {
		switch (Math.abs(rand.nextInt()) % 12) {
		case 0:
		case 1:
		case 2:
		case 3:
			return new PowerUpBomb();
		case 4:
			return new PowerUpBigBomb();
		case 5:
		case 6:
		case 7:
			return new PowerUpGiveBallAndChain();
		case 8:
		case 9:
			return new PowerUpGiveSlug();
		case 10:
		case 11:
			return new PowerUpFeather();
		default:
			return new PowerUpGiveBallAndChain();
		}
	}
}
