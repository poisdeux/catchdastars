package com.strategames.engine.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
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
	public final static float WIDTH = 0.35f;
	public final static float HEIGHT = 0.35f;
	
	private Wall wall;
	
	public Door() {
		super(new Vector2(WIDTH, HEIGHT));
	}
	
	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().passageToNextLevel;
	}
	
	@Override
	protected Body createBody() {
		return null;
	}

	@Override
	protected void writeValues(Json json) {
		
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		
	}

	@Override
	public GameObject copy() {
		Door door = new Door();
		door.setPosition(getX(), getY());
		return door;
	}

	@Override
	protected GameObject newInstance() {
		return new Door();
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		return null;
	}

	@Override
	public void increaseSize() {
		
	}

	@Override
	public void decreaseSize() {
		
	}

	@Override
	protected void destroyAction() {
		
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse,
			GameObject gameObject) {
		
	}

	@Override
	public void loadSounds() {
		
	}

	@Override
	public void applyForce() {
		
	}
	
	public Wall getWall() {
		return wall;
	}
	
	public void setWall(Wall wall) {
		this.wall = wall;
	}
}
