package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

public class GameLoader {

	static private final String LOCAL_PATH = "games";
	static private final String INTERNAL_PATH = "games";
	static private OnGameLoadedListener gameLoadedListener;

	public interface OnGameLoadedListener {
		public void onGameLoaded(Game game);
	}

	/**
	 * Loads packaged level files (synchronous)
	 * @param name name of the level file
	 * @return Level object containing the game objects
	 */
	static private Game loadInternalSync(String UUID) {
		try {
			FileHandle file = Gdx.files.internal(getInternalPath(UUID));
			return loadSync(file);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Loads local level files (synchronous) saved using {@link FileWriter#save(Stage, int)}
	 * @param level levelnumber to load
	 * @return Level object containing the game objects 
	 */
	static public Game loadLocalSync(String UUID) {
		try {
			FileHandle file = Gdx.files.local(getLocalPath(UUID));
			return loadSync(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads local level files (asynchronous) saved using {@link #save(Stage, int)}
	 * <br/>
	 * Use {@link #levelLoaded()} to see if loading has finished. After loading you
	 * can retrieve the Level object using {@link #getGame()} 
	 * @param level levelnumber to load
	 */
	static private void loadLocalAsync(String UUID, OnGameLoadedListener listener) {
		gameLoadedListener = listener;
		try {
			FileHandle file = Gdx.files.local(getLocalPath(UUID));
			loadAsync(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads level file (synchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalSync(int)} or {@link #loadLocalSync(int)} instead.
	 * @param file
	 * @return Level object containing the game objects 
	 */
	static private Game loadSync(FileHandle file) {
		Json json = new Json();
		try {
			String text = file.readString();
			Object root =  json.fromJson(Level.class, text);
			return (Game) root;
		} catch (GdxRuntimeException e) {
			Gdx.app.log("GameLoader", "Runtime error while loading game: "+e.getMessage());
		} catch (SerializationException e) {
			Gdx.app.log("GameLoader", "Serialization error while loading game: "+e.getMessage());
		}
		return null;
	}

	/**
	 * Loads level file (asynchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalAsync(int)} or {@link #loadLocalAsync(int)} instead.
	 * @param file
	 */
	static private void loadAsync(final FileHandle file) {
		Thread thread = new Thread( new Runnable() {

			@Override
			public void run() {
				Json json = new Json();
				String text = file.readString();
				Object root =  json.fromJson(Game.class, text);
				if( gameLoadedListener != null ) {
					gameLoadedListener.onGameLoaded((Game) root);
				}
			}
		});

		thread.start();
	}

	static public Array<Game> loadAllLocalGames() {
		FileHandle dir = getLocalLevelsDir();
		FileHandle[] files = dir.list();

		Array<Game> games = new Array<Game>();

		for( FileHandle file : files ) {
			Game game = loadSync(file);
			if( game != null ) {
				games.add(game);
			}
		}

		return games;
	}

	static public FileHandle getLocalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.local(LOCAL_PATH);
			return dir;
		} catch (Exception e) {
			Gdx.app.log("Level", "Error getting local games directory: "+e.getMessage());
		}
		return null;
	}

	static public FileHandle getInternalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.internal(INTERNAL_PATH);
			return dir;
		} catch (Exception e) {
			Gdx.app.log("Level", "Error getting internal games directory: "+e.getMessage());
		}
		return null;
	}

	static public String getLocalPath() {
		return LOCAL_PATH;
	}

	static public String getLocalPath(String UUID) {
		return LOCAL_PATH + "/" + UUID + "/meta";
	}

	static public String getInternalPath(String UUID) {
		return INTERNAL_PATH + "/" + UUID + "/meta";
	}
}
