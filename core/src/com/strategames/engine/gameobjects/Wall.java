package com.strategames.engine.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.ConfigurationItem.OnConfigurationItemChangedListener;
import com.strategames.engine.utils.Textures;

public class Wall extends GameObject implements OnConfigurationItemChangedListener {
	public final static float WIDTH = 0.30f;
	public final static float HEIGHT = 0.30f;

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}
	private Orientation orientation = Orientation.HORIZONTAL;
	
	private float length = WIDTH;
	private float increaseDecreaseSizeAccumulatedDelta;
	private float stepSize;

	private float startHorizontalMiddlePart;
	private float startHorizontalEndPart;
	private float endHorizontalMiddlePart;

	private int amountOfParts;

	private Vector2 pos = new Vector2();

	private boolean isBorder;

	private Textures textures = Textures.getInstance();
	
	/**
	 * Creates a wall object with type horizontal and default length
	 */
	public Wall() {
		super(new Vector2(WIDTH, HEIGHT));
	}

	/**
	 * Use this to specify this wall as part of the game border
	 * @param isBorder
	 */
	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	/**
	 * Use this to determine if this wall is part of the game border
	 * @return true if wall is part of border, false otherwise
	 */
	public boolean isBorder() {
		return this.isBorder;
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		pos.x = x;
		pos.y = y;
		setupParts();
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		pos.x = x;
		setupParts();
	}
	
	@Override
	public void setY(float y) {
		super.setY(y);
		pos.y = y;
		setupParts();
	}
	
	@Override
	public void setup() {
		setScaling(Scaling.stretch);
		this.textures.bricksHorizontal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.textures.bricksVertical.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		super.setup();
	}
	
	@Override
	protected TextureRegion createTextureRegion() {
		return new TextureRegion(this.textures.bricksHorizontal);
	}

	@Override
	protected Body setupBox2D() {
		Gdx.app.log("Wall", this+": setupBox2D: halfWidth="+super.halfWidth+", halfHeight="+halfHeight);
		PolygonShape box = new PolygonShape();  
		if( orientation == Orientation.HORIZONTAL ) {
			box.setAsBox(super.halfWidth, super.halfHeight, new Vector2(super.halfWidth, super.halfHeight), 0f);
		} else {
			box.setAsBox(super.halfWidth, super.halfHeight, new Vector2(super.halfWidth, super.halfHeight), 0f);
		}

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(getX(), getY()); // Set its world position
		Body body = getGame().getWorld().createBody(groundBodyDef);
		body.createFixture(box, 0.0f); //Attach the box we created horizontally or vertically to the body
		box.dispose();

		return body;
	}

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
				Gdx.app.log("Wall", this+": setLength (before): Orientation="+orientation.name()+", getWidth()="+getWidth()+", getHeight()="+getHeight()+
						", length="+length);

		if( orientation == Orientation.HORIZONTAL ) {
			this.length = length < WIDTH ? WIDTH : length; //Make sure length is not smaller than a single block
			setWidth(this.length);
			setHeight(HEIGHT);
			this.stepSize = WIDTH;		
		} else {
			this.length = length < HEIGHT ? HEIGHT : length; //Make sure length is not smaller than a single block
			setHeight(this.length);
			setWidth(WIDTH);
			this.stepSize = HEIGHT;
		}

		Gdx.app.log("Wall", this+": setLength (after): Orientation="+orientation.name()+", getWidth()="+getWidth()+", getHeight()="+getHeight()+
				", length="+length);
		
		setupParts();

//				Gdx.app.log("Wall", "setLength (after): getWidth()="+getWidth()+", getHeight()="+getHeight()+
//						", this.length="+this.length+
//						", this.amountOfParts="+this.amountOfParts);
	}

	public float getLength() {
		return length;
	}
	
	public void setOrientation(Orientation type) {
		this.orientation = type;
		setName(getClass().getSimpleName() + " " + type.name().toLowerCase());
	}

	public Orientation getOrientation() {
		return orientation;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		float prevAlpha = batch.getColor().a;
		batch.getColor().a = getColor().a;

		if ( orientation == Orientation.HORIZONTAL ) {
			if( this.length > this.stepSize ) {
				batch.draw(this.textures.bricksHorizontalEndLeft, pos.x, pos.y, WIDTH, HEIGHT);
				batch.draw(this.textures.bricksHorizontal, this.startHorizontalMiddlePart, pos.y, this.endHorizontalMiddlePart, HEIGHT, 0, 0, this.amountOfParts, -1);			
				batch.draw(this.textures.bricksHorizontalEndRight, this.startHorizontalEndPart, pos.y, WIDTH, HEIGHT);
			} else { // draw single brick
				batch.draw(this.textures.bricksVertical, pos.x, pos.y, WIDTH, HEIGHT);
			}
		} else {
			batch.draw(this.textures.bricksVertical, pos.x, pos.y, WIDTH, this.length, 0, 0, -1, this.amountOfParts);
		}

		batch.getColor().a = prevAlpha;

		//		drawBoundingBox(batch);
	}

	@Override
	protected void writeValues(Json json) {
		json.writeValue("type", this.orientation.name());
		json.writeValue("length", this.length);
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("type")) {
			this.orientation = Orientation.valueOf(jsonData.asString());
		} else if( name.contentEquals("length")) {
			this.length = jsonData.asFloat();
		}
	}

	@Override
	public GameObject copy() {
		Wall object = (Wall) newInstance();
		object.setPosition(getX(), getY());
		object.setLength(getLength());
		object.setOrientation(getOrientation());
		object.setGame(getGame());
		return object;
	}

	@Override
	protected GameObject newInstance() {
		return new Wall();
	}
	
	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		ArrayList<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		ConfigurationItem item = new ConfigurationItem(this);
		item.setName("length");
		item.setType(ConfigurationItem.Type.NUMERIC_RANGE);
		item.setValueNumeric(this.length);
		item.setMaxValue(Game.convertScreenToWorld(Gdx.app.getGraphics().getWidth()) + this.stepSize);

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
	public void destroyAction() {
		setCanBeDeleted(true);
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveTo(float x, float y) {
		if( this.body != null ) {
			this.body.setTransform(x, y, this.body.getAngle());
		}
		setPosition(x, y);
	}

	private void setupParts() {
		this.startHorizontalMiddlePart = this.pos.x + this.stepSize;
		this.endHorizontalMiddlePart = this.length - (2 * this.stepSize);
		this.startHorizontalEndPart = this.startHorizontalMiddlePart + this.endHorizontalMiddlePart; 
		this.amountOfParts = ((int) (this.length / this.stepSize)) - 2;
	}
}
