package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.ConfigurationItem.OnConfigurationItemChangedListener;
import com.strategames.catchdastars.utils.Textures;

public class Wall extends GameObject implements OnConfigurationItemChangedListener {
	private Sprite spriteMiddlePart;
	private Sprite spriteLeftPart;
	private Sprite spriteRightPart;
	private Orientation orientation;
	private float length;
	private float drawLength;
	private float increaseDecreaseSizeAccumulatedDelta;
	private float stepSize;
	
	public enum Orientation {
		HORIZONTAL, VERTICAL
	}

	public Wall() {
		super();
	}
	
	public Wall(Game game, float x, float y, float length, Orientation type) {
		super();
		setGame(game);
		setPosition(x, y);
		setType(type);
		setup();
		setLength(length);
	}

	@Override
	public void setup() {
		if( orientation == Orientation.HORIZONTAL ) {
			this.spriteMiddlePart = new Sprite(Textures.bricksHorizontal);
			this.spriteMiddlePart.setSize(Game.convertWorldToBox(this.spriteMiddlePart.getWidth()), 
					Game.convertWorldToBox(this.spriteMiddlePart.getHeight()));
			this.spriteLeftPart = new Sprite(Textures.bricksHorizontalEndLeft);
			this.spriteLeftPart.setSize(Game.convertWorldToBox(this.spriteLeftPart.getWidth()), 
					Game.convertWorldToBox(this.spriteLeftPart.getHeight()));
			this.spriteRightPart = new Sprite(Textures.bricksHorizontalEndRight);
			this.spriteRightPart.setSize(Game.convertWorldToBox(this.spriteRightPart.getWidth()), 
					Game.convertWorldToBox(this.spriteRightPart.getHeight()));
		} else {
			this.spriteMiddlePart = new Sprite(Textures.bricksVertical);
			this.spriteMiddlePart.setSize(Game.convertWorldToBox(this.spriteMiddlePart.getWidth()), 
					Game.convertWorldToBox(this.spriteMiddlePart.getHeight()));
		}

		setDrawable(new TextureRegionDrawable(this.spriteMiddlePart));
		setScaling(Scaling.stretch);
		setLength(this.length);

		super.setup();
	}

	@Override
	TextureRegionDrawable createTexture() {
		return null;
	}

	@Override
	Body setupBox2D() {
		PolygonShape box = new PolygonShape();  
		if( orientation == Orientation.HORIZONTAL ) {
			box.setAsBox(this.length/2f, super.halfHeight, new Vector2(this.length/2f, super.halfHeight), 0f);
		} else {
			box.setAsBox(super.halfWidth, this.length/2f, new Vector2(super.halfWidth, this.length/2f), 0f);
		}

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(getX(), getY()); // Set its world position
		Body body = getWorld().createBody(groundBodyDef);
		body.createFixture(box, 0.0f); //Attach the box we created horizontally or vertically to the body
		box.dispose();
		return body;
	}


//	public static Wall create(Game game, float x, float y, float length, Orientation type) {
//		Wall wall = new Wall();
//		wall.setGame(game);
//		wall.setPosition(x, y);
//		wall.setType(type);
//		wall.setup();
//		wall.setLength(length);
//		return wall;
//	}

	/**
	 * Returns the size increment for growing/shrinking this object
	 * @return
	 */
	public float getStepSize() {
		return stepSize;
	}
	
	/**
	 * Sets the length of this object. This can only be called after {@link #setup()} has been called.
	 * @param length in Box2D
	 */
	public void setLength(float length) {
		float width = this.spriteMiddlePart.getWidth();
		float height = this.spriteMiddlePart.getHeight();
		
		if( orientation == Orientation.HORIZONTAL ) {
			this.length = length < width ? width : length; //Make sure length is not smaller than a single block
			setWidth(this.length);
			setHeight(height);
			this.stepSize = width;		
			this.drawLength = this.length - (this.stepSize * 1.9f);
		} else {
			this.length = length < height ? height : length; //Make sure length is not smaller than a single block
			setHeight(this.length);
			setWidth(width);
			this.stepSize = height;
			this.drawLength = this.length - (this.stepSize * 0.9f);
		}
	}

	public void setType(Orientation type) {
		this.orientation = type;
		setName(getClass().getSimpleName() + " " + type.name().toLowerCase());
	}
	
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Vector2 v = super.body.getPosition();
		float x = v.x;
		float y = v.y;
		setPosition(x, y);
		
		if ( orientation == Orientation.HORIZONTAL ) {
			this.spriteLeftPart.setPosition(x, y);
			this.spriteLeftPart.draw(batch, parentAlpha);

			float middlePartEndPosition = x + this.drawLength;

			float xd;
			for(xd = x + stepSize; 
					xd < middlePartEndPosition; 
					xd += stepSize ) {
				this.spriteMiddlePart.setPosition(xd, y);
				this.spriteMiddlePart.draw(batch, parentAlpha);
			}

			this.spriteRightPart.setPosition(xd, y);
			this.spriteRightPart.draw(batch, parentAlpha);
		} else {
			float middlePartEndPosition = y + this.drawLength;
			for( float yd = y; yd < middlePartEndPosition; yd += stepSize ) {
				this.spriteMiddlePart.setPosition(x, yd);
				this.spriteMiddlePart.draw(batch, parentAlpha);
			}
		}
		
//		drawBoundingBox(batch);
	}

	@Override
	void writeValues(Json json) {
		json.writeValue("type", this.orientation.name());
		json.writeValue("length", this.length);
	}

	@Override
	void readValue(String key, Object value) {
		if( key.contentEquals("type")) {
			this.orientation = Orientation.valueOf(value.toString());
		} else if( key.contentEquals("length")) {
			this.length = Float.valueOf(value.toString());
		}
	}

	@Override
	public GameObject createCopy() {
		GameObject object = new Wall(getGame(), 
				getX(), 
				getY(),
				this.length,
				this.orientation);
		return object;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		ArrayList<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		ConfigurationItem item = new ConfigurationItem(this);
		item.setName("length");
		item.setType(ConfigurationItem.Type.NUMERIC_RANGE);
		item.setValueNumeric(this.length);
		item.setMaxValue(Game.convertWorldToBox(Gdx.app.getGraphics().getWidth()) + this.stepSize);

		item.setMinValue(this.stepSize);
		item.setStepSize(this.stepSize);
		
		items.add(item);

		return items;
	}

	@Override
	public void increaseSize() {
		this.increaseDecreaseSizeAccumulatedDelta += this.stepSize;

		if( this.increaseDecreaseSizeAccumulatedDelta > this.stepSize ) {
			this.increaseDecreaseSizeAccumulatedDelta = 0;

			setLength(this.length + this.stepSize);
		}
	}

	@Override
	public void decreaseSize() {
		this.increaseDecreaseSizeAccumulatedDelta -= this.stepSize;
		
		if( Math.abs(this.increaseDecreaseSizeAccumulatedDelta) > this.stepSize ) {
			this.increaseDecreaseSizeAccumulatedDelta = 0;

			setLength(this.length - this.stepSize);
		}
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		this.spriteMiddlePart.setColor(r, g, b, a);
		if( orientation == Orientation.HORIZONTAL ) {
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
		messageBuffer.append(", type="+this.orientation);
		return messageBuffer.toString();
	}

	@Override
	public void destroy() {
		if(remove() && deleteBody()) {
			setDeleted(true);
		}
	}

	@Override
	protected Type setType() {
		return Type.WALL;
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		// TODO Auto-generated method stub

	}
}
