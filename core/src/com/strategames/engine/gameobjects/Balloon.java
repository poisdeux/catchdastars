package com.strategames.engine.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.utils.BodyEditorLoader;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.ConfigurationItem.OnConfigurationItemChangedListener;
import com.strategames.engine.utils.Sounds;

abstract public class Balloon extends GameObject implements OnConfigurationItemChangedListener {
	private static final float MIN_LIFTFACTOR = 1f;
	private static final float MAX_LIFTFACTOR = 2f;
	private static final float DEFAULT_LIFTFACTOR = 1.3f;

	private static final float WIDTH = 0.30f;

	private Vector2 upwardLiftPosition;
	private float upwardLift;
	private float liftFactor = DEFAULT_LIFTFACTOR;

	private static Sounds sounds = Sounds.getInstance();

	private static final float maxVolume = 0.5f;

	/**
	 * Following value was determined empirically
	 */
	private static final float maxImpulse = 0.05f / maxVolume;

	protected Balloon() {
		super(new Vector2(WIDTH, -1f));
	}

	public void setLiftFactor(float liftFactor) {
		if( liftFactor > MAX_LIFTFACTOR ) {
			liftFactor = MAX_LIFTFACTOR;
		} else if( liftFactor < MIN_LIFTFACTOR ) {
			liftFactor = MIN_LIFTFACTOR;
		}
		this.liftFactor = liftFactor;
	}

	public float getLiftFactor() {
		return liftFactor;
	}

	@Override
	protected Body setupBox2D() {
		World world = getGame().getWorld();

		/**
		 * TODO do we really need a separate loader for each object? Replace with static?
		 */
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));
		loader.setupVertices("Balloon", WIDTH);

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 1f;
		bd.linearDamping=1f;
		Body body = world.createBody(bd);

		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 0.1786f;  // Helium density 0.1786 g/l == 0.1786 kg/m3
		fixtureBalloon.friction = 0.6f;
		fixtureBalloon.restitution = 0.4f; // Make it bounce a little bit
		loader.attachFixture(body, "Balloon", 0, fixtureBalloon);

		this.upwardLiftPosition = body.getLocalCenter();
		this.upwardLiftPosition.y += 0.1f;

		this.upwardLift = -body.getMass() * this.liftFactor;

		return body;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setRotation(MathUtils.radiansToDegrees * super.body.getAngle());
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if( super.game.getGameState() == game.GAME_STATE_RUNNING ) {
			Vector2 worldPointOfForce = super.body.getWorldPoint(this.upwardLiftPosition);
			super.body.applyForce(getGame().getWorld().getGravity().scl(this.upwardLift), worldPointOfForce, true);
		}
	}

	@Override
	protected void writeValues(Json json) {
		json.writeValue("liftfactor", this.liftFactor);
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("liftfactor")) {
			setLiftFactor(Float.valueOf(jsonData.asFloat()));
		}
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		ArrayList<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		ConfigurationItem item = new ConfigurationItem(this);
		item.setName("lift");
		item.setType(ConfigurationItem.Type.NUMERIC_RANGE);
		item.setValueNumeric(this.liftFactor);
		item.setMaxValue(MAX_LIFTFACTOR);

		item.setMinValue(MIN_LIFTFACTOR);
		item.setStepSize(0.1f);

		items.add(item);

		return items;
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
		sounds.play(sounds.balloonPop, 1);
		setCanBeRemoved(true);
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		float[] impulses = impulse.getNormalImpulses();
		if( impulses[0] > 0.01 ) {
			sounds.play(sounds.balloonBounce, (float) (impulses[0] / maxImpulse));
		}
	}

	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", liftFactor="+this.liftFactor);
		return messageBuffer.toString();
	}

	@Override
	public void onConfigurationItemChanged(ConfigurationItem item) {
		if( item.getName().contentEquals("lift") ) {
			setLiftFactor(item.getValueNumeric());
		}
	}

	@Override
	public GameObject copy() {
		Balloon balloon = (Balloon) newInstance();
		balloon.setPosition(getX(), getY());
		balloon.setLiftFactor(getLiftFactor());
		balloon.setGame(getGame());
		balloon.setDrawable(getDrawable());
		return balloon;
	}
}
