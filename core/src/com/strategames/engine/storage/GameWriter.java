package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

import java.util.HashMap;
import java.util.Map;

public class GameWriter {

    static public boolean saveInprogress(Game game) {
        String metafile = Files.getInprogressGameMetaFile(game);
        if (metafile == null) {
            return false;
        }

        FileHandle file = Gdx.files.local(metafile);

        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(game.getJson()), false);
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }

        HashMap<String, Level> levels = game.getLevels();
        for( Map.Entry<String, Level> entry : levels.entrySet() ) {
            if( ! LevelWriter.saveCompleted(game, entry.getValue()) ) {
                return false;
            }
        }

        return true;
    }

    static public boolean saveOriginal(Game game) {
        String metafile = Files.getOriginalGameMetaFile(game);
        if (metafile == null) {
            return false;
        }

        FileHandle file = Gdx.files.local(metafile);

        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(game.getJson()), false);
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }

        HashMap<String, Level> levels = game.getLevels();
        for( Map.Entry<String, Level> entry : levels.entrySet() ) {
            if( ! LevelWriter.saveOriginal(game, entry.getValue()) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Saves the game's meta data
     *
     */
    static public boolean saveMetadataOriginal(Game game) {
        String gamePath = Files.getOriginalGameMetaFile(game);
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
    static public boolean deleteInprogress(Game game) {
        FileHandle file = Gdx.files.local(Files.getInProgressGameDirectory(game));
        if (file.isDirectory()) {
            if (file.deleteDirectory()) {
                return true;
            }
        }

        Gdx.app.log("Writer", "deleteInprogress: failed to delete " + file.name());
        return false;
    }

    /**
     * Deletes the complete game
     * @param game
     * @return
     */
    static public boolean deleteOriginal(Game game) {
        FileHandle file = Gdx.files.local(Files.getOriginalGameDirectory(game));
        if (file.isDirectory()) {
            if (file.deleteDirectory()) {
                return true;
            }
        }

        Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory " + Files.getOriginalGamesDirectory());
        return false;
    }

    static public boolean deleteAllOriginalGames() {
        FileHandle file = Gdx.files.local(Files.getOriginalGamesDirectory());
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
        Gdx.app.log("Writer", "deleteLocalDir: failed to delete directory " + file);
        return false;
    }
}
