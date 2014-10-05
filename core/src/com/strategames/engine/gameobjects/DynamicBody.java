package com.strategames.engine.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

abstract public class DynamicBody extends GameObject {

	public DynamicBody(Vector2 size) {
		super(size);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TextureRegion createImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Body createBody(World world) {
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		Body body = world.createBody(bd);
		setupBody(body);
		return body;
	}

	/**
	 * Called prior to updating the physics world (Box2D) so you can
	 * apply forces to the gameobject.
	 */
	abstract public void applyForce();

	/**
	 * Called after {@link #createTextureRegionDrawable()} to create the Box2D body of the game object.
	 */
	abstract protected void setupBody(Body body);
}
