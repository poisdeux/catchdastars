package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.BodyEditorLoader;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.ConfigurationItem.OnConfigurationItemChangedListener;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class Balloon extends GameObject implements OnConfigurationItemChangedListener {
	private static final float MIN_LIFTFACTOR = 1f;
	private static final float MAX_LIFTFACTOR = 4f;
	private static final float DEFAULT_LIFTFACTOR = 1.6f;

	private static final float WIDTH = 0.30f;
	
	private Vector2 upwardLiftPosition;
	private float upwardLift;
	private float liftFactor = DEFAULT_LIFTFACTOR;

	private float maxVolume = 0.5f;
	/**
	 * New velocity is calculated as follows by Box2D
	 * 
	 * velocity += Game.UPDATE_FREQUENCY_SECONDS * (Game.GRAVITY + ((1f/this.balloon.getMass()) * (this.upwardLift * Game.GRAVITY)));
	 * velocity *= 1.0f - (Game.UPDATE_FREQUENCY_SECONDS * bd.linearDamping);
	 * 
	 * Where bd.linearDamping is set in setupBox2D()
	 * Following value of 28.77593 was determined empirically by checking maximum speed of balloon
	 * in game for maximum liftFactor (see createConfigurationItems)
	 */
	private final float maxVelocitySquared = 28.77593f * 28.77593f * (1/maxVolume);

	public static enum ColorType {
		BLUE, RED
	}

	private ColorType colorType;

	public Balloon() {
		super(new Vector2(WIDTH, -1f));
	}

	public Balloon(Game game, float x, float y, ColorType type) {
		this();
		setGame(game);
		setColorType(type);
		setPosition(x, y);
		setup();
		setLiftFactor(DEFAULT_LIFTFACTOR);
	}
	
//	public static Balloon create(Game game, float x, float y, ColorType type) {
//		Balloon balloon = new Balloon();
//		balloon.setGame(game);
//		balloon.setColorType(type);
//		balloon.setPosition(x, y);
//		balloon.setup();
//		balloon.setLiftFactor(DEFAULT_LIFTFACTOR);
//		return balloon;
//	}

	public void setColorType(ColorType colorType) {
		this.colorType = colorType;
	}

	public ColorType getColorType() {
		return colorType;
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
	TextureRegionDrawable createTexture() {
		TextureRegionDrawable trd = null;
		if( colorType == ColorType.BLUE ) {
			trd = new TextureRegionDrawable(Textures.blueBalloon);
		} else if( colorType == ColorType.RED ) {
			trd = new TextureRegionDrawable(Textures.redBalloon);
		}

		return trd;
	}

	@Override
	Body setupBox2D() {
		World world = getGame().getWorld();
//		float balloonWidth = Game.convertScreenToWorld(getPrefWidth() * getScaleX());
		
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
		this.upwardLiftPosition.y += 0.03f;
 		
		this.upwardLift = -body.getMass() * this.liftFactor;
		
		return body;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		setRotation(MathUtils.radiansToDegrees * super.body.getAngle());
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if( this.game.getGameState() == game.GAME_STATE_RUNNING ) {
			Vector2 worldPointOfForce = super.body.getWorldPoint(this.upwardLiftPosition);
			super.body.applyForce(getWorld().getGravity().mul(this.upwardLift), worldPointOfForce);
		}
	}

	@Override
	void writeValues(Json json) {
		json.writeValue("type", this.colorType.name());
		json.writeValue("liftfactor", this.liftFactor);
	}

	@Override
	void readValue(String key, Object value) {
		if( key.contentEquals("type")) {
			setColorType(ColorType.valueOf(value.toString()));
		} else if( key.contentEquals("liftfactor")) {
			setLiftFactor(Float.valueOf(value.toString()));
		}
	}

	@Override
	public GameObject createCopy() {
		Balloon balloon = new Balloon(getGame(), 
				getX(), 
				getY(),
				this.colorType);
		balloon.setLiftFactor(this.liftFactor);
		return balloon;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		ArrayList<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		ConfigurationItem item = new ConfigurationItem(this);
		item.setName("lift");
		item.setType(ConfigurationItem.Type.NUMERIC_RANGE);
		item.setValueNumeric(this.liftFactor);
		item.setMaxValue(4f);

		item.setMinValue(1f);
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
	public void destroy() {
		Gdx.app.log("Balloon", "destroy");
		if(remove()) {
			Sounds.balloonPop.play();
			setDeleted(true);
		}
	}

	@Override
	protected Type setType() {
		return Type.BALLOON;
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 normal = worldManifold.getNormal();
		float bounceVelocity = super.body.getLinearVelocity().mul(normal.x, normal.y).len2();
		if( bounceVelocity > 0.1 ) {
			Sounds.balloonBounce.play(bounceVelocity / this.maxVelocitySquared);
		}
//		Gdx.app.log("Balloon", "handleCollision: bounceVelocity="+bounceVelocity+
//				", this.maxVelocitySquared="+this.maxVelocitySquared+
//				", this.liftFactor="+this.liftFactor);
	}

	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", colorType="+this.colorType.name());
		return messageBuffer.toString();
	}

	@Override
	public void onConfigurationItemChanged(ConfigurationItem item) {
		if( item.getName().contentEquals("lift") ) {
			setLiftFactor(item.getValueNumeric());
		}
	}
}
