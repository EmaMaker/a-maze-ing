package com.emamaker.amazeing.player.powerups;

import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpBallAndChain extends PowerUpTemporized {

	public PowerUpBallAndChain() {
		super("BALL AND CHAIN", TextureLoader.textureBallAndChain, true, 10, 1f, 1f);
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
				TextureLoader.textureBallAndChain, false,
				1f, 1f);
	}

}
