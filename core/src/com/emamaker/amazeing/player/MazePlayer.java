package com.emamaker.amazeing.player;

import java.util.Random;

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
	boolean disposing = false;
	boolean disposed = false;
	boolean playing = false;
	boolean show = true;
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
		disposing = false;
		disposed = false;
		playing = false;
	}

	MazePlayer(String name, boolean s) {
		main = AMazeIng.getMain();
		show = s;
		setName(name);
		if (show)
			buildModel();
	}

	public Vector3 getPos() {
		return pos;
	}

	public Quaternion getRotation() {
		return rot;
	}

	public void setPlaying() {
		disposing = false;
		playing = true;
	}

	public void setPos(Vector3 v) {
		if (!disposing)
			setPos(v.x, v.y, v.z);
	}

	public void setPos(float x, float y, float z) {
		if (!disposing)
			setTransform(x, y, z, 0, 0, 0, 0);
	}

	public void setTransform(float x, float y, float z, float i, float j, float k, float l) {
		if (!disposing && !disposed) {
			pos.set(x, y, z);
			rot.set(i, j, k, l);
			if (show)
				instance.transform.set(x, y, z, i, j, k, l);
		}
	}

	public void setName(String name_) {
		this.name = name_;
	}

	public String getName() {
		return name;
	}

	public void render(ModelBatch b, Environment e) {
		if (!disposing && ! disposed && playing) {
			update();
			if (show)
				b.render(instance, e);
		}
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

	public void update() {
		speed = baseSpeed * speedMult;
		turnSpeed = baseTurnSpeed * speedMult;
		
		if(currentPowerUp != null && currentPowerUp.continousEffect) usePowerUp();
	}
	
	public void usePowerUp() {
		if (currentPowerUp != null && !currentPowerUp.beingUsed)
			if (currentPowerUp.usePowerUp(this))
				currentPowerUp = null;
	}

	@Override
	public void dispose() {
		playing = false;
		if (!disposed) {
			disposing = true;
			if (show)
				mazePlayerModel.dispose();
			disposing = false;
		}
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}

	public boolean isPlaying() {
		return playing;
	}

}
