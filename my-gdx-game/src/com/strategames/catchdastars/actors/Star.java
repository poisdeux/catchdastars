package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

/**
 * @author martijn brekhof
 * 
 */

public class Star extends GameObject {
	private float rotationSpeed;
	private Body star;
	private float scale = 1f;
	
	public static enum ColorType {
		BLUE, 
		RED,
		YELLOW
	}

	public ColorType colorType;
	
	public Star() {
	}
	
	public static Star create(World world, float x, float y, ColorType type) {
		Star star = new Star();
		star.setColorType(type);
		star.setPosition(x, y);
		star.setWorld(world);
		star.setup();
		star.setCollectible(true);
		return star;
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
	public void draw(SpriteBatch batch, float parentAlpha) {
		rotate(this.rotationSpeed);
//		Vector2 v = super.body.getWorldCenter();
		Vector2 v = super.body.getPosition();
		setPosition(v.x - super.halfWidth, v.y - super.halfHeight);
		super.draw(batch, parentAlpha);
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
	void readValue(String key, Object value) {
		if( key.contentEquals("type")) {
			this.colorType = ColorType.valueOf(value.toString());
		} else if( key.contentEquals("rotationSpeed")) {
			this.rotationSpeed = Float.valueOf(value.toString());
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
		setScale(this.scale);
		float halfWidth = Game.convertWorldToBox((getPrefWidth() * getScaleX()) / 2f);
		float radius = halfWidth * 0.7f;
		
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		BodyDef bd = new BodyDef();  
		bd.position.set(getX(), getY());
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
