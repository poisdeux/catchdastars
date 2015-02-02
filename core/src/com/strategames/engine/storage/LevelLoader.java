package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class LevelLoader {

	static private OnLevelLoadedListener levelLoadedListener;

	public interface OnLevelLoadedListener {
		public void onLevelLoaded(Level level);
	}

	/**
	 * Loads packaged level files (synchronous)
	 * @param name name of the level file
	 * @return Level object containing the game objects
	 */
	static private Level loadInternalSync(String name) {
		try {
			FileHandle file = Gdx.files.internal(Files.getPath(name));
			return loadSync(file);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Loads local level files (synchronous) saved using {@link com.strategames.engine.storage.LevelWriter#saveOriginal(com.strategames.engine.utils.Game, Writer)}
	 */
	static public Level loadLocalSync(Game game, int[] pos) {
		try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelPath(game, pos));
            if( ! file.exists() ) {
                file = Gdx.files.local(Files.getOriginalLevelPath(game, pos));
            }
			return loadSync(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads local level files (asynchronous) saved using {@link com.strategames.engine.storage.LevelWriter#saveOriginal(com.strategames.engine.utils.Game, Writer)}
	 * <br/>
	 */
	static private void loadLocalAsync(Game game, int[] pos, OnLevelLoadedListener listener) {
		levelLoadedListener = listener;
		try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelPath(game, pos));
            if( ! file.exists() ) {
                file = Gdx.files.local(Files.getOriginalLevelPath(game, pos));
            }
			loadAsync(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads level file (synchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalSync(String)} or {@link #loadLocalSync(com.strategames.engine.utils.Game, com.strategames.engine.utils.Level)} instead.
	 * @param file
	 * @return Level object containing the game objects 
	 */
	static private Level loadSync(FileHandle file) {
		Json json = new Json();
		try {
			String text = file.readString();
			Object root =  json.fromJson(Level.class, text);
			return (Level) root;
		} catch (GdxRuntimeException e) {
			Gdx.app.log("LevelLoader", "Runtime error while loading level: "+e.getMessage());
		} catch (SerializationException e) {
			Gdx.app.log("LevelLoader", "Serialization error while loading level: "+e.getMessage());
		}
		return null;
	}

	/**
	 * Loads level file (asynchronous) from FileHandle.
     * You should never need to use this. Use {@link #loadInternalSync(String)} or {@link #loadLocalSync(com.strategames.engine.utils.Game, com.strategames.engine.utils.Level)} instead.
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

	static public Array<Level> loadAllLocalLevels(Game game) {
		FileHandle dir = getLocalLevelsDir(game);
		FileHandle[] files = dir.list();

		Array<Level> levels = new Array<Level>();

		for( FileHandle file : files ) {
			Level level = loadSync(file);
			if( level != null ) {
				levels.add(level);
			}
		}

		return levels;
	}

	static public FileHandle getLocalLevelsDir(Game game) {
		try {
			FileHandle dir = Gdx.files.local(Files.getOriginalLevelsPath(game));
			return dir;
		} catch (Exception e) {
			//			Gdx.app.log("Level", "error");
		}
		return null;
	}

	static public FileHandle getInternalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.internal(Files.getGamesDirectory());
			return dir;
		} catch (Exception e) {
			//			Gdx.app.log("Level", "error");
		}
		return null;
	}

	

	/**
	 * Reads the amount of level files on disk to determine
	 * the last level number
	 * @return
	 */
	public static int getLastLevelNumber(Game game) {

		FileHandle dir = getLocalLevelsDir(game);
		FileHandle[] files = dir.list();
		return files.length;
	}


}
