package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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
	private final static float WIDTH = 0.30f;
	private final static float HEIGHT = 0.30f;

	private Color colorActor;
	private Orientation orientation;
	private float length;
	private float increaseDecreaseSizeAccumulatedDelta;
	private float stepSize;

	private float startHorizontalMiddlePart;
	private float startHorizontalEndPart;
	private float endHorizontalMiddlePart;
	
	private int amountOfParts;

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}

	public Wall() {
		super(new Vector2(WIDTH, -1f));
	}

	public Wall(Game game, float x, float y, float length, Orientation type) {
		this();
		setGame(game);
		setPosition(x, y);
		setType(type);
		setup();
		setLength(length);
	}

	@Override
	public void setup() {
		this.colorActor = getColor();

		Sprite sprite = new Sprite(Textures.bricksHorizontal);
		setDrawable(new TextureRegionDrawable(sprite));
		setScaling(Scaling.stretch);
		setLength(this.length);

		Textures.bricksHorizontal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		Textures.bricksVertical.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
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
			box.setAsBox(super.halfWidth, super.halfHeight, new Vector2(super.halfWidth, super.halfHeight), 0f);
		} else {
			box.setAsBox(super.halfWidth, super.halfHeight, new Vector2(super.halfWidth, super.halfHeight), 0f);
		}

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(getX(), getY()); // Set its world position
		Body body = getWorld().createBody(groundBodyDef);
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
		Gdx.app.log("Wall", "setLength (before): Orientation="+orientation.name()+", getWidth()="+getWidth()+", getHeight()="+getHeight()+
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
		
		this.startHorizontalMiddlePart = getX() + this.stepSize;
		this.endHorizontalMiddlePart = this.length - this.stepSize;
		this.startHorizontalEndPart = getX() + this.length - this.stepSize;
		
		this.amountOfParts = (int) (this.length / this.stepSize);

		Gdx.app.log("Wall", "setLength (after): getWidth()="+getWidth()+", getHeight()="+getHeight()+
				", this.length="+this.length+
				", this.amountOfParts="+this.amountOfParts);
	}

	public void setType(Orientation type) {
		this.orientation = type;
		setName(getClass().getSimpleName() + " " + type.name().toLowerCase());
	}


	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float x = getX();
		float y = getY();
		setPosition(x, y);
		float prevAlpha = batch.getColor().a;
		batch.getColor().a = this.colorActor.a;

		if ( orientation == Orientation.HORIZONTAL ) {

			batch.draw(Textures.bricksHorizontalEndLeft, x, y, this.stepSize, getHeight());

			if( this.length > this.stepSize ) {
				batch.draw(Textures.bricksHorizontal, this.startHorizontalMiddlePart, y, this.endHorizontalMiddlePart, getHeight(), 0, 0, this.amountOfParts, -1);			
			}

			batch.draw(Textures.bricksHorizontalEndRight, this.startHorizontalEndPart, y, this.stepSize, getHeight());
		} else {
			batch.draw(Textures.bricksVertical, x, y, getWidth(), this.length, 0, 0, -1, this.amountOfParts);
		}

		batch.getColor().a = prevAlpha;

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
