package com.strategames.catchdastars.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.utils.Textures;

/**
 * @author martijn brekhof
 * 
 */

public class Star extends GameObject {
	private float rotationSpeed;
	private Body star;
	private float scale = 0.6f;
	
	public static enum Type {
		BLUE, 
		RED,
		YELLOW
	}

	public Type type;
	
	private Star(World world, float x, float y, Type type, TextureRegionDrawable trd) {
		super(trd, Scaling.none);
		this.type = type;
		setName(getClass().getSimpleName() + "_" + type.name());
		
		setScale(this.scale);
		setOrigin(getPrefWidth() / 2f, getPrefHeight() / 2f);
		float halfWidth = getPrefWidth() / 2f;
		float halfHeight = getPrefHeight() / 2f;
		setPosition(x - halfWidth, y - halfHeight);
		
		CircleShape circle = new CircleShape();
		circle.setRadius((halfWidth * this.scale) * 0.7f);
		
		BodyDef bd = new BodyDef();  
		bd.position.set(new Vector2(x, y));
		this.star = world.createBody(bd);  
		Fixture fixture = this.star.createFixture(circle, 0.0f);
		fixture.setSensor(true);
		circle.dispose();
		
		this.rotationSpeed = getRotationSpeed();
	}
	
	public static Star create(World world, float x, float y, Type type) {
		TextureRegionDrawable trd = null;
		if( type == Type.BLUE ) {
			trd = new TextureRegionDrawable(Textures.starBlue);
		} else if ( type == Type.RED ) {
			trd = new TextureRegionDrawable(Textures.starRed);
		} else if ( type == Type.YELLOW ) {
			trd = new TextureRegionDrawable(Textures.starYellow);
		}
		
		return new Star(world, x, y, type, trd);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		setPosition(getX(), getY());
		rotate(this.rotationSpeed);
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	
	}

	@Override
	void writeValues(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void readValue(String key, Object value) {
		// TODO Auto-generated method stub
		
	}
}
