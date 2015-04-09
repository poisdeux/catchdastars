package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Level;

import java.util.HashMap;
import java.util.Map;

public class GameWriter {

    static public boolean saveInProgress(GameMetaData gameMetaData) {
        String metafile = Files.getInprogressGameMetaFile(gameMetaData);
        if (metafile == null) {
            return false;
        }

        FileHandle file = Gdx.files.local(metafile);

        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(gameMetaData.getJson()), false);
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }

        HashMap<String, Level> levels = gameMetaData.getLevels();
        for( Map.Entry<String, Level> entry : levels.entrySet() ) {
            if( ! LevelWriter.saveCompleted(gameMetaData, entry.getValue()) ) {
                return false;
            }
        }

        return true;
    }

    static public boolean saveOriginal(GameMetaData gameMetaData) {
        String metafile = Files.getOriginalGameMetaFile(gameMetaData);
        if (metafile == null) {
            return false;
        }

        FileHandle file = Gdx.files.local(metafile);

        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(gameMetaData.getJson()), false);
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }

        HashMap<String, Level> levels = gameMetaData.getLevels();
        for( Map.Entry<String, Level> entry : levels.entrySet() ) {
            if( ! LevelWriter.saveOriginal(gameMetaData, entry.getValue()) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Saves the game's meta data
     *
     */
    static public boolean saveMetadataOriginal(GameMetaData gameMetaData) {
        String gamePath = Files.getOriginalGameMetaFile(gameMetaData);
        if (gamePath == null) {
            return false;
        }
        FileHandle file = Gdx.files.local(gamePath);
        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(gameMetaData.getJson()), false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the complete game
     * @param gameMetaData
     * @return
     */
    static public boolean deleteInprogress(GameMetaData gameMetaData) {
        FileHandle file = Gdx.files.local(Files.getInProgressGameDirectory(gameMetaData));
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
     * @param gameMetaData
     * @return
     */
    static public boolean deleteOriginal(GameMetaData gameMetaData) {
        FileHandle file = Gdx.files.local(Files.getOriginalGameDirectory(gameMetaData));
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
