package com.strategames.engine.utils;

import java.util.ArrayList;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobjects.GameObject;

public class Level implements Comparable<Level> {
	private int number;
	private String name;
	private ArrayList<GameObject> gameObjects;

	public Level() {
		this.gameObjects = new ArrayList<GameObject>();
	}
	
	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		if( gameObjects == null )
			return;
		
		this.gameObjects = new ArrayList<GameObject>();
		
		for( GameObject object : gameObjects ) {
			addGameObject(object);
		}
	}

	/**
	 * Adds a gameobject to the level.
	 * Note that gameobjects will only be added if {@link GameObject#setSaveToFile(boolean)}
	 * is set to true
	 * @param object
	 */
	public void addGameObject(GameObject object) {
		if( object.getSaveToFile() ){
			this.gameObjects.add(object);
		}
	}
	
	public ArrayList<GameObject> getGameObjects() {
		return this.gameObjects;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setLevelNumber(int number) {
		this.number = number;
	}

	public int getLevelNumber() {
		return this.number;
	}

	public String getJson() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		Gdx.app.log("Level", "getJson: json="+json.toJson(this));
		return json.toJson(this);
	}

	@Override
	public int compareTo(Level o) {
		if( this.number > o.getLevelNumber() ) {
			return 1;
		} else if( this.number == o.getLevelNumber() ) {
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return String.format( Locale.US, "%d %s", this.number, this.name );
	}

	public Level copy() {
		Level level = new Level();
		if( this.gameObjects != null ) {
			ArrayList<GameObject> copyGameObjects = new ArrayList<GameObject>();
			for( GameObject gameObject : this.gameObjects ) {
				copyGameObjects.add(gameObject.copy());
			}
			level.setGameObjects(copyGameObjects);
		}
		level.setName(new String(this.name));
		level.setLevelNumber(this.number);
		return level;
	}
}
