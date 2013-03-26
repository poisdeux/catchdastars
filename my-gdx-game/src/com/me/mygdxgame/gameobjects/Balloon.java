package com.me.mygdxgame.gameobjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
	
	public static Balloon newInstance(World world, float x, float y) {
		return new Balloon(world, x, y);
	}
	
	public Balloon(World world, float x, float y) {
		super(x, y);
		setup(world);
	}

	@Override
	public void setPosition(float x, float y, boolean transform) {
		super.setPosition(x, y, transform);
		if( transform ) {
			knot.setTransform(knot.getWorldCenter().x, y, knot.getAngle());
			knot.setAwake(true);
			balloon.setTransform(x, y, balloon.getAngle());
			balloon.setAwake(true);
		}
	}

	private void setup(World world) {
		Sprite sprite = new Sprite(Textures.blueBalloon);
		super.setSprite(sprite);
		
		this.localPositionTopOfBalloon = new Vector2(sprite.getWidth() / 2f, sprite.getHeight());
		
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(super.x, super.y);
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.8f;
		this.balloon = world.createBody(bd);
		this.balloon.setUserData(this);
		
		FixtureDef fixtureBalloon = new FixtureDef();
		fixtureBalloon.density = 0.32f; 
		fixtureBalloon.friction = 0.2f;
		fixtureBalloon.restitution = 0.8f; // Make it bounce a little bit

		loader.attachFixture(this.balloon, "Balloon", fixtureBalloon, sprite.getWidth());
		Vector2 origin = loader.getOrigin("Balloon", sprite.getWidth()).cpy();
		
		sprite.setOrigin(origin.x, origin.y);
		
		//Balloon knot
		bd = new BodyDef();
		Vector2 knotPosition = new Vector2(this.balloon.getWorldPoint(origin));
		knotPosition.x += sprite.getWidth() * 0.7f;
		bd.position.set(knotPosition);
		bd.type = BodyType.DynamicBody;
		this.knot = world.createBody(bd);
		
		PolygonShape shape = new PolygonShape();
		float knotSize = sprite.getWidth()/30f;
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
}
