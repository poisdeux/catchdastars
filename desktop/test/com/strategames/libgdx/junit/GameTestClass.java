package com.strategames.libgdx.junit;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.utils.Score;
import com.strategames.ui.dialogs.Dialog;

public class GameTestClass extends GameEngine {

	@Override
	public void create() {
		new World(new Vector2(0f, -1f), true); // needed to make sure box2d libraries are loaded
		setScreen(new ScreenTestClass(this));
	}
	
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Array<GameObject> getAvailableGameObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setup(Stage stage) {
		return true;
	}

    @Override
    public void levelComplete(Score score) {

    }

    @Override
    public void levelFailed() {

    }
}
