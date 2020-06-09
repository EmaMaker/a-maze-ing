package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpBallAndChain extends PowerUpTemporized {

	public PowerUpBallAndChain() {
		super("BALL AND CHAIN", TextureLoader.textureBallAndChain, true, 10, 1f, 1f,
				Gdx.files.internal("data/particles/ball_and_chain.particle"), Gdx.files.internal("data/powerups"));
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		super.temporizedEffect(player);
		player.speedMult = 0.5f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		super.temporizedEffectExpired(player);
		player.speedMult = 1f;
	}
}

class PowerUpGiveBallAndChain extends PowerUpGiver {

	public PowerUpGiveBallAndChain() {
		super(new PowerUpBallAndChain(), "BALL AND CHAIN GIVER", TextureLoader.textureBallAndChain, false, null, null);
	}

}
