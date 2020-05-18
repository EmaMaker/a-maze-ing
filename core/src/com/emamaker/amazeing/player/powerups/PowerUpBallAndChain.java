package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.emamaker.amazeing.player.MazePlayer;

public class PowerUpBallAndChain extends PowerUpTemporized {

	public PowerUpBallAndChain() {
		super("BALL AND CHAIN",
				new Texture(Gdx.files.internal("data/powerups/ball_and_chain.png")), true,
				10, 1f, 1f);
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		player.speedMult = 0.5f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		player.speedMult = 1f;
	}
}

class PowerUpGiveBallAndChain extends PowerUpGiver {

	public PowerUpGiveBallAndChain() {
		super(new PowerUpBallAndChain(), "BALL AND CHAIN GIVER",
				new Texture(Gdx.files.internal("data/powerups/ball_and_chain.png")), false,
				1f, 1f);
	}

}
