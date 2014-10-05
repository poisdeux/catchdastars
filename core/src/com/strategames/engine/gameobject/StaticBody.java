package com.strategames.engine.gameobject;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

abstract public class StaticBody extends GameObject {

	public StaticBody(Vector2 size) {
		super(size);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TextureRegion createImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Body createBody(World world) {
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.StaticBody;
		Body body = world.createBody(bd);
		setupBody(body);
		return body;
	}
	
	/**
	 * Called after {@link #createTextureRegionDrawable()} to create the Box2D body of the game object.
	 */
	abstract protected void setupBody(Body body);
}
