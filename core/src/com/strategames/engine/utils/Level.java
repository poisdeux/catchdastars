package com.strategames.engine.utils;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.FileWriter.Writer;

public class Level implements Comparable<Level>, Writer {
	private String name = "noname";
	private Array<GameObject> gameObjects;
	private Array<Door> doors;
	private Vector2 worldSize = new Vector2(0, 0);
	private Vector2 viewSize  = new Vector2(0, 0);;
	private int[] position = new int[2];
	private boolean reachable;
	private Game game;
	
	public Level() {
		this.gameObjects = new Array<GameObject>();
		this.doors = new Array<Door>();
	}

	public void setGameObjects(Array<GameObject> gameObjects) {
		if( gameObjects == null )
			return;

		this.gameObjects = new Array<GameObject>();

		for( GameObject object : gameObjects ) {
			addGameObject(object);
			if( object instanceof Door ) {
				this.doors.add((Door) object);
			}
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
			if( object instanceof Door ) {
				Gdx.app.log("Level", "addGameObject: Door="+object);
				this.doors.add((Door) object);
			} else {
				this.gameObjects.add(object);
			}
		}
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void removeGameObject(GameObject object) {
		this.gameObjects.removeValue(object, true);
	}

	public Array<GameObject> getGameObjects() {
		return this.gameObjects;
	}

	public void addDoor(Door door) {
		if( door.getSaveToFile() ){
			this.doors.add((Door) door);
		}
	}

	public void removeDoor(Door door) {
		this.doors.removeValue(door, true);
	}

	public Array<Door> getDoors() {
		return doors;
	}

	public void setDoors(Array<Door> doors) {
		this.doors = doors;
	}

	public void setReachable(boolean reachable) {
		this.reachable = reachable;
	}

	public boolean isReachable() {
		return reachable;
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

	/**
	 * Position of the level in the game grid
	 * @return String representing the position as x,y where , is used as separator.
	 * E.g. 1,4 is column 1 row 4
	 */
	public String getPositionAsString() {
		return position[0]+","+position[1];
	}

	@Override
	public String getJson() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		return json.toJson(this);
	}

	@Override
	public String getFilename() {
		return position[0]+","+position[1];
	}
	
	@Override
	public int compareTo(Level o) {
		int[] oPosition = o.getPosition();
		if( position[0] > oPosition[0] ) {
			return 1;
		} else if( position[0] < oPosition[0] ) {
			return -1;
		} else {
			if( position[1] > oPosition[1] ) {
				return 1;
			} else if( position[1] < oPosition[1] ) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public String toString() {
		return String.format( Locale.US, "%d, %d,%d %s, #gameobjects=%d. #doors=%d", hashCode(), this.position[0], this.position[1], this.name, this.gameObjects.size, this.doors.size );
	}

	public Level copy() {
		Level level = new Level();
		if( this.gameObjects != null ) {
			Array<GameObject> copyGameObjects = new Array<GameObject>();
			for( GameObject gameObject : this.gameObjects ) {
				GameObject copy = gameObject.copy();
				copy.setupImage();
				copy.setupBody();
				copyGameObjects.add(copy);
			}
			level.setGameObjects(copyGameObjects);
		}
		level.setPosition(this.position[0], this.position[1]);
		level.setReachable(isReachable());

		Array<Door> doors = level.getDoors();
		for(Door door : doors) {
			level.addDoor(door);
		}

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

	@Override
	public boolean equals(Object obj) {
		
		if( obj == null ) {
			return false;
		}
		
		if( this == obj ) {
			return true;
		}
		
		if( ! ( obj instanceof Level ) ) {
			return false;
		} 
		
		Level level = (Level) obj;

		int[] levelPosition = level.getPosition();

		if( ( position[0] != levelPosition[0] ) || ( position[1] != levelPosition[1] ) ) {
			return false;
		}

		if( ! (
				( worldSize.equals(level.getWorldSize()) ) &&
				( viewSize.equals(level.getViewSize()) ) &&
				( reachable == level.isReachable() )
				) ) {
			return false;
		}

		return arrayGameObjectsEquals(level.getGameObjects(), this.gameObjects) &&
				arrayDoorsEquals(level.getDoors(), this.doors);	

	}

	private boolean arrayGameObjectsEquals(Array<GameObject> array1, Array<GameObject> array2) {
		for(GameObject object : array2) {
			if( ! array1.contains(object, false) ) {
				return false;
			}
		}
		return true;
	}

	private boolean arrayDoorsEquals(Array<Door> array1, Array<Door> array2) {
		for(Door object : array2) {
			if( ! array1.contains(object, false) ) {
				return false;
			}
		}
		return true;
	}
}
