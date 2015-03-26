package com.strategames.engine.storage;

import com.strategames.engine.utils.Game;
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

	static public String getGamesDirectory() {
		return TOP_PATH;
	}

	static public String getPath(String name) {
		return TOP_PATH + "/" + name;
	}
	
	static public String getOriginalGameDirectory(Game game) {
		if( game == null ) {
			return null;
		}
		return ORIGINALS_PATH + "/" + game.getUuid() + "/";
	}

    static public String getInProgressGameDirectory(Game game) {
        if( game == null ) {
            return null;
        }
        return INPROGRESS_PATH + "/" + game.getUuid() + "/";
    }

    static public String getOriginalGameMetaPath(Game game) {
        return getOriginalGameDirectory(game) + META_FILENAME;
    }

	static public String getInprogressGameMetaPath(Game game) {
		return getInProgressGameDirectory(game) + META_FILENAME;
	}
	
	static public String getOriginalLevelsPath(Game game) {
		if( game == null ) {
			return null;
		}
		return getOriginalGameDirectory(game) + LEVELS_DIRECTORY;
	}

    static public String getCompletedLevelsPath(Game game) {
        if( game == null ) {
            return null;
        }
        return getInProgressGameDirectory(game) + LEVELS_COMPLETED_DIRECTORY;
    }

    /**
     * Returns the path of the original level
     * @param game
     * @param level
     * @return
     */
	static public String getOriginalLevelFilename(Game game, Level level) {
		if( ( level == null ) || ( game == null ) ) {
			return null;
		}

       return getOriginalLevelsPath(game) + "/" + level.getFilename();
	}

    /**
     * Returns the path of the writer in the game's level directory
     * @param game
     * @param pos
     * @return path
     */
    static public String getOriginalLevelFilename(Game game, int[] pos) {
        if( game == null ) {
            return null;
        }

        return getOriginalLevelsPath(game) + "/" + getLevelFilename(pos);
    }

    static public String getLevelFilename(int[] pos) {
        return pos[0]+","+pos[1];
    }

    /**
     * Returns the path of the original level
     * @param game
     * @param level
     * @return path
     */
    static public String getCompletedLevelFilename(Game game, Level level) {
        if( ( level == null ) || ( game == null ) ) {
            return null;
        }

        return getCompletedLevelsPath(game) + "/" + level.getFilename();
    }

    /**
     * Returns the path of the writer in the game's level directory
     * @param game
     * @param pos
     * @return path
     */
    static public String getCompletedLevelFilename(Game game, int[] pos) {
        if( game == null ) {
            return null;
        }

        return getCompletedLevelsPath(game) + "/" + getLevelFilename(pos);
    }
}
