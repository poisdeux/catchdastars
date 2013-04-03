package com.strategames.catchdastars.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.actors.Balloon.Type;

abstract public class GameObject extends Image implements Json.Serializable {
	private float length;
	private float rotationSpeed;
	private String name;
	
	public GameObject() {
	}
	
	public GameObject(TextureRegionDrawable trd, Scaling scaling) {
		super(trd, scaling);
	}
	
	public void setLength(float length) {
		this.length = length;
	}
	
	public float getLength() {
		return this.length;
	}
	
	public void setRotationSpeed(float speed) {
		this.rotationSpeed = speed;
	}
	
	public float getRotationSpeed() {
		return this.rotationSpeed;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
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
	
	@Override
	public void read(Json json, OrderedMap<String, Object> jsonData) {
		Entries<String, Object> entries = jsonData.entries();
		
		while(entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			if ( entry.key.contentEquals("x")) {
				setX(Float.valueOf(entry.value.toString()));
			} else if ( entry.key.contentEquals("y")) {
				setY(Float.valueOf(entry.value.toString()));
			} else {
				readValue(entry.key, entry.value);
			}
		}
	}
}
