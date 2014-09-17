package com.strategames.engine.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.Textures;

public class Door extends GameObject {
	public final static float WIDTH = 0.30f;
	public final static float HEIGHT = 0.30f;
	
	public Door() {
		super(new Vector2(WIDTH, HEIGHT));
	}
	
	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().passageToNextLevel;
	}

	@Override
	protected Body createBody() {
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

	@Override
	public void applyForce() {
		// TODO Auto-generated method stub
		
	}

}
