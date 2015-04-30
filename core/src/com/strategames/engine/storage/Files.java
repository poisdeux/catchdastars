package com.strategames.engine.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.utils.Level;

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
			return null;
		}
		return getOriginalGameDirectory(gameMetaData) + LEVELS_DIRECTORY;
	}

    static public String getCompletedLevelsPath(GameMetaData gameMetaData) {
        if( gameMetaData == null ) {
            return null;
        }
        return getInProgressGameDirectory(gameMetaData) + LEVELS_COMPLETED_DIRECTORY;
    }

    /**
     * Returns the path of the original level
     * @param gameMetaData
     * @param level
     * @return
     */
	static public String getOriginalLevelFilename(GameMetaData gameMetaData, Level level) {
		if( ( level == null ) || ( gameMetaData == null ) ) {
			return null;
		}

       return getOriginalLevelsPath(gameMetaData) + "/" + level.getFilename();
	}

    /**
     * Returns the path of the writer in the game's level directory
     * @param gameMetaData
     * @param pos
     * @return path
     */
    static public String getOriginalLevelFilename(GameMetaData gameMetaData, int[] pos) {
        if( gameMetaData == null ) {
            return null;
        }

        return getOriginalLevelsPath(gameMetaData) + "/" + getLevelFilename(pos);
    }

    static public String getLevelFilename(int[] pos) {
        return pos[0]+","+pos[1];
    }

    /**
     * Returns the path of the original level
     * @param gameMetaData
     * @param level
     * @return path
     */
    static public String getCompletedLevelFilename(GameMetaData gameMetaData, Level level) {
        if( ( level == null ) || ( gameMetaData == null ) ) {
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
            return null;
        }

        return getCompletedLevelsPath(gameMetaData) + "/" + getLevelFilename(pos);
    }

    static public FileHandle getOriginalLevelsDir(GameMetaData gameMetaData) {
        try {
            FileHandle dir = Gdx.files.local(Files.getOriginalLevelsPath(gameMetaData));
            return dir;
        } catch (Exception e) {
            //			Gdx.app.log("Level", "error");
        }
        return null;
    }
}
