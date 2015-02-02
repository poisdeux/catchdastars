package com.strategames.engine.storage;

import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class Files {
	static private final String PATH = "games";
	
	static public String getGamesDirectory() {
		return PATH;
	}

	static public String getPath(String name) {
		return PATH + "/" + name;
	}
	
	static public String getGameDirectory(Game game) {
		if( game == null ) {
			return null;
		}
		return PATH + "/" + game.getUuid() + "/";
	}
	
	static public String getGameMetaPath(Game game) {
		return getGameDirectory(game) + "/meta";
	}
	
	static public String getOriginalLevelsPath(Game game) {
		if( game == null ) {
			return null;
		}
		return getGameDirectory(game) + "/levels";
	}

    static public String getCompletedLevelsPath(Game game) {
        if( game == null ) {
            return null;
        }
        return getGameDirectory(game) + "/levels_completed";
    }

    /**
     * Returns the path of the original level
     * @param game
     * @param level
     * @return
     */
	static public String getOriginalLevelPath(Game game, Level level) {
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
    static public String getOriginalLevelPath(Game game, int[] pos) {
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
    static public String getCompletedLevelPath(Game game, Level level) {
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
    static public String getCompletedLevelPath(Game game, int[] pos) {
        if( game == null ) {
            return null;
        }

        return getCompletedLevelsPath(game) + "/" + getLevelFilename(pos);
    }
}
