package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpSlug extends PowerUpTemporized {

	public PowerUpSlug() {
		super("SLUG", TextureLoader.textureSlug, true, 15,
				1.3f, 1.3f, Gdx.files.internal("data/particles/slugs.particle"), Gdx.files.internal("data/powerups"));
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		super.temporizedEffect(player);
		player.speedMult = 0.25f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		super.temporizedEffectExpired(player);
		player.speedMult = 1f;
	}

}

class PowerUpGiveSlug extends PowerUpGiver {

	public PowerUpGiveSlug() {
		super(new PowerUpSlug(), "SLUG_GIVER", TextureLoader.textureSlug, false, 1f, 1f, null, null);
	}

}
