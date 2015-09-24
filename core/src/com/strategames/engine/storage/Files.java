/**
 * 
 * Copyright 2014 Martijn Brekhof
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
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.ScreenDensity;

/**
 * File structure:
 *
 * games/
 *   in_progress/
 *     <GAME_UUID>-<NUMBER>/
 *       levels_completed/
 *         <POSITION>
 *         ...
 *       meta
 *     ...
 *   originals
 *     <GAME_UUID>/
 *       levels/
 *         <POSITION>
 *         ...
 *       meta
 *       screenshots
 *     ...
 */

public class Files {
    static private final String TOP_PATH = "games";
    static private final String INPROGRESS_PATH = TOP_PATH+"/in_progress";
    static private final String ORIGINALS_PATH = TOP_PATH+"/originals";
    static private final String META_FILENAME = "meta";
    static private final String LEVELS_DIRECTORY = "levels";
    static private final String LEVELS_COMPLETED_DIRECTORY = "levels_completed";

    static public String getOriginalGamesDirectory() {
        return ORIGINALS_PATH;
    }

    static public String getPath(String name) {
        return TOP_PATH + "/" + name;
    }

    static public String getOriginalGameDirectory(GameMetaData gameMetaData) {
        if( gameMetaData == null ) {
            return null;
        }
        return ORIGINALS_PATH + "/" + gameMetaData.getUuid() + "/";
    }

    static public String getInProgressGameDirectory(GameMetaData gameMetaData) {
        if( gameMetaData == null ) {
            return null;
        }

        return INPROGRESS_PATH + "/" + gameMetaData.getUuid() + "-" + gameMetaData.getSavedGameProgressNumber() + "/";
    }

    static public String getOriginalGameMetaFile(GameMetaData gameMetaData) {
        return getOriginalGameDirectory(gameMetaData) + META_FILENAME;
    }

    static public String getInprogressGameMetaFile(GameMetaData gameMetaData) {
        return getInProgressGameDirectory(gameMetaData) + META_FILENAME;
    }

    static public String getOriginalLevelsPath(GameMetaData gameMetaData) {
        if( gameMetaData == null ) {
            Gdx.app.log("Files", "getOriginalLevelsPath: Error level has no game meta data set");
            return null;
        }
        return getOriginalGameDirectory(gameMetaData) + LEVELS_DIRECTORY;
    }

    static public String getCompletedLevelsPath(GameMetaData gameMetaData) {
        if( gameMetaData == null ) {
            Gdx.app.log("Files", "getCompletedLevelsPath: Error level has no game meta data set");
            return null;
        }
        return getInProgressGameDirectory(gameMetaData) + LEVELS_COMPLETED_DIRECTORY;
    }

    /**
     * Returns the path of the original level
     * @param level
     * @return
     */
    static public String getOriginalLevelFilename(Level level) {
        GameMetaData gameMetaData = level.getGameMetaData();

        if( level == null ) {
            Gdx.app.log("Files", "getOriginalLevelFilename: Error level is null");
            return null;
        }

        if( gameMetaData == null ) {
            Gdx.app.log("Files", "getOriginalLevelFilename: Error level has no game meta data set");
            return null;
        }

        return getOriginalLevelsPath(gameMetaData) + "/" + getLevelFilename(level.getPosition());
    }

    /**
     * Returns the path of the original level's screenshot image
     * @param level
     * @return
     */
    static public String getScreenshotFilename(Level level) {
        if( level == null ) {
            return null;
        }
        
        GameMetaData gameMetaData = level.getGameMetaData();

        if( level == null ) {
            Gdx.app.log("Files", "getScreenshotFilename: Error level is null");
            return null;
        }

        if( gameMetaData == null ) {
            Gdx.app.log("Files", "getScreenshotFilename: Error level has no game meta data set");
            return null;
        }

        return getOriginalGameDirectory(gameMetaData) +
                "/screenshots/" +
                ScreenDensity.getDensityName() +
                "/" +
                getLevelFilename(level.getPosition()) +
                ".png";
    }

    /**
     * Returns the path of the writer in the game's level directory
     * @param gameMetaData
     * @param pos
     * @return path
     */
    static public String getOriginalLevelFilename(GameMetaData gameMetaData, int[] pos) {
        if( gameMetaData == null ) {
            Gdx.app.log("Files", "getOriginalLevelFilename: Error level has no game meta data set");
            return null;
        }

        return getOriginalLevelsPath(gameMetaData) + "/" + getLevelFilename(pos);
    }

    static public String getLevelFilename(int[] pos) {
        return pos[0]+","+pos[1];
    }

    /**
     * Returns the path of the original level
     * @param level
     * @return path
     */
    static public String getCompletedLevelFilename(Level level) {
        GameMetaData gameMetaData = level.getGameMetaData();

        if( level == null ) {
            Gdx.app.log("Files", "getCompletedLevelFilename: Error level is null");
            return null;
        }

        if( gameMetaData == null ) {
            Gdx.app.log("Files", "getCompletedLevelFilename: Error level has no game meta data set");
            return null;
        }

        return getCompletedLevelsPath(gameMetaData) + "/" + level.getFilename();
    }

    /**
     * Returns the path of the writer in the game's level directory
     * @param gameMetaData
     * @param pos
     * @return path
     */
    static public String getCompletedLevelFilename(GameMetaData gameMetaData, int[] pos) {
        if( gameMetaData == null ) {
            Gdx.app.log("Level", "getCompletedLevelFilename: Error gameMetaData is null");
            return null;
        }

        return getCompletedLevelsPath(gameMetaData) + "/" + getLevelFilename(pos);
    }

    static public FileHandle getOriginalLevelsDir(GameMetaData gameMetaData) {
        try {
            FileHandle dir = Gdx.files.local(Files.getOriginalLevelsPath(gameMetaData));
            return dir;
        } catch (Exception e) {
            Gdx.app.log("Level", "getOriginalLevelsDir: Error: "+e.getMessage());
        }
        return null;
    }

    static public boolean originalLevelExists(Level level) {
        try {
            FileHandle handle = Gdx.files.local(getOriginalLevelFilename(level));
            return handle.exists();
        } catch (Exception e) {
            Gdx.app.log("Files", "originalLevelExists: Error: "+ e.getMessage());
        }
        return false;
    }
}
