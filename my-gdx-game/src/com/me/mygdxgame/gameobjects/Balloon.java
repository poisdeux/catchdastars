package com.me.mygdxgame.gameobjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Textures;

public class Balloon extends GameObject {
	public Vector2 origin;
	
	public Balloon(World world, float x, float y) {
		super(new Sprite(Textures.blueBalloon), x, y);
		setup(world);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
	}

	private void setup(World world) {
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		BodyDef bd = new BodyDef();
		bd.position.set(super.x, super.y);
		bd.type = BodyType.DynamicBody;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.32f; 
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.4f; // Make it bounce a little bit

		Body body = world.createBody(bd);
		body.setUserData(this);
		
		loader.attachFixture(body, "Balloon", fixtureDef, super.sprite.getWidth());
		
		this.origin = loader.getOrigin("Balloon", super.sprite.getWidth()).cpy();
		this.sprite.setOrigin(this.origin.x, this.origin.y);
	}
}
