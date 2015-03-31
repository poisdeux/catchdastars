package com.strategames.engine.storage;

import java.io.File;
import java.io.FilenameFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.strategames.engine.utils.Game;

public class GameLoader {
    static private OnGameLoadedListener gameLoadedListener;

    public interface OnGameLoadedListener {
        public void onGameLoaded(Game game);
    }

    /**
     * Loads game file (synchronous) from FileHandle.
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


    static public Array<Game> loadAllOriginalGames() {
        Array<Game> games = new Array<Game>();
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
                    Game game = loadSync(files[0]);
                    if( game != null ) {
                        games.add(game);
                    }
                }
            }
        }

        return games;
    }

    static public Game loadInProgressGame(Game game) {
        Game gameInprogress = null;
        FileHandle file = Gdx.files.local(Files.getInprogressGameMetaFile(game));
        if( file.exists() ) {
            gameInprogress = loadSync(file);
        }

        return gameInprogress;
    }

    /**
     * Creates a Game using jsonString as json input
     * @param jsonString the json input containing the game
     * @return Game
     */
    static public Game getGame(String jsonString) {
        Json json = new Json();
        try {
            return json.fromJson(Game.class, jsonString);
        } catch (Exception e) {
            Gdx.app.log("GameLoader", "getGame: error parsing json: "+e.getMessage());
            return null;
        }
    }
}
