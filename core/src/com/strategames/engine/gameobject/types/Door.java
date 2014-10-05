package com.strategames.engine.gameobject.types;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.StaticBody;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.Textures;

public class Door extends StaticBody {
	public final static float WIDTH = 0.35f;
	public final static float HEIGHT = 0.35f;
	
	private Wall wall;
	private boolean open;
	private int[] nextLevelPosition = new int[2];
	
	public Door() {
		super(new Vector2(WIDTH, HEIGHT));
	}
	
	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().passageToNextLevel;
	}
	
	@Override
	protected void setupBody(Body body) {
		Vector2 leftBottom = new Vector2(0, 0);
		Vector2 rightBottom = new Vector2(WIDTH, 0);
		Vector2 rightTop = new Vector2(WIDTH, HEIGHT);
		Vector2 leftTop = new Vector2(0, HEIGHT);
		
		ChainShape chain = new ChainShape();
		chain.createLoop(new Vector2[] {leftBottom, rightBottom, rightTop, leftTop});

		Fixture fixture = body.createFixture(chain, 0.0f);
		fixture.setSensor(true);
	}

	@Override
	protected void writeValues(Json json) {
		json.writeArrayStart("nextLevelPosition");
		json.writeValue(nextLevelPosition[0]);
		json.writeValue(nextLevelPosition[1]);
		json.writeArrayEnd();
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("nextLevelPosition")) {
			this.nextLevelPosition = jsonData.asIntArray();
		}
	}

	@Override
	public GameObject copy() {
		Door door = new Door();
		door.setPosition(getX(), getY());
		int[] pos = getNextLevelPosition();
		door.setNextLevelPosition(pos[0], pos[1]);
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
	
	public Wall getWall() {
		return wall;
	}
	
	public void setWall(Wall wall) {
		this.wall = wall;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setNextLevelPosition(int x, int y) {
		this.nextLevelPosition[0] = x;
		this.nextLevelPosition[1] = y;
	}
	
	public int[] getNextLevelPosition() {
		return nextLevelPosition;
	}
}
