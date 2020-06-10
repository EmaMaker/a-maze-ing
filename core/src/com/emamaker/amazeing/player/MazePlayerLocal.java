package com.emamaker.amazeing.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.emamaker.amazeing.AMazeIng;

public class MazePlayerLocal extends MazePlayer {

	/*
	 * Player controlled on local machine with mouse and kbd, touch or controller
	 */

	btConvexShape ghostShape;
	public btPairCachingGhostObject ghostObject;
	public btKinematicCharacterController characterController;
	Matrix4 characterTransform;
	Vector3 characterDirection = new Vector3();
	Vector3 walkDirection = new Vector3();
	public Controller ctrl;
	public Touchpad tctrl;
	public int tctrlPosition;

	boolean touchpadPressed = false;
	public TextButton touchpadPowerUp;
	float oldAngle = 0, angle;

	// Physics using LibGDX's bullet wrapper
	public int kup, kdown, ksx, kdx, kpup;
	float startx, starty, startz;

	// Give keys in up, down, left, right order
	public MazePlayerLocal(int... keys) {
		this(keys[0], keys[1], keys[2], keys[3], keys[4]);
	}

	public MazePlayerLocal(int up_, int down_, int sx_, int dx_, int pup_) {
		this(up_, down_, sx_, dx_, pup_, 8, 8, 8);
	}

	public MazePlayerLocal(int up_, int down_, int sx_, int dx_, int pup_, String name) {
		this(up_, down_, sx_, dx_, pup_, 8, 8, 8, name);
	}

	public MazePlayerLocal(int up_, int down_, int sx_, int dx_, int pup_, float startx, float starty, float startz) {
		this(up_, down_, sx_, dx_, pup_, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayerLocal(int up_, int down_, int sx_, int dx_, int pup_, float startx, float starty, float startz,
			String name) {
		super(name, true);
		this.kup = up_;
		this.kdown = down_;
		this.ksx = sx_;
		this.kdx = dx_;
		this.kpup = pup_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;
	}

	public MazePlayerLocal(Controller ctrl_) {
		this(ctrl_, 0, 0, 0);
	}

	public MazePlayerLocal(Controller ctrl_, String name) {
		this(ctrl_, 0, 0, 0, name);
	}

	public MazePlayerLocal(Controller crtl_, float startx, float starty, float startz) {
		this(crtl_, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayerLocal(Controller ctrl_, float startx, float starty, float startz, String name) {
		super(true);
		this.ctrl = ctrl_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;
	}

	public MazePlayerLocal(Touchpad ctrl_, int p) {
		this(ctrl_, p, 0, 0, 0);
	}

	public MazePlayerLocal(Touchpad ctrl_, int p, String name) {
		this(ctrl_, p, 0, 0, 0, name);
	}

	public MazePlayerLocal(Touchpad crtl_, int p, float startx, float starty, float startz) {
		this(crtl_, p, startx, starty, startz, String.valueOf((char) (65 + rand.nextInt(26))));
	}

	public MazePlayerLocal(Touchpad ctrl_, int p, float startx, float starty, float startz, String name) {
		super(true);
		this.tctrl = ctrl_;

		this.startx = startx;
		this.starty = starty;
		this.startz = startz;
		this.tctrlPosition = p;

		tctrl.setResetOnTouchUp(true);

		oldAngle = 0;
		angle = 0;
		touchpadPressed = false;

		touchpadPowerUp = new TextButton("No object to use", AMazeIng.getMain().uiManager.skin);

		tctrl.setSize(Gdx.graphics.getHeight() / 6f, Gdx.graphics.getHeight() / 6f);
		touchpadPowerUp.setSize(Gdx.graphics.getHeight() / 6f, Gdx.graphics.getHeight() / 12f);

		tctrl.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				oldAngle = angle;
				angle = MathUtils.atan2(-tctrl.getKnobPercentY(), -tctrl.getKnobPercentX()) * 180f / MathUtils.PI;

				touchpadPressed = true;
				// System.out.println(angle);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				touchpadPressed = false;
			}
		});

		touchpadPowerUp.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				usePowerUp();
				return true;
			}
		});

		if (tctrlPosition == 0) {
			tctrl.setPosition(tctrl.getWidth() / 2, tctrl.getHeight() / 2);
			touchpadPowerUp.setPosition(tctrl.getWidth() / 2, tctrl.getHeight() * 1.75f);
		} else if (tctrlPosition == 1) {
			tctrl.setPosition(Gdx.graphics.getWidth() - tctrl.getWidth() * 1.5f, tctrl.getHeight() / 2);
			touchpadPowerUp.setPosition(Gdx.graphics.getWidth() - tctrl.getWidth() * 1.5f, tctrl.getHeight() * 1.75f);
		} else if (tctrlPosition == 2) {
			tctrl.setPosition(Gdx.graphics.getWidth() - tctrl.getWidth() * 1.5f,
					Gdx.graphics.getHeight() - tctrl.getHeight() * 1.5f);
			touchpadPowerUp.setPosition(Gdx.graphics.getWidth() - tctrl.getWidth() * 1.5f,
					Gdx.graphics.getHeight() - tctrl.getHeight() * 2.25f);
		} else if (tctrlPosition == 3) {
			tctrl.setPosition(tctrl.getWidth() / 2, Gdx.graphics.getHeight() - tctrl.getHeight() * 1.5f);
			touchpadPowerUp.setPosition(tctrl.getWidth() / 2, Gdx.graphics.getHeight() - tctrl.getHeight() * 2.25f);
		}

	}

	public void initPhysics() {
		super.initPhysics();
		characterTransform = instance.transform; // Set by reference
		characterTransform.set(startx, starty, startz, 0, 0, 0, 0);

		// Create the physics representation of the character
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(characterTransform);
		ghostShape = new btBoxShape(new Vector3(0.3f, 0.5f, 0.3f));
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
		// Update for touchscreen controller is done in touchpad listener
		if (AMazeIng.PLATFORM == AMazeIng.Platform.DESKTOP)
			if (ctrl != null)
				inputController();
			else
				inputKeyboard();
		else
			inputTouchscreen();

//		if (pressed)
//			main.client.updateLocalPlayer(this);
	}

	public void inputController() {

	}

	public void inputKeyboard() {
		if (Gdx.input.isKeyPressed(kdown))
			characterDirection.set(-1, 0, 0).rotate(90, 0, 1, 0).nor();
		if (Gdx.input.isKeyPressed(kup))
			characterDirection.set(-1, 0, 0).rotate(-90, 0, 1, 0).nor();
		if (Gdx.input.isKeyPressed(ksx))
			characterDirection.set(-1, 0, 0).rotate(0, 0, 1, 0).nor();
		if (Gdx.input.isKeyPressed(kdx))
			characterDirection.set(-1, 0, 0).rotate(180, 0, 1, 0).nor();

		if (Gdx.input.isKeyPressed(kdown) || Gdx.input.isKeyPressed(kup) || Gdx.input.isKeyPressed(ksx)
				|| Gdx.input.isKeyPressed(kdx)) {
			walkDirection.set(0, 0, 0);
			walkDirection.add(characterDirection);
			walkDirection.scl(speed * Gdx.graphics.getDeltaTime());
			characterController.setWalkDirection(walkDirection);
			ghostObject.getWorldTransform(characterTransform);

			pressed = true;
		} else {
			walkDirection.set(0, 0, 0);
			characterController.setWalkDirection(walkDirection);
		}

		if (Gdx.input.isKeyJustPressed(kpup))
			usePowerUp();
	}

	public void inputTouchscreen() {
		if (touchpadPressed) {
			touchpadPowerUp.setText("No object to use");
			if (currentPowerUp != null)
				touchpadPowerUp.setText(currentPowerUp.name.toUpperCase() + "!");
			// characterTransform.rotate(0, 1, 0, angle-oldAngle);
			// ghostObject.setWorldTransform(characterTransform);

			// Fetch which direction the character is facing now
			characterDirection.set(-1, 0, 0).rotate(angle, 0, 1, 0).nor();

//            characterDirection.set(-1, 0, 0).rot(characterTransform).nor();
			// Set the walking direction accordingly (either forward or backward)
			walkDirection.set(0, 0, 0);
			walkDirection.add(characterDirection);
			walkDirection.scl(speed * Gdx.graphics.getDeltaTime());
			// And update the character controller
			characterController.setWalkDirection(walkDirection);
			// Now we can update the world as normally
			// And fetch the new transformation of the character (this will make the model
			// be rendered correctly)
			ghostObject.getWorldTransform(characterTransform);

			oldAngle = angle;
			pressed = true;
		} else {
			walkDirection.set(0, 0, 0);
			characterController.setWalkDirection(walkDirection);
		}
	}

	@Override
	public void update() {
		super.update();
		if (initedPhysics && !isDisposed())
			inputs();
	}

	@Override
	protected void updateFromTmpPos() {
		super.updateFromTmpPos();
		if (initedPhysics && !isDisposed())
			pos.set(ghostObject.getWorldTransform().getTranslation(new Vector3()));
	}

//	@Override
//	public void setPos(Vector3 v) {
//		this.setPos(v.x, v.y, v.z);
//	}
//
//	@Override
//	public void setPos(float x, float y, float z) {
//		if (!disposed)
//			setTransform(x, y, z, 0, 0, 0, 0);
//	}

	@Override
	public void setTransform(float x, float y, float z, float i, float j, float k, float l) {
		if (!isDisposed() && initedPhysics) {
			characterTransform.set(x, y, z, i, j, k, l);
			ghostObject.setWorldTransform(characterTransform);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (!isDisposed()) {
			main.world.dynamicsWorld.removeAction(characterController);
			main.world.dynamicsWorld.removeCollisionObject(ghostObject);
			characterController.dispose();
			ghostObject.dispose();
			ghostShape.dispose();
			disposed = true;
		}
	}

	public boolean getPressed() {
		return pressed;
	}

}