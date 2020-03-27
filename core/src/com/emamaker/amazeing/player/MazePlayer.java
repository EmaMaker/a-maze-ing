package com.emamaker.amazeing.player;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.voxelengine.physics.GameObject;

public class MazePlayer {

	static Random rand = new Random();

	// MazePlayer model building stuff
	public Model MazePlayerModel;
	public ModelInstance instance;
	ModelBuilder modelBuilder = new ModelBuilder();
	MeshPartBuilder meshBuilder;
	static int meshAttr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
	public GameObject obj;

	btConvexShape ghostShape;
	public btPairCachingGhostObject ghostObject;
	public btKinematicCharacterController characterController;
	Matrix4 characterTransform;
	Vector3 characterDirection = new Vector3();
	Vector3 walkDirection = new Vector3();
	public Controller ctrl;

	// Physics using LibGDX's bullet wrapper
	public int kup, kdown, ksx, kdx;
	float startx, starty, startz;
	String name;
	AMazeIng main;

	boolean disposing = false;

	public MazePlayer(Game main_, int up_, int down_, int sx_, int dx_) {
		this(main_, up_, down_, sx_, dx_, 0, 0, 0);
	}

	public MazePlayer(Game main_, int up_, int down_, int sx_, int dx_, String name) {
		this(main_, up_, down_, sx_, dx_, 0, 0, 0, name);
	}

	public MazePlayer(Game main_, int up_, int down_, int sx_, int dx_, float startx, float starty, float startz) {
		this(main_, up_, down_, sx_, dx_, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayer(Game main_, int up_, int down_, int sx_, int dx_, float startx, float starty, float startz,
			String name) {
		this.kup = up_;
		this.kdown = down_;
		this.ksx = sx_;
		this.kdx = dx_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;

		this.main = (AMazeIng) main_;

		setName(name);
		buildModel();
		initPhysics();
	}

	public MazePlayer(Game main_, Controller ctrl_) {
		this(main_, ctrl_, 0, 0, 0);
	}

	public MazePlayer(Game main_, Controller ctrl_, String name) {
		this(main_, ctrl_, 0, 0, 0, name);
	}

	public MazePlayer(Game main_, Controller crtl_, float startx, float starty, float startz) {
		this(main_, crtl_, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayer(Game main_, Controller ctrl_, float startx, float starty, float startz, String name) {
		this.ctrl = ctrl_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;

		setName(name);
		buildModel();
		initPhysics();
	}

//	public void buildModel() {
//		modelBuilder.begin();
//		Node n = modelBuilder.node();
//		n.id = "MazePlayer";
//		modelBuilder.part("MazePlayer", GL20.GL_TRIANGLES, meshAttr, new Material()).cone(1, 1, 1, 20);
//		MazePlayerModel = modelBuilder.end();
//	}
//
//	public void initPhysics() {
//		GameObject.Constructor construct = new GameObject.Constructor(MazePlayerModel, "MazePlayer", new btConeShape(1,1),
//			30f);
//		obj = construct.construct();
//		obj.transform.trn(VoxelSettings.chunkSize/2, VoxelSettings.chunkSize/2, VoxelSettings.chunkSize/2);
//		obj.body.proceedToTransform(obj.transform);
//		obj.body.setCollisionFlags(
//				obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
//		VoxelSettings.voxelWorld.dynamicsWorld.addRigidBody(obj.body);
////		obj.body.setContactCallbackFlag(VoxelSettings.OBJECT_FLAG);
////		obj.body.setContactCallbackFilter(VoxelSettings.GROUND_FLAG);
//	}
//	
//	public void render(ModelBatch b, Environment e) {
//		b.render(obj, e);
//	}

	public void buildModel() {
		modelBuilder.begin();
		Node n = modelBuilder.node();
		n.id = "MazePlayer";
		modelBuilder.part("MazePlayer", GL20.GL_TRIANGLES, meshAttr, new Material()).box(0.6f, 0.6f, 0.6f);
		MazePlayerModel = modelBuilder.end();
		instance = new ModelInstance(MazePlayerModel);
	}

	public void initPhysics() {
		characterTransform = instance.transform; // Set by reference
		characterTransform.set(startx, starty, startz, 0, 0, 0, 0);

		// Create the physics representation of the character
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(characterTransform);
		ghostShape = new btBoxShape(new Vector3(0.3f, 0.3f, 0.3f));
		ghostObject.setCollisionShape(ghostShape);
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		characterController = new btKinematicCharacterController(ghostObject, ghostShape, .05f, Vector3.Y);

		// And add it to the physics world
		main.world.dynamicsWorld.addCollisionObject(ghostObject,
				(short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
				(short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter
						| btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
		((btDiscreteDynamicsWorld) (main.world.dynamicsWorld)).addAction(characterController);
	}

	public void render(ModelBatch b, Environment e) {
		if (!disposing) {
			inputs();
			b.render(instance, e);
		}
	}

	public void inputs() {
		// If the left or right key is pressed, rotate the character and update its
		// physics update accordingly.
		if (Gdx.input.isKeyPressed(ksx)) {
			characterTransform.rotate(0, 1, 0, 2.5f);
			ghostObject.setWorldTransform(characterTransform);
		}
		if (Gdx.input.isKeyPressed(kdx)) {
			characterTransform.rotate(0, 1, 0, -2.5f);
			ghostObject.setWorldTransform(characterTransform);
		}
		// Fetch which direction the character is facing now
		characterDirection.set(-1, 0, 0).rot(characterTransform).nor();
		// Set the walking direction accordingly (either forward or backward)
		walkDirection.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(kup))
			walkDirection.add(characterDirection);
		if (Gdx.input.isKeyPressed(kdown))
			walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
		walkDirection.scl(3f * Gdx.graphics.getDeltaTime());
		// And update the character controller
		characterController.setWalkDirection(walkDirection);
		// Now we can update the world as normally
		// And fetch the new transformation of the character (this will make the model
		// be rendered correctly)
		ghostObject.getWorldTransform(characterTransform);
	}

	public Vector3 getPos() {
		return instance.transform.getTranslation(new Vector3());
	}

	public void setPos(Vector3 v) {
		if (!disposing) {
			characterTransform.set(v.x, v.y, v.z, 0, 0, 0, 0);
			ghostObject.setWorldTransform(characterTransform);
		}
	}

	public void setPos(float x, float y, float z) {
		if (!disposing) {
			characterTransform.set(x, y, z, 0, 0, 0, 0);
			ghostObject.setWorldTransform(characterTransform);
		}
	}

	public void setName(String name_) {
		this.name = name_;
	}

	public String getName() {
		return name;
	}

	public void setPlaying() {
		disposing = false;
	}

	public void dispose() {
		disposing = true;
		main.world.dynamicsWorld.removeAction(characterController);
		main.world.dynamicsWorld.removeCollisionObject(ghostObject);
		characterController.dispose();
		ghostObject.dispose();
		ghostShape.dispose();
		MazePlayerModel.dispose();
		disposing = false;
	}

}
