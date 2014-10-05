package com.strategames.engine.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.Game;
import com.strategames.engine.sounds.BalloonBounceSound;
import com.strategames.engine.sounds.BalloonPopSound;
import com.strategames.engine.utils.BodyEditorLoader;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.ConfigurationItem.OnConfigurationItemChangedListener;

abstract public class Balloon extends DynamicBody implements OnConfigurationItemChangedListener {
	private static final float MIN_LIFTFACTOR = 1f;
	private static final float MAX_LIFTFACTOR = 2f;
	private static final float DEFAULT_LIFTFACTOR = 1.4f;

	private static final float WIDTH = 0.30f;

	private Vector2 upwardLiftPosition;
	private float upwardLift;
	private float liftFactor = DEFAULT_LIFTFACTOR;

	private BalloonPopSound soundBalloonPop = new BalloonPopSound();
	private BalloonBounceSound soundBalloonBounce = new BalloonBounceSound();
	
	private static final float maxVolume = 0.5f;
	
	/**
	 * Following value was determined empirically
	 */
	private static final float maxImpulse = 0.08f / maxVolume;

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
	protected void setupBody(Body body) {

		/**
		 * TODO do we really need a separate loader for each object? Replace with static?
		 */
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));
		loader.setupVertices("Balloon", WIDTH);

		//Balloon body
		body.setAngularDamping(1f);
		body.setLinearDamping(1f);
		
		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 0.1786f;  // Helium density 0.1786 g/l == 0.1786 kg/m3
		fixtureBalloon.friction = 0.6f;
		fixtureBalloon.restitution = 0.4f; // Make it bounce a little bit
		loader.attachFixture(body, "Balloon", 0, fixtureBalloon);

		this.upwardLiftPosition = body.getLocalCenter();
		this.upwardLiftPosition.y += 0.1f;

		this.upwardLift = -body.getMass() * this.liftFactor;
	}

	@Override
	public void applyForce() {
		Game game = getGame();
//		if( game.isRunning() ) {
			Vector2 worldPointOfForce = super.body.getWorldPoint(this.upwardLiftPosition);
			super.body.applyForce(game.getWorld().getGravity().scl(this.upwardLift), worldPointOfForce, true);
//		}
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
		this.soundBalloonPop.play();
		setCanBeRemoved(true);
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		float[] impulses = impulse.getNormalImpulses();
		if( impulses[0] > 0.04 ) {
			//If impulse is greater then maxImpulse we play volume at max (i.e. 1f)
			//otherwise we divide impulse by maxImpulse to make sound softer if balloon
			//hits object softer
			float volume = impulses[0] > maxImpulse ? 1f : (impulses[0] / maxImpulse);
			this.soundBalloonBounce.play(volume);
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
	
	@Override
	public void loadSounds() {
		this.soundBalloonBounce.load();
		this.soundBalloonPop.load();
	}
}
