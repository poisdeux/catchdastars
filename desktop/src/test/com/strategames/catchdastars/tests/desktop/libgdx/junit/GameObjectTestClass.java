package com.strategames.catchdastars.tests.desktop.libgdx.junit;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.utils.ConfigurationItem;

public class GameObjectTestClass extends GameObject {

	@Override
	protected TextureRegion createImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Body createBody(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeValues(Json json) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readValue(JsonValue jsonData) {
		// TODO Auto-generated method stub

	}

	@Override
	public GameObject copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameObject newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void increaseSize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void decreaseSize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destroyAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse,
			GameObject gameObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadSounds() {
		// TODO Auto-generated method stub

	}

}
