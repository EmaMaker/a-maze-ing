package com.emamaker.amazeing.player;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.emamaker.voxelengine.physics.GameObject;

import java.util.Random;

public abstract class MazePlayer implements Disposable {

	AMazeIng main;

	static Random rand = new Random();

	// MazePlayer model building stuff
	public Model mazePlayerModel;
	public ModelInstance instance;
	ModelBuilder modelBuilder = new ModelBuilder();
	MeshPartBuilder meshBuilder;
	static int meshAttr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
	public GameObject obj;
	String name = "";
	boolean disposed = false;
	boolean built = false;
	boolean show = true;
	boolean initedPhysics = false;
	boolean toUpdatePos = false;

	public String uuid;
	public PowerUp currentPowerUp;

	public float baseSpeed = 3f;
	public float baseTurnSpeed = 2.5f;
	public float speedMult = 1f;
	public float speed;
	public float turnSpeed;

	Vector3 pos = new Vector3();
	Quaternion rot = new Quaternion();

	MazePlayer(boolean s) {
		this(String.valueOf((char) (65 + rand.nextInt(26))), s);
		disposed = false;
	}

	MazePlayer(String name, boolean s) {
		main = AMazeIng.getMain();
		show = s;
		setName(name);
		built = false;
	}

	public Vector3 getPos() {
		return pos;
	}

	public Quaternion getRotation() {
		return rot;
	}

	public void setPos(Vector3 v) {
		setPos(v.x, v.y, v.z);
	}

	public void setPos(float x, float y, float z) {
		if (!isDisposed()) {
			pos.set(x, y, z);
			toUpdatePos = true;
		}
	}

	public void setTransform(float x, float y, float z, float i, float j, float k, float l) {
		if (!isDisposed()) {
			pos.set(x, y, z);
			rot.set(i, j, k, l);
			if (show)
				instance.transform.set(x, y, z, i, j, k, l);
		}
	}

	protected void updateFromTmpPos() {
		if (!isDisposed() && toUpdatePos && initedPhysics) {
			setTransform(pos.x, pos.y, pos.z, 0, 0, 0, 0);
			toUpdatePos = false;
		}
	}

	public void setName(String name_) {
		this.name = name_;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("deprecation")
	public void buildModel() {
		modelBuilder.begin();
		Node n = modelBuilder.node();
		n.id = "MazePlayer";
		modelBuilder.part("MazePlayer", GL20.GL_TRIANGLES, meshAttr, new Material()).box(0.6f, 0.6f, 0.6f);
		mazePlayerModel = modelBuilder.end();
		instance = new ModelInstance(mazePlayerModel);
	}

	public void initPhysics() {
		initedPhysics = true;
		disposed = false;
	}

	public void update() {
		speed = baseSpeed * speedMult;
		turnSpeed = baseTurnSpeed * speedMult;

		updateFromTmpPos();

		if (currentPowerUp != null && currentPowerUp.continousEffect && currentPowerUp.beingUsed)
			usePowerUp();
	}

	public void render(ModelBatch b, Environment e) {
		if (!disposed && show) {
			if (!built) {
				buildModel();
				initPhysics();

				built = true;
			}

			updateFromTmpPos();
			b.render(instance, e);
		}
	}

	public void usePowerUp() {
		if (currentPowerUp != null && !currentPowerUp.beingUsed)
			if (currentPowerUp.usePowerUp(this))
				disablePowerUp();
	}

	public void disablePowerUp() {
		currentPowerUp = null;
	}

	@Override
	public void dispose() {
		if (!isDisposed()) {
			if (show && built)
				mazePlayerModel.dispose();
		}
		disposed = true;
	}

	public boolean isDisposed() {
		return disposed;
	}
}
