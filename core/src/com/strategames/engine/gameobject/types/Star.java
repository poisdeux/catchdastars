package com.strategames.engine.gameobject.types;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.StaticBody;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.ConfigurationItem;

/**
 * @author martijn brekhof
 * 
 */

abstract public class Star extends StaticBody {
	private final static float WIDTH = 0.30f;
	private float rotationSpeed;

	private SoundEffect soundCollected;

	protected Star() {
		super(new Vector2(WIDTH, -1f));
		setCollectible(true);
		this.soundCollected = getSoundCollected();
	}

	public void setRotationSpeed(float speed) {
		this.rotationSpeed = speed;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		rotateBy(this.rotationSpeed);
		super.draw(batch, parentAlpha);
		//		drawBoundingBox(batch);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	protected void writeValues(Json json) {
		json.writeValue("rotationSpeed", this.rotationSpeed);
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("rotationSpeed")) {
			this.rotationSpeed = jsonData.asFloat();
		}
	}

	@Override
	protected void setupBody(Body body) {
		float radius = getHalfWidth() * 0.7f;

		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		circle.setPosition(new Vector2(getHalfWidth(), getHalfHeight()));
		Fixture fixture = body.createFixture(circle, 0.0f);
		fixture.setSensor(true);

		circle.dispose();
	}

	@Override
	public void loadSounds() {
		if( this.soundCollected != null ) {
			this.soundCollected.load();
		}
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
	public void destroyAction() {
		this.soundCollected.play();
		addAction( sequence( parallel(
				fadeOut( 0.8f ) , repeat(2, sequence( rotateTo(5f, 0.2f, Interpolation.linear), rotateTo(-5f, 0.2f, Interpolation.linear)) )),
				new Action() {

			@Override
			public boolean act(float delta) {
				setCanBeRemoved(true);
				return true;
			}
		}));
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public GameObject copy() {
		Star star = (Star) newInstance();
		star.setPosition(getX(), getY());
		star.setRotationSpeed(this.rotationSpeed);
		star.setGame(getGame());
		return star;
	}

	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		return messageBuffer.toString();
	}

	abstract protected SoundEffect getSoundCollected();
}
