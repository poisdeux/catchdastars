package com.strategames.engine.utils;

import java.io.File;
import java.io.FilenameFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

public class GameLoader {
	static private OnGameLoadedListener gameLoadedListener;

	public interface OnGameLoadedListener {
		public void onGameLoaded(Game game);
	}

	/**
	 * Loads packaged game files (synchronous)
	 * @param game 
	 * @return Game
	 */
	static private Game loadInternalSync(Game game) {
		try {
			FileHandle file = Gdx.files.internal(Files.getGamePath(game));
			return loadSync(file);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Loads local game files (synchronous) saved using {@link FileWriter#save(Stage, int)}
	 * @param game 
	 * @return Game
	 */
	static public Game loadLocalSync(Game game) {
		try {
			FileHandle file = Gdx.files.local(Files.getGamePath(game));
			return loadSync(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads local game files (asynchronous)
	 * @param game
	 * @param listener will be called when game has finished loading
	 */
	static private void loadLocalAsync(Game game, OnGameLoadedListener listener) {
		gameLoadedListener = listener;
		try {
			FileHandle file = Gdx.files.local(Files.getGamePath(game));
			loadAsync(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads game file (synchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalSync(int)} or {@link #loadLocalSync(int)} instead.
	 * @param file
	 * @return Game
	 */
	static private Game loadSync(FileHandle file) {
		Json json = new Json();
		try {
			String text = file.readString();
			Object root =  json.fromJson(Game.class, text);
			return (Game) root;
		} catch (GdxRuntimeException e) {
			Gdx.app.log("GameLoader", "Runtime error while loading game: "+e.getMessage());
		} catch (SerializationException e) {
			Gdx.app.log("GameLoader", "Serialization error while loading game: "+e.getMessage());
		}
		return null;
	}

	/**
	 * Loads game file (asynchronous) from FileHandle.
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
		Array<Game> games = new Array<Game>();
		FileHandle dir = getLocalGamesDir();
		FileHandle[] entries = dir.list();
		for( FileHandle entry : entries ) {
			if( entry.isDirectory() ) {
				FileHandle[] files = entry.list(new FilenameFilter() {

					@Override
					public boolean accept(File arg0, String arg1) {
						return arg1.contentEquals("meta");
					}
				});

				if( files.length > 0 ) {
					Game game = loadSync(files[0]);
					if( game != null ) {
						games.add(game);
					}
				}
			}
		}

		return games;
	}

	static private FileHandle getLocalGamesDir() {
		try {
			FileHandle dir = Gdx.files.local(Files.getGamesPath());
			return dir;
		} catch (Exception e) {
			Gdx.app.log("Game", "Error getting local games directory: "+e.getMessage());
		}
		return null;
	}

	static private FileHandle getInternalGamesDir() {
		try {
			FileHandle dir = Gdx.files.internal(Files.getGamesPath());
			return dir;
		} catch (Exception e) {
			Gdx.app.log("Games", "Error getting internal games directory: "+e.getMessage());
		}
		return null;
	}
}
