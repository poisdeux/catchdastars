package com.strategames.engine.utils;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class FileWriter {

	public interface Writer {
		/**
		 * Must return the json data to be written
		 * @return String containing the json data
		 */
		public String getJson();
		/**
		 * Must return the filename where the json data should be written to.
		 * @return String filename
		 */
		public String getFilename();
	}

	/**
	 * Saves the writer in json format in the given directory on local storage
	 * @param game
	 * @param level
	 * @return true if saving was succesful, false otherwise
	 */
	static public boolean saveLevelLocal(Game game, Level level) {
		FileHandle file = Gdx.files.local(Files.getLevelPath(game, level));
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
	 * Saves all levels in ArrayList levels on local storage
	 * @param game
	 * @param levels
	 * @return array of levels failed to write
	 */
	static public Array<Writer> saveLevelsLocal(Game game, Array<Level> levels) {
		Array<Writer> levelsFailed = new Array<Writer>();
		for(Level level : levels) {
			if( ! FileWriter.saveLevelLocal(game, level) ) {
				Gdx.app.log("LevelWriter", "save: Failed to save: "+level);
				levelsFailed.add(level);
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
	static public boolean deleteLevelLocal(Game game, Level level) {
		try {
			FileHandle file = Gdx.files.local(Files.getLevelPath(game, level));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Saves the game in json format in the given directory on local storage
	 * @param directory where to save the writer
	 * @param writer to be written
	 * @return true if succesful, false otherwise
	 */
	static public boolean saveGameLocal(Game game) {
		String gamePath = Files.getGameMetaPath(game);
		if( gamePath == null ) {
			return false;
		}
		FileHandle file = Gdx.files.local( gamePath );
		try {
			Json json = new Json();
			file.writeString(json.prettyPrint(game.getJson()), false);
			return true;
		} catch (Exception e) {
			Gdx.app.log("LevelWriter", "save: could not write: "+file.path()+"\nError: "+e.getMessage());
			return false;
		}
	}

	static public boolean deleteLocalGame(Game game) {
		FileHandle file = Gdx.files.local(Files.getGameDirectory(game));
		if( file.isDirectory() ) {
			if( file.deleteDirectory())  {
				return true;
			}
		} else if( file.exists() ) {
			if( file.delete() ) {
				return true;
			}
		} else {
			return true; // directory does not exist
		}
		Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory "+Files.getGamesDirectory());
		return false;
	}

	static public boolean deleteLocalGamesDir() {
		FileHandle file = Gdx.files.local(Files.getGamesDirectory());
		if( file.isDirectory() ) {
			if( file.deleteDirectory())  {
				return true;
			}
		} else if( file.exists() ) {
			if( file.delete() ) {
				return true;
			}
		} else {
			return true; // directory does not exist
		}
		Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory "+Files.getGamesDirectory());
		return false;
	}

	/**
	 * Deletes the local file for the given level
	 * @param level
	 */
	//	static public boolean deleteLocal(Writer writer) {
	//		try {
	//			FileHandle file = Gdx.files.local(Files.getPath(writer.getFilename()));
	//			return file.delete();
	//		} catch (Exception e) {
	//			return false;
	//		}
	//	}
}
