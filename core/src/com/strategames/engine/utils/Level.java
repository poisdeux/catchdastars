package com.strategames.engine.utils;

import java.util.ArrayList;
import java.util.Locale;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobjects.GameObject;

public class Level implements Comparable<Level> {
	private int number;
	private String name;
	private Array<GameObject> gameObjects;
	private Vector2 worldSize;
	private Vector2 viewSize;
	private int[] position = new int[2];
	
	public Level() {
		this.gameObjects = new Array<GameObject>();
	}
	
	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		if( gameObjects == null )
			return;
		
		this.gameObjects = new Array<GameObject>();
		
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
	
	public void removeGameObject(GameObject object) {
		this.gameObjects.removeValue(object, true);
	}
	
	public Array<GameObject> getGameObjects() {
		return this.gameObjects;
	}

	public void setPosition(int x, int y) {
		this.position[0] = x;
		this.position[1] = y;
	}

	/**
	 * Position of the level in the game grid
	 * @return int array. int[0] = x, int[1] = y
	 */
	public int[] getPosition() {
		return position;
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
//		Gdx.app.log("Level", "getJson: json="+json.toJson(this));
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
		return String.format( Locale.US, "%d %s, #gameobjects=%d", this.number, this.name, this.gameObjects.size );
	}

	public Level copy() {
		Level level = new Level();
		if( this.gameObjects != null ) {
			ArrayList<GameObject> copyGameObjects = new ArrayList<GameObject>();
			for( GameObject gameObject : this.gameObjects ) {
				GameObject copy = gameObject.copy();
				copy.setupImage();
				copy.setupBody();
				copyGameObjects.add(copy);
			}
			level.setGameObjects(copyGameObjects);
		}
		level.setName(new String(this.name));
		level.setLevelNumber(this.number);
		return level;
	}
	
	public void setWorldSize(Vector2 worldSize) {
		this.worldSize = worldSize;
	}
	
	public Vector2 getWorldSize() {
		return worldSize;
	}
	
	public void setViewSize(Vector2 viewSize) {
		this.viewSize = viewSize;
	}
	
	public Vector2 getViewSize() {
		return viewSize;
	}
}
