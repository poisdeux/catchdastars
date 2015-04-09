package com.strategames.engine.storage;

import java.io.File;
import java.io.FilenameFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

public class GameLoader {
    static private OnGameLoadedListener gameLoadedListener;

    public interface OnGameLoadedListener {
        public void onGameLoaded(GameMetaData gameMetaData);
    }

    /**
     * Loads game file (synchronous) from FileHandle.
     * @param file
     * @return Game
     */
    static private GameMetaData loadSync(FileHandle file) {
        Json json = new Json();
        try {
            String text = file.readString();
            Object root =  json.fromJson(GameMetaData.class, text);
            return (GameMetaData) root;
        } catch (GdxRuntimeException e) {
            Gdx.app.log("GameLoader", "Runtime error while loading game: "+e.getMessage());
        } catch (SerializationException e) {
            Gdx.app.log("GameLoader", "Serialization error while loading game: "+e.getMessage());
        }
        return null;
    }


    static public Array<GameMetaData> loadAllOriginalGames() {
        Array<GameMetaData> games = new Array<GameMetaData>();
        FileHandle dir = Gdx.files.local(Files.getOriginalGamesDirectory());
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
                    GameMetaData gameMetaData = loadSync(files[0]);
                    if( gameMetaData != null ) {
                        games.add(gameMetaData);
                    }
                }
            }
        }

        return games;
    }

    static public GameMetaData loadOriginal(GameMetaData gameMetaData) {
        GameMetaData gameMetaDataOriginal = null;
        FileHandle file = Gdx.files.local(Files.getOriginalGameMetaFile(gameMetaData));
        if( file.exists() ) {
            gameMetaDataOriginal = loadSync(file);
        }

        return gameMetaDataOriginal;
    }

    static public GameMetaData loadInProgress(GameMetaData gameMetaData) {
        GameMetaData gameMetaDataInprogress = null;
        FileHandle file = Gdx.files.local(Files.getInprogressGameMetaFile(gameMetaData));
        if( file.exists() ) {
            gameMetaDataInprogress = loadSync(file);
        }

        return gameMetaDataInprogress;
    }

    /**
     * Creates a Game using jsonString as json input
     * @param jsonString the json input containing the game
     * @return Game
     */
    static public GameMetaData getGame(String jsonString) {
        Json json = new Json();
        try {
            return json.fromJson(GameMetaData.class, jsonString);
        } catch (Exception e) {
            Gdx.app.log("GameLoader", "getGame: error parsing json: "+e.getMessage());
            return null;
        }
    }
}
