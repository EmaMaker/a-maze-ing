package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.MathUtils;

public class PowerUp implements Disposable {

	public String name;
	Texture texture;

	public boolean beingUsed, continousEffect;
	private boolean built, toUpdatePos;

	ModelBuilder modelBuilder = new ModelBuilder();
	ModelInstance instance;
	Model quadModel;
	static int meshAttr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
			| VertexAttributes.Usage.TextureCoordinates;
	BlendingAttribute blendingAttribute = new BlendingAttribute();

	float scaleX, scaleZ;

	Vector3 pos = new Vector3();

	ParticleEffect effect;
	ParticleEffectPool effectPool;

	public PowerUp(String name_, Texture texture_, boolean cont, FileHandle effectFile, FileHandle imageSrc) {
		this(name_, texture_, cont, 1, 1, effectFile, imageSrc);
	}

	public PowerUp(String name_, Texture texture_, boolean cont, float scaleX, float scaleZ, FileHandle effectFile,
			FileHandle imageSrc) {
		this.name = name_;
		this.texture = texture_;
		this.scaleX = scaleX;
		this.scaleZ = scaleZ;
		this.continousEffect = cont;

		blendingAttribute.blended = true;
		blendingAttribute.sourceFunction = GL20.GL_SRC_ALPHA;
		blendingAttribute.destFunction = GL20.GL_ONE_MINUS_SRC_ALPHA;

		beingUsed = false;
		built = false;
		toUpdatePos = false;

		if (effectFile != null && imageSrc != null) {
			effect = new ParticleEffect();
			effect.load(effectFile, imageSrc);
			effectPool = new ParticleEffectPool(effect, 1, 2);
		}
	}

	@SuppressWarnings("deprecation")
	public void spawnQuad() {
		modelBuilder.begin();
		Node n = modelBuilder.node();
		n.id = "Quad";
		modelBuilder
				.part("Quad", GL20.GL_TRIANGLES, meshAttr,
						new Material(TextureAttribute.createDiffuse(texture), blendingAttribute))
				.box(0.85f * scaleX, 0.000001f, 0.85f * scaleZ);
		n.rotation.set(Vector3.Y, 90);
		quadModel = modelBuilder.end();
		instance = new ModelInstance(quadModel);
	}

	protected void setModelScale(float x, float y) {
		instance.transform.scale(scaleX, 1, scaleZ);
	}

	public void render(ModelBatch b, Environment e) {
		if (!built) {
			spawnQuad();
			built = true;
		} else {
			updateFromTmpPos();
			b.render(instance, e);
		}
	}

	public void setPosition(float x, float y, float z) {
		pos.set(x, y, z);
		toUpdatePos = true;
	}

	public void setPosition(Vector3 v) {
		this.setPosition(v.x, v.y, v.z);
	}

	public Vector3 getPosition() {
		return pos;
	}

	protected void updateFromTmpPos() {
		if (toUpdatePos && built) {
			instance.transform.set(pos.x, pos.y, pos.z, 0, 0, 0, 0);
			toUpdatePos = false;
		}
	}

	// Return true if the effect has been resolved
	public boolean usePowerUp(MazePlayer player) {
		System.out.println(this.name + " ! ");

		if (effect != null) {
			PooledEffect e = effectPool.obtain();
			Vector3 pos = MathUtils.toScreenCoords(player.getPos());
			e.setPosition(pos.x, pos.y);

			AMazeIng.getMain().effects.add(e);
		}
		return true;
	}

	@Override
	public void dispose() {
		if (quadModel != null)
			quadModel.dispose();
	}
}

class PowerUpTemporized extends PowerUp {

	long time = 0, startTime = 0;
	boolean used = false;

	public PowerUpTemporized(String name_, Texture texture_, boolean cont, float secs_, FileHandle effectFile,
			FileHandle imageSrc) {
		this(name_, texture_, cont, secs_, 1, 1, effectFile, imageSrc);
	}

	public PowerUpTemporized(String name_, Texture texture_, boolean cont, float secs_, float scaleX, float scaleZ,
			FileHandle effectFile, FileHandle imageSrc) {
		super(name_, texture_, cont, scaleX, scaleZ, effectFile, imageSrc);
		this.time = (long) (secs_ * 1000);
		startTime = 0;
	}

	PooledEffect e;

	@Override
	public boolean usePowerUp(MazePlayer player) {
		if (!used) {
			startTime = System.currentTimeMillis();
			used = true;

			e = effectPool.obtain();
			AMazeIng.getMain().effects.add(e);
		}

		if (System.currentTimeMillis() - startTime <= time) {
			temporizedEffect(player);
//			System.out.println("starting " + name);
			return false;
		} else {
			used = false;
//			System.out.println("finishing " + name);
			e = null;
			temporizedEffectExpired(player);
			return true;
		}
	}

	public void temporizedEffect(MazePlayer player) {
		Vector3 p = MathUtils.toScreenCoords(player.getPos());
		e.setPosition(p.x, p.y);

		beingUsed = true;
	}

	public void temporizedEffectExpired(MazePlayer player) {
		beingUsed = false;
	}

}

class PowerUpGiver extends PowerUp {

	PowerUp powerup;
	MazePlayer p;

	public PowerUpGiver(PowerUp p, String name, Texture texture, boolean continuos, FileHandle effectFile,
			FileHandle imageSrc) {
		this(p, name, texture, continuos, 1f, 1f, effectFile, imageSrc);
	}

	public PowerUpGiver(PowerUp p, String name, Texture texture, boolean continuos, float scaleX, float scaleZ,
			FileHandle effectFile, FileHandle imageSrc) {
		super(name, texture, continuos, scaleX, scaleZ, effectFile, imageSrc);
		this.powerup = p;
	}

	@Override
	public boolean usePowerUp(MazePlayer player) {
//		super.usePowerUp(player);

		p = null;
		if (AMazeIng.getMain().currentGameManager.players.size() > 1) {
			while (p == player || p == null)
				p = AMazeIng.getMain().currentGameManager.getRandomPlayer();
			p.currentPowerUp = powerup;
			p.usePowerUp();
		}

		return true;
	}

}