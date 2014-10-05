package com.strategames.engine.gameobject.types;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.StaticBody;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.ConfigurationItem.OnConfigurationItemChangedListener;

abstract public class Wall extends StaticBody implements OnConfigurationItemChangedListener {
	public final static float WIDTH = 0.30f;
	public final static float HEIGHT = 0.30f;

	private float length = WIDTH;
	private float partSize;

	private boolean isBorder;

	/**
	 * Creates a wall object with type horizontal and default length
	 */
	protected Wall() {
		super(new Vector2(WIDTH, HEIGHT));
	}

	/**
	 * Use this to specify this wall as part of the game border
	 * @param isBorder
	 */
	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	/**
	 * Use this to determine if this wall is part of the game border
	 * @return true if wall is part of border, false otherwise
	 */
	public boolean isBorder() {
		return this.isBorder;
	}

	@Override
	protected void setupBody(Body body) {
		PolygonShape box = new PolygonShape();  
		box.setAsBox(super.halfWidth, super.halfHeight, new Vector2(super.halfWidth, super.halfHeight), 0f);
		body.createFixture(box, 0.0f);
		box.dispose();
	}

	/**
	 * Sets the length of this object.
	 * @param length in Box2D
	 */
	public void setLength(float length) {
		this.length = length;
	}

	public float getLength() {
		return length;
	}

	public void setPartSize(float partSize) {
		this.partSize = partSize;
	}

	public float getPartSize() {
		return partSize;
	}


	@Override
	protected void writeValues(Json json) {
		json.writeValue("length", this.length);
		json.writeValue("border", this.isBorder);
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("length")) {
			setLength(jsonData.asFloat());
		} else if ( name.contentEquals("border")) {
			setBorder(jsonData.asBoolean());
		}
	}

	@Override
	public GameObject copy() {
		Wall object = (Wall) newInstance();
		object.setPosition(getX(), getY());
		object.setLength(getLength());
		object.setGame(getGame());
		object.setTextureRegion(getTextureRegion());
		object.setPartSize(getPartSize());
		return object;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		ArrayList<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		ConfigurationItem item = new ConfigurationItem(this);
		item.setName("length");
		item.setType(ConfigurationItem.Type.NUMERIC_RANGE);
		item.setValueNumeric(this.length);
		item.setMaxValue(Game.convertScreenToWorld(Gdx.app.getGraphics().getWidth()) + this.partSize);

		item.setMinValue(this.partSize);
		item.setStepSize(this.partSize);

		items.add(item);

		return items;
	}

	@Override
	public void increaseSize() {
		setLength(this.length + this.partSize);
	}

	@Override
	public void decreaseSize() {
		setLength(this.length - this.partSize);
	}

	@Override
	public void onConfigurationItemChanged(ConfigurationItem item) {
		if( item.getName().contentEquals("length") ) {
			setLength(item.getValueNumeric());
		}
	}

	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", length="+this.length);
		return messageBuffer.toString();
	}

	@Override
	public void destroyAction() {
		setCanBeRemoved(true);
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void loadSounds() {
		// no sounds for walls
	}
}
