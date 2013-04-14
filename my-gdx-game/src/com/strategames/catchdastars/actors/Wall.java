package com.strategames.catchdastars.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.actors.Balloon.Type;
import com.strategames.catchdastars.utils.Textures;

public class Wall extends GameObject {
	private Sprite spriteMiddlePart;
	private Sprite spriteLeftPart;
	private Sprite spriteRightPart;
	private Vector2 startPosition;
	private Vector2 endPosition;
	private Type type;
	private float length;
	
	public enum Type {
		HORIZONTAL, VERTICAL
	}

	public Wall() {
		super();
	}
	
	
	void setupImage() {
		float x = getX();
		float y = getY();
		if( type == Type.HORIZONTAL ) {
			this.spriteMiddlePart = new Sprite(Textures.bricksHorizontal);
			this.spriteLeftPart = new Sprite(Textures.bricksHorizontalEndLeft);
			this.spriteRightPart = new Sprite(Textures.bricksHorizontalEndRight);
			this.startPosition = new Vector2(x - (this.length/2f), y - (this.spriteMiddlePart.getHeight() / 2f));
			this.endPosition = new Vector2(x + (this.length/2f), y - (this.spriteMiddlePart.getHeight() / 2f));
		} else {
			this.spriteMiddlePart = new Sprite(Textures.bricksVertical);
			this.startPosition = new Vector2(x - (this.spriteMiddlePart.getWidth() / 2f), y - (this.length/2f));
			this.endPosition = new Vector2(x - (this.spriteMiddlePart.getWidth() / 2f), y + (this.length/2f));
		}
	}
	
	void setupBox2D() {
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
	}
	
	public static Wall create(World world, float x, float y, float length, Type type) {
		Wall wall = new Wall();
		wall.setPosition(x, y);
		wall.setLength(length);
		wall.setType(type);
		wall.setup(world);
		return wall;
	}
	
	public void setLength(float length) {
		this.length = length;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if ( type == Type.HORIZONTAL ) {
			this.spriteLeftPart.setPosition(this.startPosition.x, this.startPosition.y);
			this.spriteLeftPart.draw(batch);
			
			float stepSize = this.spriteMiddlePart.getWidth();
			float middlePartEndPosition = this.endPosition.x - this.spriteRightPart.getWidth();
			
			for(float xd = this.startPosition.x + this.spriteLeftPart.getWidth(); 
					xd < middlePartEndPosition; 
					xd += stepSize ) {
				this.spriteMiddlePart.setPosition(xd, this.startPosition.y);
				this.spriteMiddlePart.draw(batch);
			}
			
			this.spriteRightPart.setPosition(middlePartEndPosition, this.startPosition.y);
			this.spriteRightPart.draw(batch);
		} else {
			float middlePartEndPosition = this.endPosition.y - this.spriteMiddlePart.getHeight();
			for(float yd = this.startPosition.y; yd < middlePartEndPosition; yd += this.spriteMiddlePart.getHeight() ) {
				this.spriteMiddlePart.setPosition(this.startPosition.x, yd);
				this.spriteMiddlePart.draw(batch);
			}
			this.spriteMiddlePart.setPosition(this.startPosition.x, middlePartEndPosition);
			this.spriteMiddlePart.draw(batch);
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
