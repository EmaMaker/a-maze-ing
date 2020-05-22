package com.emamaker.amazeing.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureLoader {
	
	public static Texture textureSlug;
	public static Texture textureBomb;
	public static Texture textureFeather;
	public static Texture textureBallAndChain;
	
	public TextureLoader() {
		textureBallAndChain = new Texture(Gdx.files.internal("data/powerups/ball_and_chain.png"));
		textureFeather = new Texture(Gdx.files.internal("data/powerups/feather.png"));
		textureSlug =  new Texture(Gdx.files.internal("data/powerups/slug.png"));
		textureBomb = new Texture(Gdx.files.internal("data/powerups/bomb.png"));
	}

}
