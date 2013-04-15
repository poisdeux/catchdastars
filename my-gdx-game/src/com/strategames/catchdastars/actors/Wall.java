package com.strategames.catchdastars.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.utils.Textures;

public class Wall extends GameObject {
	private Sprite spriteMiddlePart;
	private Sprite spriteLeftPart;
	private Sprite spriteRightPart;
	private Vector2 halfSize;
	private Type type;
	private float length;
	
	public enum Type {
		HORIZONTAL, VERTICAL
	}

	public Wall() {
		super();
	}
	
	@Override
	TextureRegionDrawable createTexture() {
		TextureRegionDrawable trd = null;
		if( type == Type.HORIZONTAL ) {
			trd = new TextureRegionDrawable(Textures.bricksHorizontal);
			this.spriteMiddlePart = new Sprite(Textures.bricksHorizontal);
			this.spriteLeftPart = new Sprite(Textures.bricksHorizontalEndLeft);
			this.spriteRightPart = new Sprite(Textures.bricksHorizontalEndRight);
			
			//Make sure length is not smaller than a single block
			this.length = this.length < this.spriteMiddlePart.getWidth() ? this.spriteMiddlePart.getWidth() : this.length;
			
			this.halfSize = new Vector2(this.length / 2f, this.spriteMiddlePart.getHeight() / 2f);
		} else {
			trd = new TextureRegionDrawable(Textures.bricksVertical);
			this.spriteMiddlePart = new Sprite(Textures.bricksVertical);
			this.halfSize = new Vector2(this.spriteMiddlePart.getWidth() / 2f, this.length / 2f);
		}
		return trd;
	}
	
	@Override
	Body setupBox2D() {
		Gdx.app.log("Wall",	"setupBox2D: length="+this.length);
		
		PolygonShape box = new PolygonShape();  
		if( type == Type.HORIZONTAL ) {
			box.setAsBox(this.length/2f, this.spriteMiddlePart.getHeight()/2f);
		} else {
			box.setAsBox(this.spriteMiddlePart.getWidth()/2f, this.length/2f);
		}
		
		BodyDef groundBodyDef = new BodyDef();  
		groundBodyDef.position.set(getX(), getY()); // Set its world position
		Body body = getWorld().createBody(groundBodyDef);
		body.createFixture(box, 0.0f); //Attach the box we created horizontally or vertically to the body
		box.dispose();
		
		return body;
	}
	
	public static Wall create(World world, float x, float y, float length, Type type) {
		Wall wall = new Wall();
		wall.setPosition(x, y);
		wall.setLength(length);
		wall.setType(type);
		wall.setWorld(world);
		wall.setup();
		return wall;
	}
	
	public void setLength(float length) {
		this.length = length;
	}
	
	public void setType(Type type) {
		this.type = type;
		setName(getClass().getSimpleName() + " " + type.name().toLowerCase());
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float x = getX() - this.halfSize.x;
		float y = getY() - this.halfSize.y;
		if ( type == Type.HORIZONTAL ) {
			this.spriteLeftPart.setPosition(x, y);
			this.spriteLeftPart.draw(batch);
			
			float stepSize = this.spriteMiddlePart.getWidth();
			float middlePartEndPosition = x + this.length - stepSize;
			
			for(float xd = x + stepSize; 
					xd < middlePartEndPosition; 
					xd += stepSize ) {
				this.spriteMiddlePart.setPosition(xd, y);
				this.spriteMiddlePart.draw(batch);
			}
			
			this.spriteRightPart.setPosition(middlePartEndPosition, y);
			this.spriteRightPart.draw(batch);
		} else {
			float stepSize = this.spriteMiddlePart.getHeight();
			float middlePartEndPosition = y + this.length;
			
			for(float yd = y; 
					yd < middlePartEndPosition; 
					yd += stepSize ) {
				this.spriteMiddlePart.setPosition(x, yd);
				this.spriteMiddlePart.draw(batch);
			}
		}
	}

	@Override
	void writeValues(Json json) {
		json.writeValue("type", this.type.name());
		json.writeValue("length", this.length);
	}

	@Override
	void readValue(String key, Object value) {
		Gdx.app.log("Wall", "readValue: key="+key+", value="+value.toString());
		if( key.contentEquals("type")) {
			this.type = Type.valueOf(value.toString());
		} else if( key.contentEquals("length")) {
			this.length = Float.valueOf(value.toString());
		}
	}
	
	@Override
	public GameObject createCopy() {
		GameObject object = Wall.create(getWorld(), 
					getX(), 
					getY(),
					this.length,
					this.type);
		return object;
	}
}
