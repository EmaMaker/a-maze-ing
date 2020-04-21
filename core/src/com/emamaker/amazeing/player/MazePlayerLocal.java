package com.emamaker.amazeing.player;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

public class MazePlayerLocal extends MazePlayer {

	/*
	 * Player controlled on local machine with mouse and kbd, touch or controller
	 * (in a remote future=
	 */

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

	public MazePlayerLocal(Game main_, int up_, int down_, int sx_, int dx_) {
		this(main_, up_, down_, sx_, dx_, 0, 0, 0);
	}

	public MazePlayerLocal(Game main_, int up_, int down_, int sx_, int dx_, String name) {
		this(main_, up_, down_, sx_, dx_, 0, 0, 0, name);
	}

	public MazePlayerLocal(Game main_, int up_, int down_, int sx_, int dx_, float startx, float starty, float startz) {
		this(main_, up_, down_, sx_, dx_, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayerLocal(Game main_, int up_, int down_, int sx_, int dx_, float startx, float starty, float startz,
			String name) {
		super(main_, name, true);
		this.kup = up_;
		this.kdown = down_;
		this.ksx = sx_;
		this.kdx = dx_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;

		initPhysics();
	}

	public MazePlayerLocal(Game main_, Controller ctrl_) {
		this(main_, ctrl_, 0, 0, 0);
	}

	public MazePlayerLocal(Game main_, Controller ctrl_, String name) {
		this(main_, ctrl_, 0, 0, 0, name);
	}

	public MazePlayerLocal(Game main_, Controller crtl_, float startx, float starty, float startz) {
		this(main_, crtl_, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayerLocal(Game main_, Controller ctrl_, float startx, float starty, float startz, String name) {
		super(main_, true);
		this.ctrl = ctrl_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;

		initPhysics();
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

	boolean pressed = false;

	public void inputs() {
		pressed = false;
		// If the left or right key is pressed, rotate the character and update its
		// physics update accordingly.
		if (Gdx.input.isKeyPressed(ksx)) {
			pressed = true;
			characterTransform.rotate(0, 1, 0, 2.5f);
			ghostObject.setWorldTransform(characterTransform);
		}
		if (Gdx.input.isKeyPressed(kdx)) {
			pressed = true;
			characterTransform.rotate(0, 1, 0, -2.5f);
			ghostObject.setWorldTransform(characterTransform);
		}
		// Fetch which direction the character is facing now
		characterDirection.set(-1, 0, 0).rot(characterTransform).nor();
		// Set the walking direction accordingly (either forward or backward)
		walkDirection.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(kup)) {
			pressed = true;
			walkDirection.add(characterDirection);
		}
		if (Gdx.input.isKeyPressed(kdown)) {
			pressed = false;
			walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
		}
		walkDirection.scl(3f * Gdx.graphics.getDeltaTime());
		// And update the character controller
		characterController.setWalkDirection(walkDirection);
		// Now we can update the world as normally
		// And fetch the new transformation of the character (this will make the model
		// be rendered correctly)
		ghostObject.getWorldTransform(characterTransform);

		if (pressed)
			main.client.updateLocalPlayer(this);
	}

	@Override
	public void update() {
		inputs();
	}

	@Override
	public void setPos(Vector3 v) {
		this.setPos(v.x, v.y, v.z);
	}

	@Override
	public void setPos(float x, float y, float z) {
		if (!disposing)
			setTransform(x, y, z, 0, 0, 0, 0);
	}

	@Override
	public void setTransform(float x, float y, float z, float i, float j, float k, float l) {
		if (!disposing) {
			characterTransform.set(x, y, z, i, j, k, l);
			ghostObject.setWorldTransform(characterTransform);
		}
	}

	@Override
	public void dispose() {
		disposing = true;
		main.world.dynamicsWorld.removeAction(characterController);
		main.world.dynamicsWorld.removeCollisionObject(ghostObject);
		characterController.dispose();
		ghostObject.dispose();
		ghostShape.dispose();
		mazePlayerModel.dispose();
		disposing = false;
	}

}
