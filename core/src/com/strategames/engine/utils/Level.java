package com.strategames.engine.utils;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.Writer;

public class Level implements Json.Serializable, Comparable<Level>, Writer {
	private Array<GameObject> gameObjects = new Array<>();
	private Array<Door> doors = new Array<>();
	private Array<com.strategames.engine.math.Vector2> accessibleBy = new Array<>();
	private Vector2 worldSize = new Vector2(0, 0);
	private Vector2 viewSize  = new Vector2(0, 0);;
	private int[] position = new int[2];
	private boolean reachable;
	private GameMetaData gameMetaData;

	public Level() {
	}

	@Override
	public void write(Json json) {
        json.writeValue("gameObjects", gameObjects);
		json.writeValue("reachable", reachable);
		json.writeValue("doors", doors);
		json.writeValue("viewSize", viewSize);
        json.writeValue("position", position);
        json.writeValue("worldSize", worldSize);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		json.readFields(this, jsonData);
	}


	public void setGameObjects(Array<GameObject> gameObjects) {
		if( gameObjects == null )
			return;

		this.gameObjects = new Array<>();

		for( GameObject object : gameObjects ) {
			addGameObject(object);
		}
	}

	/**
	 * Adds a gameobject to the level.
	 * Note that gameobjects will only be added if {@link GameObject#setSaveToFile(boolean)}
	 * is set to true
	 * @param object gameobject that should be added to the level
	 */
	public void addGameObject(GameObject object) {
		if( object.getSaveToFile() ){
			if( object instanceof Door ) {
				this.doors.add((Door) object);
			} else {
				this.gameObjects.add(object);
			}
		}
	}

	public void setGameMetaData(GameMetaData gameMetaData) {
		this.gameMetaData = gameMetaData;
	}
	
	public GameMetaData getGameMetaData() {
		return gameMetaData;
	}
	
	public void removeGameObject(GameObject object) {
		if( object instanceof Door ) {
			this.doors.removeValue((Door) object, true);
		} else {
			this.gameObjects.removeValue(object, true);
		}
	}

	public Array<GameObject> getGameObjects() {
		return this.gameObjects;
	}

	/**
	 * Adds level position from which this level is accessible.
	 * <br/>
	 * If position has already been added it will not be added again.
	 * @param x horizontal position
	 * @param y vertical position
	 */
	public void addAccessibleBy(int x, int y) {
		com.strategames.engine.math.Vector2 v = new com.strategames.engine.math.Vector2(x, y);
		if( ! this.accessibleBy.contains(v, false) ) {
			this.accessibleBy.add(v);
		}
	}

	/**
	 * Removes level position from which this level is accessible.
	 * @param x horizontal position
	 * @param y vertical position
	 */
	public void delAccessibleBy(int x, int y) {
		this.accessibleBy.removeValue(new com.strategames.engine.math.Vector2(x, y), false);
	}

	public Array<com.strategames.engine.math.Vector2> getAccessibleBy() {
		return accessibleBy;
	}

	/**
	 * Returns door gameobjects that have been added using {@link #addGameObject(GameObject)} or
	 * set using {@link #setDoors(Array)}
	 * @return array of Door(s)
	 */
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
		if (o == null) return 1;
		int[] oPosition = o.getPosition();
		if (position[0] > oPosition[0]) {
			return 1;
		} else if (position[0] < oPosition[0]) {
			return -1;
		} else {
			if (position[1] > oPosition[1]) {
				return 1;
			} else if (position[1] < oPosition[1]) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format(Locale.US, "%d, pos=(%d, %d), #gameobjects=%d, #doors=%d, isReachable=%b\n", hashCode(), this.position[0], this.position[1], this.gameObjects.size, this.doors.size, isReachable()));
		stringBuilder.append("accessibleBy:");
        for( Vector2 v : getAccessibleBy() ) {
            stringBuilder.append(" ");
	        stringBuilder.append(v);
        }
		stringBuilder.append("\nGameobjects:\n");
		for( GameObject gameObject : this.gameObjects ) {
			stringBuilder.append(gameObject.toString());
			stringBuilder.append("\n");
		}
		for( GameObject gameObject : this.doors ) {
			stringBuilder.append(gameObject.toString());
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	public Level copy() {
		Level level = new Level();
		if( this.gameObjects != null ) {
			Array<GameObject> copyGameObjects = new Array<>();
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
			level.addGameObject(door);
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

		Array<com.strategames.engine.math.Vector2> accessiblePos = level.getAccessibleBy();
		if( ! accessiblePos.equals(accessibleBy) ) {
			return false;
		}

		if( ! (
				( worldSize.equals(level.getWorldSize()) ) &&
				( viewSize.equals(level.getViewSize()) ) &&
				( reachable == level.isReachable() )
				) ) {
			return false;
		}

		if ( ! arrayGameObjectsEquals(level.getGameObjects(), this.gameObjects) ) {
			return false;
		}

		Gdx.app.log("Level", "arrayGameObjectsEquals: equals");

		return arrayDoorsEquals(level.getDoors(), this.doors);

	}

	private boolean arrayGameObjectsEquals(Array<GameObject> array1, Array<GameObject> array2) {
		for(GameObject object : array2) {
			if( ! array1.contains(object, false) ) {
				Gdx.app.log("Level", "arrayGameObjectsEquals: not found object="+object);
				return false;
			}
		}
		return true;
	}

	private boolean arrayDoorsEquals(Array<Door> array1, Array<Door> array2) {
		for(Door object : array2) {
			if( ! array1.contains(object, false) ) {
				Gdx.app.log("Level", "arrayDoorsEquals: not found object="+object);
				return false;
			}
		}
		return true;
	}
}
