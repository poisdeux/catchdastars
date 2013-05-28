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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.ConfigurationItem.OnConfigurationItemChangedListener;
import com.strategames.catchdastars.utils.Textures;

public class Wall extends GameObject implements OnConfigurationItemChangedListener {
	private Sprite spriteMiddlePart;
	private Sprite spriteLeftPart;
	private Sprite spriteRightPart;
	private Orientation orientation;
	private float length;
	private float increaseDecreaseSizeAccumulatedDelta;

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}

	public Wall() {
		super();
	}

	@Override
	public void setup() {
		if( orientation == Orientation.HORIZONTAL ) {
			this.spriteMiddlePart = new Sprite(Textures.bricksHorizontal);
			this.spriteLeftPart = new Sprite(Textures.bricksHorizontalEndLeft);
			this.spriteRightPart = new Sprite(Textures.bricksHorizontalEndRight);
		} else {
			this.spriteMiddlePart = new Sprite(Textures.bricksVertical);
		}

		setDrawable(new TextureRegionDrawable(this.spriteMiddlePart));
		setScaling(Scaling.none);

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


	public static Wall create(World world, float x, float y, float length, Orientation type) {
		Wall wall = new Wall();
		wall.setPosition(x, y);
		wall.setType(type);
		wall.setWorld(world);
		wall.setup();
		wall.setLength(length);
		return wall;
	}

	/**
	 * Sets the length of this object. This can only be called after {@link #setup()} has been called.
	 * @param length
	 */
	public void setLength(float length) {
		float width = this.spriteMiddlePart.getWidth();
		float height = this.spriteMiddlePart.getHeight();
		if( orientation == Orientation.HORIZONTAL ) {
			this.length = length < width ? width : length; //Make sure length is not smaller than a single block
			setWidth(this.length);
			setHeight(height);
		} else {
			this.length = length < height ? height : length; //Make sure length is not smaller than a single block
			setHeight(this.length);
			setWidth(width);
		}
	}

	public void setType(Orientation type) {
		this.orientation = type;
		setName(getClass().getSimpleName() + " " + type.name().toLowerCase());
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Vector2 v = getBody().getWorldCenter();
		float x = v.x - super.halfWidth;
		float y = v.y - super.halfHeight;
		if ( orientation == Orientation.HORIZONTAL ) {
			this.spriteLeftPart.setPosition(x, y);
			this.spriteLeftPart.draw(batch, parentAlpha);

			float stepSize = this.spriteMiddlePart.getWidth();
			float middlePartEndPosition = x + this.length - stepSize;

			for(float xd = x + stepSize; 
					xd < middlePartEndPosition; 
					xd += stepSize ) {
				this.spriteMiddlePart.setPosition(xd, y);
				this.spriteMiddlePart.draw(batch, parentAlpha);
			}

			this.spriteRightPart.setPosition(middlePartEndPosition, y);
			this.spriteRightPart.draw(batch, parentAlpha);
		} else {
			float stepSize = this.spriteMiddlePart.getHeight();
			float middlePartEndPosition = y + this.length;

			for(float yd = y; 
					yd < middlePartEndPosition; 
					yd += stepSize ) {
				this.spriteMiddlePart.setPosition(x, yd);
				this.spriteMiddlePart.draw(batch, parentAlpha);
			}
		}

		setPosition(x, y);
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
		GameObject object = Wall.create(getWorld(), 
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
		item.setMaxValue(Gdx.app.getGraphics().getWidth());

		if( orientation == Orientation.HORIZONTAL ) {
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

			if( orientation == Orientation.HORIZONTAL ) {
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

			if( orientation == Orientation.HORIZONTAL ) {
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
