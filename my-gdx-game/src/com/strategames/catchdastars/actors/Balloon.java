package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class Balloon extends GameObject {
	private Vector2 upwardLiftPosition;
	private float upwardLift;

	private float velocity;
	private Body balloon;

	private float maxVolume = 0.2f;
	/**
	 * New velocity is calculated as follows by Box2D
	 * 
	 * velocity += Game.UPDATE_FREQUENCY_SECONDS * (Game.GRAVITY + ((1f/this.balloon.getMass()) * (this.upwardLift * Game.GRAVITY)));
	 * velocity *= 1.0f - (Game.UPDATE_FREQUENCY_SECONDS * bd.linearDamping);
	 * 
	 * Where bd.linearDamping is set in 
	 * 
	 */
	private final float maxVelocitySquared = 8100f * (1/maxVolume); // (45 * 2) ^ 2  * maxVolume

	public static enum ColorType {
		BLUE, RED
	}

	private ColorType colorType;

	public Balloon() { }

	public static Balloon create(World world, float x, float y, ColorType type) {
		Balloon balloon = new Balloon();
		balloon.setColorType(type);
		balloon.setPosition(x, y);
		balloon.setWorld(world);
		balloon.setup();
		return balloon;
	}

	public void setColorType(ColorType colorType) {
		this.colorType = colorType;
	}

	public ColorType getColorType() {
		return colorType;
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
		World world = getWorld();
		float balloonWidth = Game.convertWorldToBox(getPrefWidth() * getScaleX());

		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 1f;
		bd.linearDamping=1f;
		this.balloon = world.createBody(bd);

		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 0.1786f;  // Helium density 0.1786 g/l == 0.1786 kg/m3
		fixtureBalloon.friction = 0.8f;
		fixtureBalloon.restitution = 0.4f; // Make it bounce a little bit
		loader.attachFixture(this.balloon, "Balloon", fixtureBalloon, balloonWidth);

		this.upwardLiftPosition = this.balloon.getLocalCenter();
		this.upwardLiftPosition.y += 0.1f;

		this.upwardLift = -this.balloon.getMass() * 1.4f;// * Game.UPDATE_FREQUENCY_SECONDS; //f = mv/t

//		for( int i = 0; i < 1/Game.UPDATE_FREQUENCY_SECONDS; i++) {
//			velocity += Game.UPDATE_FREQUENCY_SECONDS * ((1.0f * 9.81f) + ((1f/this.balloon.getMass()) * (this.upwardLift * 9.81f)));
//			velocity *= 1.0f - (Game.UPDATE_FREQUENCY_SECONDS * 1f);
//		}
		
		return this.balloon;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		setRotation(MathUtils.radiansToDegrees * this.balloon.getAngle());
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);		
		Vector2 worldPointOfForce = this.balloon.getWorldPoint(this.upwardLiftPosition);
		this.balloon.applyForce(getWorld().getGravity().mul(this.upwardLift), worldPointOfForce);

		Gdx.app.log("Balloon", "act: linearVelocity="+this.balloon.getLinearVelocity()+" calculatedMax="+
				velocity);
	}

	@Override
	public void write(Json json) {
		moveTo(getX(), getY()); // align body with image origin
		super.write(json);
	}

	@Override
	void writeValues(Json json) {
		json.writeValue("type", this.colorType.name());
	}

	@Override
	void readValue(String key, Object value) {
		if( key.contentEquals("type")) {
			this.colorType = ColorType.valueOf(value.toString());
		}
	}

	@Override
	public GameObject createCopy() {
		GameObject object = Balloon.create(getWorld(), 
				getX(), 
				getY(), 
				colorType);
		return object;
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
		float bounceVelocity = this.balloon.getLinearVelocity().mul(normal.x, normal.y).len2();
		if( bounceVelocity > 100 ) {
			Sounds.balloonBounce.play(bounceVelocity / this.maxVelocitySquared);
		}
		Gdx.app.log("Balloon", "handleCollision: bounceVelocity="+bounceVelocity+", this.maxVelocitySquared="+this.maxVelocitySquared);
	}

	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", colorType="+this.colorType.name());
		return messageBuffer.toString();
	}
}
