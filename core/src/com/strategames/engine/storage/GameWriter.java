package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

import java.util.HashMap;

public class GameWriter {

    static public boolean saveInprogress(Game game) {
        String metafile = Files.getInprogressGameMetaPath(game);
        if (metafile == null) {
            return false;
        }

        HashMap<String, Level> levels = game.getLevels();
        for( HashMap.Entry<String, Level> entry : levels.entrySet() ) {
            if( ! LevelWriter.saveCompleted(game, entry.getValue()) ) {
                return false;
            }
        }

        try {
            FileHandle file = Gdx.files.local(metafile);

            Json json = new Json();
            file.writeString(json.prettyPrint(game.getJson()), false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }
    }

    static public boolean saveOriginal(Game game) {
        String metafile = Files.getInprogressGameMetaPath(game);
        if (metafile == null) {
            return false;
        }

        FileHandle file = Gdx.files.local(metafile);

        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(game.getJson()), false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves the game's meta data
     *
     */
    static public boolean saveMetadataOriginal(Game game) {
        String gamePath = Files.getOriginalGameMetaPath(game);
        if (gamePath == null) {
            return false;
        }
        FileHandle file = Gdx.files.local(gamePath);
        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(game.getJson()), false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the complete game
     * @param game
     * @return
     */
    static public boolean deleteMetadataOriginal(Game game) {
        FileHandle file = Gdx.files.local(Files.getOriginalGameMetaPath(game));
        if (file.isDirectory()) {
            if (file.deleteDirectory()) {
                return true;
            }
        } else if (file.exists()) {
            if (file.delete()) {
                return true;
            }
        } else {
            return true; // directory does not exist
        }
        Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory " + Files.getGamesDirectory());
        return false;
    }

    static public boolean deleteAllOriginalGames() {
        FileHandle file = Gdx.files.local(Files.getGamesDirectory());
        if (file.isDirectory()) {
            if (file.deleteDirectory()) {
                return true;
            }
        } else if (file.exists()) {
            if (file.delete()) {
                return true;
            }
        } else {
            return true; // directory does not exist
        }
        Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory " + Files.getGamesDirectory());
        return false;
    }
}
