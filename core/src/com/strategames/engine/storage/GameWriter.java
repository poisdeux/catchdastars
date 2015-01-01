package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class GameWriter {

    /**
     * Saves the game's meta data on local storage
     *
     */
    static public boolean saveMetadataLocal(Game game) {
        String gamePath = Files.getGameMetaPath(game);
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
     * Deletes the complete game from local storage
     * @param game
     * @return
     */
    static public boolean deleteLocal(Game game) {
        FileHandle file = Gdx.files.local(Files.getGameDirectory(game));
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

    static public boolean deleteAllGamesLocal() {
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
