package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Level;

public class GameWriter {

    /**
     * Saves progress in game meta data file and saves progress in level file
     * @param gameMetaData
     * @param level
     * @return true if succeeded, false otherwise
     */
    static public boolean saveProgress(GameMetaData gameMetaData, Level level) {
        if( ! saveProgress(gameMetaData) ) {
            return false;
        }

        if( ! LevelWriter.saveCompleted(level) ) {
            return false;
        }

        return true;
    }

    /**
     * Saves progress only in meta data file
     * @param gameMetaData
     * @return true if succeeded, false otherwise
     */
    static public boolean saveProgress(GameMetaData gameMetaData) {
        String metafile = Files.getInprogressGameMetaFile(gameMetaData);
        if (metafile == null) {
            return false;
        }

        FileHandle file = Gdx.files.local(metafile);

        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(gameMetaData.getJson()), false);
        } catch (Exception e) {
            Gdx.app.log("GameWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Saves original game meta data file and original level file
     * @param gameMetaData
     * @param level
     * @return true if succeeded, false otherwise
     */
    static public boolean saveOriginal(GameMetaData gameMetaData, Level level) {
        if( ! saveOriginal(gameMetaData) ) {
            return false;
        }

        if( ! LevelWriter.saveOriginal(level) ) {
            return false;
        }

        return true;
    }

    /**
     * Saves only the original game meta data file
     * @param gameMetaData
     * @return true if succeeded, false otherwise
     */
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
            Gdx.app.log("GameWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
            return false;
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
            Gdx.app.log("GameWriter", "save: could not write: " + file.path() + "\nError: " + e.getMessage());
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
            try {
                return file.deleteDirectory();
            } catch (Exception e) {
                Gdx.app.log("GameWriter", "deleteInprogress: failed to delete " + file.name());
            }
        }
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
            try {
                return file.deleteDirectory();
            } catch (Exception e) {
                Gdx.app.log("GameWriter", "deleteOriginal: failed to delete " + file.name());
            }
        }

        return false;
    }

    static public boolean deleteAllOriginalGames() {
        FileHandle file = Gdx.files.local(Files.getOriginalGamesDirectory());
        if (file.isDirectory()) {
            try {
                return file.deleteDirectory();
            } catch (Exception e) {
                Gdx.app.log("GameWriter", "deleteAllOriginalGames: failed to delete " + file.name());
            }
        }
        return false;
    }
}
