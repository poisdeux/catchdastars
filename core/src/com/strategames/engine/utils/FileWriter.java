package com.strategames.engine.utils;

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
	 * @param directory where to save the writer
	 * @param writer to be written
	 * @return true if succesful, false otherwise
	 */
	static public boolean saveLocal(String directory, Writer writer) {
		FileHandle file = Gdx.files.local(directory +"/" + writer.getFilename());
		try {
			Json json = new Json();
			file.writeString(json.prettyPrint(writer.getJson()), false);
			return true;
		} catch (Exception e) {
			Gdx.app.log("LevelWriter", "save: could not write: "+writer+"\nError: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Saves all writers in ArrayList writers in the given directory on local storage
	 * @param directory where to save
	 * @param writers to save
	 * @return ArrayList of writers that failed to save
	 */
	static public Array<Writer> saveLocal(String directory, Array<Writer> writers) {
		Array<Writer> levelsFailed = new Array<Writer>();
		for(Writer writer : writers) {
			if( ! FileWriter.saveLocal(directory, writer) ) {
				Gdx.app.log("LevelWriter", "save: Failed to save: "+writer);
				levelsFailed.add(writer);
			}
		}
		return levelsFailed;
	}
	
	static public boolean deleteLocalDir() {
		FileHandle file = Gdx.files.local(Files.getPath());
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
		Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory "+Files.getPath());
		return false;
	}
	
	/**
	 * Deletes the local file for the given level
	 * @param level
	 */
	static public boolean deleteLocal(Writer writer) {
		try {
			FileHandle file = Gdx.files.local(Files.getPath(writer.getFilename()));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}
}
