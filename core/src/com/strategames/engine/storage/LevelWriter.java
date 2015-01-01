package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class LevelWriter {

	/**
	 * Saves the writer in json format on local storage
	 * @param game
	 * @param writer
	 * @return true if saving was succesful, false otherwise
	 */
	static public boolean saveLocal(Game game, Writer writer) {
		FileHandle file = Gdx.files.local(Files.getLevelPath(game, writer));
		try {
			Json json = new Json();
			file.writeString(json.prettyPrint(writer.getJson()), false);
			return true;
		} catch (Exception e) {
			Gdx.app.log("LevelWriter", "save: could not write: "+file.path()+"\nError: "+e.getMessage());
			return false;
		}
	}

	/**
	 * Saves all levels in ArrayList levels on local storage
	 * @param game
	 * @param levels
	 * @return array of levels failed to write
	 */
	static public Array<Writer> saveLocal(Game game, Array<Writer> levels) {
		Array<Writer> levelsFailed = new Array<Writer>();
		for(Writer writer : levels) {
			if( ! LevelWriter.saveLocal(game, writer) ) {
				Gdx.app.log("LevelWriter", "save: Failed to save: "+writer);
				levelsFailed.add(writer);
			}
		}
		return levelsFailed;
	}

	/**
	 * Deletes the level file from local storage
	 * @param game
	 * @param level
	 * @return
	 */
	static public boolean deleteLevelLocal(Game game, Writer level) {
		try {
			FileHandle file = Gdx.files.local(Files.getLevelPath(game, level));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}
}
