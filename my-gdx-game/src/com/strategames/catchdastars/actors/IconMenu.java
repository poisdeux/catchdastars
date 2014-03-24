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

public class IconMenu extends GameObject {
	private final static float WIDTH = 0.30f;
	
	public IconMenu() {
		super(new Vector2(WIDTH, -1f));
		setMenuItem(true);
		setSaveToFile(false);
	}
	
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	@Override
	TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.menu);
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
	public GameObject createCopy() {
		GameObject object = new IconMenu();
		object.setPosition(getX(), getY());
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
	void writeValues(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void readValue(String key, Object value) {
		// TODO Auto-generated method stub
		
	}
}
