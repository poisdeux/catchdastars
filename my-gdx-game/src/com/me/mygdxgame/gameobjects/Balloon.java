package com.me.mygdxgame.gameobjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.me.mygdxgame.Textures;

public class Balloon extends GameObject {
	private Vector2 localPositionTopOfBalloon;
	private Body knot;
	private Body balloon;
	private Sprite sprite;
	private float angle;
	private float x;
	private float y;
	
	public static Balloon newInstance(World world, float x, float y) {
		return new Balloon(world, x, y);
	}
	
	public Balloon(World world, float x, float y) {
		setPosition(x, y, false);
		setup(world);
	}

	@Override
	public void setPosition(float x, float y, boolean transform) {
		this.x = x;
		this.y = y;
		if( transform ) {
			knot.setTransform(knot.getWorldCenter().x, y, knot.getAngle());
			knot.setAwake(true);
			balloon.setTransform(x, y, balloon.getAngle());
			balloon.setAwake(true);
		}
	}

	private void setup(World world) {
		float scale = 0.6f;
		
		this.sprite = new Sprite(Textures.blueBalloon);
		this.sprite.setScale(scale);
		
		float balloonWidth = this.sprite.getWidth() * scale;
		float balloonHeight = this.sprite.getHeight();
		
		this.localPositionTopOfBalloon = new Vector2(balloonWidth / 2f, balloonHeight);
		
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(this.x, this.y);
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.8f;
		this.balloon = world.createBody(bd);
		this.balloon.setUserData(this);
		
		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 0.32f; 
		fixtureBalloon.friction = 0.2f;
		fixtureBalloon.restitution = 0.8f; // Make it bounce a little bit

		loader.attachFixture(this.balloon, "Balloon", fixtureBalloon, balloonWidth);
		Vector2 origin = loader.getOrigin("Balloon", balloonWidth).cpy();
		
		this.sprite.setOrigin(origin.x, origin.y);
		
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
	public void applyForce(Vector2 force) {
		Vector2 worldPointOfForce = this.balloon.getWorldPoint(this.localPositionTopOfBalloon);
		this.balloon.applyForce(force, worldPointOfForce);
	}

	@Override
	public void draw(SpriteBatch batch) {
		this.sprite.setPosition(this.x, this.y);
		this.sprite.setRotation(this.angle);
		//batch.draw(this.sprite.getTexture(), super.x, super.y);
		this.sprite.draw(batch);
	}

	@Override
	public void setAngle(float angle) {
		this.angle = angle;
	}
}
