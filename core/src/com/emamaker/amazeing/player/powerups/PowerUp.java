package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
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

	public PowerUp(String name_, Texture texture_, boolean cont) {
		this(name_, texture_, cont, 1, 1);
	}

	public PowerUp(String name_, Texture texture_, boolean cont, float scaleX, float scaleZ) {
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
//		System.out.println(this.name + "!");
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

	public PowerUpTemporized(String name_, Texture texture_, boolean cont, float secs_) {
		this(name_, texture_, cont, secs_, 1, 1);
	}

	public PowerUpTemporized(String name_, Texture texture_, boolean cont, float secs_, float scaleX, float scaleZ) {
		super(name_, texture_, cont, scaleX, scaleZ);
		this.time = (long) (secs_ * 1000);
		startTime = 0;
	}

	@Override
	public boolean usePowerUp(MazePlayer player) {
		super.usePowerUp(player);

		if (!used) {
			startTime = System.currentTimeMillis();
			used = true;
		}

		if (System.currentTimeMillis() - startTime < time) {
			temporizedEffect(player);
			return false;
		} else {
			used = false;
			temporizedEffectExpired(player);
			return true;
		}
	}

	public void temporizedEffect(MazePlayer player) {
		beingUsed = true;
	}

	public void temporizedEffectExpired(MazePlayer player) {
		beingUsed = false;
	}

}

class PowerUpGiver extends PowerUp {

	PowerUp powerup;
	MazePlayer p;

	public PowerUpGiver(PowerUp p, String name, Texture texture, boolean continuos) {
		this(p, name, texture, continuos, 1f, 1f);
	}

	public PowerUpGiver(PowerUp p, String name, Texture texture, boolean continuos, float scaleX, float scaleZ) {
		super(name, texture, continuos, scaleX, scaleZ);
		this.powerup = p;
	}

	@Override
	public boolean usePowerUp(MazePlayer player) {
		super.usePowerUp(player);

		p = null;
		if (AMazeIng.getMain().currentGameManager.players.size() > 1) {
			while (p == player || p == null)
				p = AMazeIng.getMain().currentGameManager.getRandomPlayer();
			p.currentPowerUp = new PowerUpBallAndChain();
			p.usePowerUp();
		}

		return true;
	}

}