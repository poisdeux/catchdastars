package com.strategames.catchdastars.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
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
	
	public Star() {
	}
	
	public static Star create(World world, float x, float y, Type type) {
		Star star = new Star();
		star.setType(type);
		star.setPosition(x, y);
		star.setWorld(world);
		star.setup();
		return star;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public void setRotationSpeed(float speed) {
		this.rotationSpeed = speed;
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
		json.writeValue("type", this.type.name());
		json.writeValue("rotationSpeed", this.rotationSpeed);
	}

	@Override
	void readValue(String key, Object value) {
		Gdx.app.log("Star", "readValue: key="+key+", value="+value.toString());
		if( key.contentEquals("type")) {
			this.type = Type.valueOf(value.toString());
		} else if( key.contentEquals("rotationSpeed")) {
			this.rotationSpeed = Float.valueOf(value.toString());
		}
	}
	
	@Override
	TextureRegionDrawable createTexture() {
		TextureRegionDrawable trd = null;
		if( type == Type.BLUE ) {
			trd = new TextureRegionDrawable(Textures.starBlue);
		} else if ( type == Type.RED ) {
			trd = new TextureRegionDrawable(Textures.starRed);
		} else if ( type == Type.YELLOW ) {
			trd = new TextureRegionDrawable(Textures.starYellow);
		}
		return trd;
	}
	
	@Override
	Body setupBox2D() {
		setScale(this.scale);
		float halfWidth = getPrefWidth() / 2f;
		
		CircleShape circle = new CircleShape();
		circle.setRadius((halfWidth * this.scale) * 0.7f);
		
		BodyDef bd = new BodyDef();  
		bd.position.set(new Vector2(getX(), getY()));
		this.star = getWorld().createBody(bd);  
		Fixture fixture = this.star.createFixture(circle, 0.0f);
		fixture.setSensor(true);
		circle.dispose();
		
		return this.star;
	}
	
	
	@Override
	public GameObject createCopy() {
		GameObject object = Star.create(getWorld(), 
				getX(), 
				getY(), 
				type);
		return object;
	}
}
