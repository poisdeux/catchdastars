package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class LevelWriter {

	/**
	 * Saves level as original level which will be loaded when level is played the first time
     * during a game
	 * @param game
	 * @param writer
	 * @return true if saving was succesful, false otherwise
	 */
	static public boolean saveOriginal(Game game, Level writer) {
		FileHandle file = Gdx.files.local(Files.getOriginalLevelFilename(game, writer));
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
	 * Deletes the original level file which will be loaded when level is played the first time
     * during a game
	 * @param game
	 * @param level
	 * @return
	 */
	static public boolean deleteOriginal(Game game, Level level) {
		try {
			FileHandle file = Gdx.files.local(Files.getOriginalLevelFilename(game, level));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}

    /**
    * Saves level as completed level which will be loaded when level is played again
    * @param game
    * @param level
    * @return true if saving was succesful, false otherwise
    */
    static public boolean saveCompleted(Game game, Level level) {
        FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(game, level));
        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(level.getJson()), false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: "+file.path()+"\nError: "+e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the completed level file which will be loaded when level is played again
     * @param game
     * @param level
     * @return true if success, false otherwise
     */
    static public boolean deleteCompleted(Game game, Level level) {
        try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(game, level));
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes all completed levels from game
     * @param game
     * @return true if success, false otherwise
     */
    static public boolean deleteAllCompletedLevels(Game game) {
        try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelsPath(game));
            return file.deleteDirectory();
        } catch (Exception e) {
            return false;
        }
    }
}
