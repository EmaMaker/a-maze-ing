package com.emamaker.amazeing.player;

import java.util.Random;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.voxelengine.physics.GameObject;

public abstract class MazePlayer {

	AMazeIng main;

	static Random rand = new Random();

	// MazePlayer model building stuff
	public Model mazePlayerModel;
	public ModelInstance instance;
	ModelBuilder modelBuilder = new ModelBuilder();
	MeshPartBuilder meshBuilder;
	static int meshAttr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
	public GameObject obj;
	String name;
	boolean disposing = false;

	MazePlayer(Game main_) {
		this(main_, String.valueOf((char) (65 + rand.nextInt(26))));
		disposing = false;
	}

	MazePlayer(Game main_, String name) {
		main = (AMazeIng) main_;

		setName(name);
		buildModel();
	}

	public Vector3 getPos() {
		return instance.transform.getTranslation(new Vector3());
	}

	public void setPlaying() {
		disposing = false;
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
		if (!disposing)
			instance.transform.set(x, y, z, i, j, k, l);
	}

	public void setName(String name_) {
		this.name = name_;
	}

	public String getName() {
		return name;
	}

	public void render(ModelBatch b, Environment e) {
		if (!disposing) {
			update();
			b.render(instance, e);
		}
	}

	public void buildModel() {
		modelBuilder.begin();
		Node n = modelBuilder.node();
		n.id = "MazePlayer";
		modelBuilder.part("MazePlayer", GL20.GL_TRIANGLES, meshAttr, new Material()).box(0.6f, 0.6f, 0.6f);
		mazePlayerModel = modelBuilder.end();
		instance = new ModelInstance(mazePlayerModel);
	}

	public void update() {
	}

	public void dispose() {
	}

}
