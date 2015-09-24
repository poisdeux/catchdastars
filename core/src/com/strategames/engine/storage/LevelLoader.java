/**
 * 
 * Copyright 2013 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.strategames.engine.utils.Level;

public class LevelLoader {

	static private OnLevelLoadedListener levelLoadedListener;

	public interface OnLevelLoadedListener {
		public void onLevelLoaded(Level level);
	}

	/**
	 * Loads completed level file if it exists, otherwise tries to loadSync original
     * level file
	 */
	static public Level loadSync(GameMetaData gameMetaData, int[] pos) {
		try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(gameMetaData, pos));
            if( ! file.exists() ) {
                file = Gdx.files.local(Files.getOriginalLevelFilename(gameMetaData, pos));
            }
			return loadSync(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    /**
     * Loads completed level file
     * @param gameMetaData
     * @param pos
     * @return loaded level or null if it does not exist
     */
    static public Level loadCompleted(GameMetaData gameMetaData, int[] pos) {
        try {
            String filename = Files.getCompletedLevelFilename(gameMetaData, pos);
            FileHandle file = Gdx.files.local(filename);
            if( ! file.exists() ) {
                file = Gdx.files.local(Files.getOriginalLevelFilename(gameMetaData, pos));
            }
            return loadSync(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads original level file
     * @param gameMetaData
     * @param pos
     * @return loaded level or null if it does not exist
     */
    static public Level loadOriginal(GameMetaData gameMetaData, int[] pos) {
        try {
            FileHandle file = Gdx.files.local(Files.getOriginalLevelFilename(gameMetaData, pos));
            return loadSync(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	/**
	 * Loads level file (synchronous) from FileHandle.
	 * @param file
	 * @return Level object containing the game objects 
	 */
	static private Level loadSync(FileHandle file) {
		Json json = new Json();
//		Gdx.app.log("LevelLoader", "loadSync: filename="+file.name());
		try {
			String text = file.readString();
			Object root =  json.fromJson(Level.class, text);
			return (Level) root;
		} catch (GdxRuntimeException e) {
			Gdx.app.log("LevelLoader", "Runtime error while loading level: "+e.getMessage());
		} catch (SerializationException e) {
			Gdx.app.log("LevelLoader", "Serialization error while loading level: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Loads level file (asynchronous) from FileHandle.
     * @param file
	 */
	static private void loadAsync(final FileHandle file) {
		Thread thread = new Thread( new Runnable() {

			@Override
			public void run() {
				Json json = new Json();
				String text = file.readString();
				Object root =  json.fromJson(Level.class, text);
				if( levelLoadedListener != null ) {
					levelLoadedListener.onLevelLoaded((Level) root);
				}
			}
		});

		thread.start();
	}

	static public Array<Level> loadAllLocalLevels(GameMetaData gameMetaData) {
		FileHandle dir = Files.getOriginalLevelsDir(gameMetaData);
		FileHandle[] files = dir.list();

		Array<Level> levels = new Array<Level>();

		for( FileHandle file : files ) {
			Level level = loadSync(file);
			if( level != null ) {
				level.setGameMetaData(gameMetaData);
				levels.add(level);
			}
		}

		return levels;
	}

	public static Texture loadScreenShot(GameMetaData gameMetaData, Level level) {
		String filename = Files.getScreenshotFilename(level);
		if( filename == null ) {
			return null;
		}

		Texture texture = null;
		FileHandle file = Gdx.files.local(filename);
		if( file.exists() ) {
			try {
				texture = new Texture(file);
			} catch (Exception e) {
				Gdx.app.log("LevelLoader", "loadScreenShot: Error: " + e.getMessage());
			}
		}
		return texture;
	}
}
