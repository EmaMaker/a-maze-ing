package com.emamaker.amazeing.player.powerups;

import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpSlug extends PowerUpTemporized {

	public PowerUpSlug() {
		super("SLUG", TextureLoader.textureSlug, true, 20,
				1.3f, 1.3f);
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		player.speedMult = 0.25f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		player.speedMult = 1f;
	}

}

class PowerUpGiveSlug extends PowerUpGiver {

	public PowerUpGiveSlug() {
		super(new PowerUpSlug(), "SLUG GIVER", TextureLoader.textureSlug, false, 1f, 1f);
	}

}
