package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Level;

public class LevelWriter {

	/**
	 * Saves level as original level which will be loaded when level is played the first time
     * during a game
	 * @param gameMetaData
	 * @param writer
	 * @return true if saving was succesful, false otherwise
	 */
	static public boolean saveOriginal(GameMetaData gameMetaData, Level writer) {
        String filename = Files.getOriginalLevelFilename(gameMetaData, writer);
        if( filename == null ) {
            Gdx.app.log("LevelWriter", "saveOriginal: failed to get filename for \n"+
                    "gameMetaData: "+gameMetaData+
            "level: "+writer);
            return false;
        }

		FileHandle file = Gdx.files.local(filename);
		try {
			Json json = new Json();
			file.writeString(json.prettyPrint(writer.getJson()), false);
			return true;
		} catch (Exception e) {
			Gdx.app.log("LevelWriter", "saveOriginal: could not write: "+file.path()+"\nError: "+e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes the original level file which will be loaded when level is played the first time
     * during a game
	 * @param gameMetaData
	 * @param level
	 * @return
	 */
	static public boolean deleteOriginal(GameMetaData gameMetaData, Level level) {
		try {
			FileHandle file = Gdx.files.local(Files.getOriginalLevelFilename(gameMetaData, level));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}

    /**
    * Saves level as completed level which will be loaded when level is played again
    * @param gameMetaData
    * @param level
    * @return true if saving was succesful, false otherwise
    */
    static public boolean saveCompleted(GameMetaData gameMetaData, Level level) {
        FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(gameMetaData, level));
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
     * @param gameMetaData
     * @param level
     * @return true if success, false otherwise
     */
    static public boolean deleteCompleted(GameMetaData gameMetaData, Level level) {
        try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(gameMetaData, level));
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }
}
