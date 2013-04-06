package com.strategames.catchdastars.actors;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.utils.Textures;

public class Balloon extends GameObject {
	private Body knot;
	private Body balloon;
	private Vector2 localPositionTopOfBalloon;
	private World world;
	private float balloonHalfWidth;
	private float balloonHalfHeight;
	
	public static enum Type {
		BLUE
	}

	private Type type;

	public Balloon() { }

	public static Balloon create(World world, float x, float y, Type type) {
		Balloon balloon = new Balloon();
		balloon.setType(type);
		balloon.setPosition(x, y);
		balloon.setScale(0.6f);
		balloon.setup(world);
		return balloon;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public void setup(World world) {
		this.world = world;
		setupImage();
		setupBox2D(world);
	}

	private void setupImage() {
		TextureRegionDrawable trd = null;
		if( type == Type.BLUE ) {
			trd = new TextureRegionDrawable(Textures.blueBalloon);
		}
		setDrawable(trd);
		setScaling(Scaling.none);
	}
	
	private void setupBox2D(World world) {
		float balloonWidth = getPrefWidth() * getScaleX();
		float balloonHeight = getPrefHeight() * getScaleY();
		
		this.balloonHalfWidth = balloonWidth / 2f;
		this.balloonHalfHeight = balloonHeight / 2f;
		
		setOrigin(-this.balloonHalfWidth, -this.balloonHalfHeight);
		
		this.localPositionTopOfBalloon = new Vector2(balloonWidth / 2f, balloonHeight);

		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.8f;
		this.balloon = world.createBody(bd);

		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 10.33f;  // Helium density 
		fixtureBalloon.friction = 0.2f;
		fixtureBalloon.restitution = 0.9f; // Make it bounce a little bit

		loader.attachFixture(this.balloon, "Balloon", fixtureBalloon, balloonWidth);
		Vector2 origin = loader.getOrigin("Balloon", balloonWidth).cpy();
		
		//Balloon knot
		bd = new BodyDef();
		Vector2 knotPosition = new Vector2(this.balloon.getWorldPoint(origin));
		knotPosition.x += balloonWidth * 0.7f;
		bd.position.set(knotPosition);
		bd.type = BodyType.DynamicBody;
		this.knot = world.createBody(bd);

		PolygonShape shape = new PolygonShape();
		float knotSize = balloonWidth/30f;
		shape.setAsBox(knotSize, knotSize);
		this.knot.createFixture(shape, fixtureBalloon.density * 40f);

		WeldJointDef wd = new WeldJointDef();
		wd.initialize(this.balloon, knot, this.balloon.getWorldCenter());
		world.createJoint(wd);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		setPosition(
				this.balloon.getPosition().x + this.balloonHalfWidth, 
				this.balloon.getPosition().y + this.balloonHalfHeight
				);
		setRotation(MathUtils.radiansToDegrees * this.balloon.getAngle());
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);		
		Vector2 worldPointOfForce = this.balloon.getWorldPoint(this.localPositionTopOfBalloon);
		this.balloon.applyForce(this.world.getGravity().mul(this.balloon.getMass()).mul(-4f), worldPointOfForce);
	}

	@Override
	void writeValues(Json json) {
		json.writeValue("type", this.type.name());
	}

	@Override
	void readValue(String key, Object value) {
		Gdx.app.log("Balloon", "readValue: key="+key+", value="+value.toString());
		if( key.contentEquals("type")) {
			this.type = Type.valueOf(value.toString());
		}
	}
}
