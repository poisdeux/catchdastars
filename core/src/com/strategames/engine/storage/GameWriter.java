/**
 * 
 * Copyright 2015 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Level;

public class GameWriter {

    /**
     * Saves progress in game meta data file and saves progress in level file
     * @param level
     * @return true if succeeded, false otherwise
     */
    static public boolean saveProgress(Level level) {
        if( ! saveProgress(level.getGameMetaData()) ) {
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
     * @param level
     * @return true if succeeded, false otherwise
     */
    static public boolean saveOriginal(Level level) {
        if( ! saveOriginal(level.getGameMetaData()) ) {
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
