package com.strategames.catchdastars.actors;

import java.util.ArrayList;

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
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.ConfigurationItem.OnConfigurationItemChangedListener;
import com.strategames.catchdastars.utils.Textures;

public class Wall extends GameObject implements OnConfigurationItemChangedListener {
	private Sprite spriteMiddlePart;
	private Sprite spriteLeftPart;
	private Sprite spriteRightPart;
	private Type type;
	private float length;
	private float increaseDecreaseSizeAccumulatedDelta;

	public enum Type {
		HORIZONTAL, VERTICAL
	}

	public Wall() {
		super();
	}

	@Override
	TextureRegionDrawable createTexture() {
		if( type == Type.HORIZONTAL ) {
			this.spriteMiddlePart = new Sprite(Textures.bricksHorizontal);
			this.spriteLeftPart = new Sprite(Textures.bricksHorizontalEndLeft);
			this.spriteRightPart = new Sprite(Textures.bricksHorizontalEndRight);
		} else {
			this.spriteMiddlePart = new Sprite(Textures.bricksVertical);
		}
		
		setLength(this.length);
		
		return null;
	}

	@Override
	Body setupBox2D() {
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
		wall.setType(type);
		wall.setWorld(world);
		wall.setup();
		return wall;
	}

	public void setLength(float length) {
		if( this.spriteMiddlePart == null ) {
			createTexture();
		}
		
		float width = this.spriteMiddlePart.getWidth();
		float height = this.spriteMiddlePart.getHeight();
		if( type == Type.HORIZONTAL ) {
			this.length = length < width ? width : length; //Make sure length is not smaller than a single block
			setWidth(this.length);
			setHeight(height);
		} else {
			this.length = length < height ? height : length; //Make sure length is not smaller than a single block
			setHeight(this.length);
			setWidth(width);
		}
	}

	public void setType(Type type) {
		this.type = type;
		setName(getClass().getSimpleName() + " " + type.name().toLowerCase());
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Vector2 v = getBody().getWorldCenter();
		float x = v.x - super.halfWidth;
		float y = v.y - super.halfHeight;
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
		
		setPosition(x, y);
		drawBoundingBox(batch);
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
			setLength(Float.valueOf(value.toString()));
		}
	}

	@Override
	public GameObject createCopy() {
		Gdx.app.log("Wall", "createCopy:");
		GameObject object = Wall.create(getWorld(), 
				getX(), 
				getY(),
				this.length,
				this.type);
		return object;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		ArrayList<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		ConfigurationItem item = new ConfigurationItem(this);
		item.setName("length");
		item.setType(ConfigurationItem.Type.NUMERIC_RANGE);
		item.setValueNumeric(this.length);
		item.setMaxValue(Gdx.app.getGraphics().getWidth());

		if( type == Type.HORIZONTAL ) {
			item.setMinValue(this.spriteMiddlePart.getWidth());
			item.setStepSize(this.spriteMiddlePart.getWidth());
		} else {
			item.setMinValue(this.spriteMiddlePart.getHeight());
			item.setStepSize(this.spriteMiddlePart.getHeight());
		}

		items.add(item);

		return items;
	}

	@Override
	public void increaseSize() {
		this.increaseDecreaseSizeAccumulatedDelta += 1f;

		if( this.increaseDecreaseSizeAccumulatedDelta > this.spriteMiddlePart.getWidth() ) {
			this.increaseDecreaseSizeAccumulatedDelta = 0;

			if( type == Type.HORIZONTAL ) {
				setLength(this.length + this.spriteMiddlePart.getWidth());
			} else {
				setLength(this.length + this.spriteMiddlePart.getHeight());
			}
		}
	}

	@Override
	public void decreaseSize() {
		this.increaseDecreaseSizeAccumulatedDelta -= 1f;

		if( Math.abs(this.increaseDecreaseSizeAccumulatedDelta) > this.spriteMiddlePart.getWidth() ) {
			this.increaseDecreaseSizeAccumulatedDelta = 0;

			if( type == Type.HORIZONTAL ) {
				setLength(this.length - this.spriteMiddlePart.getWidth());
			} else {
				setLength(this.length - this.spriteMiddlePart.getHeight());
			}
		}
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		this.spriteMiddlePart.setColor(r, g, b, a);
		if( type == Type.HORIZONTAL ) {
			this.spriteLeftPart.setColor(r, g, b, a);
			this.spriteRightPart.setColor(r, g, b, a);
		}
	}

	@Override
	public void onConfigurationItemChanged(ConfigurationItem item) {
		if( item.getName().contentEquals("length") ) {
			setLength(item.getValueNumeric());
		}
	}
	
	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", length="+this.length);
		messageBuffer.append(", type="+this.type);
		return messageBuffer.toString();
	}
}
