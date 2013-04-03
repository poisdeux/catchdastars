package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.catchdastars.actors.GameObject;

public class GameFile {

	static private String PATH = "levels";

	public void load(int level) {

	}

	public void save(Stage stage, int level) {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);

		FileHandle file = Gdx.files.local(PATH + "/" + level);
		file.delete();

		Array<Actor> actors = stage.getActors();

		for(Actor actor: actors) {
			GameObject gameObject = (GameObject) actor;
			String text = json.toJson(gameObject);
			file.writeString(text, true);
		}
	}

	private class GameObjectHolder {
		private String name;
		private float x;
		private float y;
		private float rotation;
		private float length;
		private float rotationSpeed;

		public GameObjectHolder() {
		}

		public void fill(GameObject gameObject) {
			setName(gameObject.getName());
			setX(gameObject.getX());
			setY(gameObject.getY());
			setRotation(gameObject.getRotation());
			setLength(gameObject.getLength());
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getX() {
			return this.x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getY() {
			return this.y;
		}

		public void setRotation(float rotation) {
			this.rotation = rotation;
		}

		public float getRotation() {
			return this.rotation;
		}

		public void setLength(float length) {
			this.length = length;
		}

		public float getLength() {
			return this.length;
		}
	}
}
