package com.strategames.catchdastars.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;

abstract public class GameObject extends Image implements Json.Serializable {
	private String name;
	private World world;
	private Body body;
	
	public GameObject() {
		setName(getClass().getSimpleName());
	}
	
	public GameObject(Drawable trd) {
		super(trd, Scaling.none);
		setName(getClass().getSimpleName());
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public void setBody(Body body) {
		this.body = body;
	}
	
	public Body getBody() {
		return body;
	}
	
	/**
	 * Setup the image and body of this game object. 
	 * @param world Box2D world that should hold the body. If null only image will be set and no body will be created.
	 */
	public void setup() {
		TextureRegionDrawable trd = createTexture();
		if( trd != null ) {
			setDrawable(trd);
			setScaling(Scaling.none);
			setWidth(trd.getRegion().getRegionWidth());
			setHeight(trd.getRegion().getRegionHeight());
		}
		
		if( this.world != null ) {
			this.body = setupBox2D();
		}
	}
	
	public void moveTo(float x, float y) {
		setPosition(x, y);
		if( this.body != null ) {
			this.body.setTransform(x,  y, this.body.getAngle());
		}
	}
	
	/**
	 * Called to create the image for the game object
	 */
	abstract TextureRegionDrawable createTexture();
	
	/**
	 * Called to create the Box2D body of the game object.
	 * @return the created body
	 */
	abstract Body setupBox2D();
	
	/**
	 * Use this to write specific object properties to file(s)
	 * Note that generic properties are saved by GameObject abstract class
	 * and you do not need to write them using {@link #writeValues(Json)}
	 * <br>
	 * Example
	 * <pre>
	 * json.writeValue("type", type.name());
	 * json.writeValue("lift", getLift());
	 * </pre>
	 * @param json
	 */
	abstract void writeValues(Json json);
	
	@Override
	public void write(Json json) {
		json.writeObjectStart(this.getClass().getSimpleName());
		json.writeValue("x", getX());
		json.writeValue("y", getY());
		writeValues(json);
		json.writeObjectEnd();
	}

	/**
	 * Use this to read specific object properties read from file(s) you
	 * saved using {@link #writeValues(Json)}
	 * <br>
	 * Example
	 * <pre>
	 * if( key.contentEquals("type")) {
	 *   type = Type.valueOf(value.toString());
	 * }
	 * @param key	String holding the name you used in json.writeValue(String name, Object value) 
	 * @param value	The object you set using json.writeValue(String name, Object value)
	 */
	abstract void readValue(String key, Object value);
	
	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, OrderedMap<String, Object> jsonData) {
		Entries<String, Object> entries = jsonData.entries();
		
		while(entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			if ( entry.value instanceof OrderedMap ) {
				read(json, (OrderedMap<String, Object>) entry.value);
			} else if ( entry.key.contentEquals("x")) {
				setX(Float.valueOf(entry.value.toString()));
			} else if ( entry.key.contentEquals("y")) {
				setY(Float.valueOf(entry.value.toString()));
			} else {
				readValue(entry.key, entry.value);
			}
		}
	}
	
	abstract public GameObject createCopy();
}
