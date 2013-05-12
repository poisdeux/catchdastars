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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class Balloon extends GameObject {
	private Vector2 localPositionTopOfBalloon;
	private Body balloon;
	
	private float maxVolume = 0.1f;
	/**
	 * Box2D limits the acceleration per timestep to 2 m/s.
	 * Therefore the maximum speed any object can obtain is
	 * maxSpeed = worldTimeStep * 2
	 */
	private final float maxVelocitySquared = 8100f * (1/maxVolume); // (45 * 2) ^ 2  * maxVolume
	
	public static enum ColorType {
		BLUE
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
		}
		return trd;
	}

	@Override
	Body setupBox2D() {
		World world = getWorld();
		float balloonWidth = getPrefWidth() * getScaleX();
		float balloonHeight = getPrefHeight() * getScaleY();

		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.8f;
		this.balloon = world.createBody(bd);
		
		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 0.1786f;  // Helium density 0.1786 g/l == 0.1786 kg/m3
		fixtureBalloon.friction = 0.2f;
		fixtureBalloon.restitution = 0.6f; // Make it bounce a little bit
		loader.attachFixture(this.balloon, "Balloon", fixtureBalloon, balloonWidth);
		
		this.localPositionTopOfBalloon = this.balloon.getLocalCenter();
		this.localPositionTopOfBalloon.y += balloonHeight / 2f;
		
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
		Vector2 worldPointOfForce = this.balloon.getWorldPoint(this.localPositionTopOfBalloon);
		this.balloon.applyForce(getWorld().getGravity().mul(this.balloon.getMass()).mul(-4f), worldPointOfForce);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Type setType() {
		return Type.BALLOON;
	}

	@Override
	public void handleCollision(Contact contact, GameObject gameObject) {
		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 normal = worldManifold.getNormal();
		float bounceVelocity = this.balloon.getLinearVelocity().mul(normal).len2();
		if( bounceVelocity > 100 ) {
			Sounds.balloonBounce.play(bounceVelocity / this.maxVelocitySquared);
		}
	}
}
