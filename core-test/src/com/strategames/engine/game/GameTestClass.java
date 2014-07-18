package com.strategames.engine.game;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.screens.ScreenTestClass;

public class GameTestClass extends Game {

	@Override
	public void create() {
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
	public ArrayList<GameObject> getAvailableGameObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
