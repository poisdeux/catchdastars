package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

/**
 * @author martijn brekhof
 * 
 */

public class Star extends GameObject {
	private final static float WIDTH = 0.30f;
	private float rotationSpeed;
//	private float scale = 1f;
	
	public static enum ColorType {
		BLUE, 
		RED,
		YELLOW
	}

	public ColorType colorType;
	
	public Star() {
		super(new Vector2(WIDTH, -1f));
	}
	
	public Star(Game game, float x, float y, ColorType type) {
		this();
		setGame(game);
		setColorType(type);
		setPosition(x, y);
		setup();
		setCollectible(true);
	}
	
	public void setColorType(ColorType colorType) {
		this.colorType = colorType;
	}
	
	public ColorType getColorType() {
		return colorType;
	}
	
	public void setRotationSpeed(float speed) {
		this.rotationSpeed = speed;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		rotateBy(this.rotationSpeed);
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
		super.draw(batch, parentAlpha);
//		drawBoundingBox(batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	void writeValues(Json json) {
		json.writeValue("type", this.colorType.name());
		json.writeValue("rotationSpeed", this.rotationSpeed);
	}
	
	@Override
	void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("type")) {
			this.colorType = ColorType.valueOf(jsonData.asString());
		} else if( name.contentEquals("rotationSpeed")) {
			this.rotationSpeed = jsonData.asFloat();
		}
	}
	
	@Override
	TextureRegionDrawable createTexture() {
		TextureRegionDrawable trd = null;
		if( colorType == ColorType.BLUE ) {
			trd = new TextureRegionDrawable(Textures.starBlue);
		} else if ( colorType == ColorType.RED ) {
			trd = new TextureRegionDrawable(Textures.starRed);
		} else if ( colorType == ColorType.YELLOW ) {
			trd = new TextureRegionDrawable(Textures.starYellow);
		}
		
		return trd;
	}
	
	@Override
	Body setupBox2D() {
		float radius = getHalfWidth() * 0.7f;
		
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		circle.setPosition(new Vector2(getHalfWidth(), getHalfHeight()));
		BodyDef bd = new BodyDef();  
		bd.position.set(getX(), getY());
		Body body = getWorld().createBody(bd);  
		Fixture fixture = body.createFixture(circle, 0.0f);
		fixture.setSensor(true);
		
		circle.dispose();
		
		return body;
	}
	
	
	@Override
	public GameObject copy() {
		GameObject object = new Star(getGame(), 
				getX(), 
				getY(), 
				colorType);
		return object;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void increaseSize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void decreaseSize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		if(remove()) {
			Sounds.glass.play();
			if(deleteBody()) {
				setDeleted(true);
			}
		}
	}

	@Override
	protected Type setType() {
		return Type.STAR;
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", colorType="+this.colorType.name());
		return messageBuffer.toString();
	}
}
