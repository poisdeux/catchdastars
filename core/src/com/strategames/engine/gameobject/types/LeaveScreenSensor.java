package com.strategames.engine.gameobject.types;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.StaticBody;
import com.strategames.engine.utils.ConfigurationItem;

public class LeaveScreenSensor extends StaticBody {

	/**
	 * Create a sensor object surrounding world
	 * @param size is ignored so passing null would be best
	 */
	public LeaveScreenSensor(Vector2 size) {
		super(size);
	}
	
	@Override
	protected TextureRegion createImage() {
		return null;
	}

	@Override
	protected void setupBody(Body body) {
		Game game = getGame();
		
		Vector3 worldSize = game.getWorldSize();
		Vector2 beginning = new Vector2(0, 0).add(-Wall.WIDTH, -Wall.HEIGHT);
		Vector2 end = new Vector2(worldSize.x, worldSize.y).add(Wall.WIDTH, Wall.HEIGHT);

		Vector2 leftBottom = new Vector2(beginning.x, beginning.y);
		Vector2 rightBottom = new Vector2(end.x, beginning.y);
		Vector2 rightTop = new Vector2(end.x, end.y);
		Vector2 leftTop = new Vector2(beginning.x, end.y);
		
		ChainShape chain = new ChainShape();
		chain.createLoop(new Vector2[] {leftBottom, rightBottom, rightTop, leftTop});
		Fixture fixture = body.createFixture(chain, 0.0f);
		fixture.setSensor(true);
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
