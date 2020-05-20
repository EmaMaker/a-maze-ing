package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.emamaker.amazeing.player.MazePlayer;

public class PowerUpSlug extends PowerUpTemporized {

	public PowerUpSlug() {
		super("SLUG", new Texture(Gdx.files.internal("data/powerups/slug.png")), true, 20,
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
		super(new PowerUpSlug(), "SLUG GIVER",
				new Texture(Gdx.files.internal("data/powerups/slug.png")), false, 1f, 1f);
	}

}
