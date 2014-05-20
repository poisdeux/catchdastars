package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.catchdastars.actors.GameObject;

public class Level implements Comparable<Level> {
	private int number;
	private String name;
	private ArrayList<GameObject> gameObjects;
	
	static private Level level;
	
	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		this.gameObjects = new ArrayList<GameObject>();
		for( GameObject object : gameObjects ) {
			if( object.getSaveToFile() ){
				this.gameObjects.add(object);
			}
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

	public static Level getLevel() {
		return level;
	}

	public String getJson() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
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
}
