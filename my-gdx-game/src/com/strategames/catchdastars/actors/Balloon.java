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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.utils.Textures;

public class Balloon extends GameObject {
	private Body balloon;
	private Vector2 localPositionTopOfBalloon;

	public static enum Type {
		BLUE
	}

	private Type type;

	public Balloon() { }

	public static Balloon create(World world, float x, float y, Type type) {
		Balloon balloon = new Balloon();
		balloon.setType(type);
		balloon.setPosition(x, y);
		balloon.setup(world);
		return balloon;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	void setupImage() {
		TextureRegionDrawable trd = null;
		if( type == Type.BLUE ) {
			trd = new TextureRegionDrawable(Textures.blueBalloon);
		}
		setDrawable(trd);
		setScaling(Scaling.none);
	}

	@Override
	void setupBox2D() {
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
		fixtureBalloon.density = 10.33f;  // Helium density 
		fixtureBalloon.friction = 0.2f;
		fixtureBalloon.restitution = 0.6f; // Make it bounce a little bit

		loader.attachFixture(this.balloon, "Balloon", fixtureBalloon, balloonWidth);

		this.localPositionTopOfBalloon = this.balloon.getLocalCenter();
		this.localPositionTopOfBalloon.y += balloonHeight / 2f;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Vector2 balloonPos = this.balloon.getWorldCenter();
		setPosition(balloonPos.x, balloonPos.y);	    
		setRotation(MathUtils.radiansToDegrees * this.balloon.getAngle());
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);		
		Vector2 worldPointOfForce = this.balloon.getWorldPoint(this.localPositionTopOfBalloon);
		this.balloon.applyForce(getWorld().getGravity().mul(this.balloon.getMass()).mul(-4f), worldPointOfForce);
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

	@Override
	public GameObject createCopy() {
		GameObject object = Balloon.create(getWorld(), 
				getX(), 
				getY(), 
				type);
		return object;
	}
}
