package com.emamaker.amazeing.player.powerups;

import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpFeather extends PowerUpTemporized {

	public PowerUpFeather() {
		super("FEATHER", TextureLoader.textureFeather, true, 10,
				1f, 1f);
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		player.speedMult = 2f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		player.speedMult = 1f;
	}

}
