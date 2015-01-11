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
	
	static public String getLevelsPath(Game game) {
		if( game == null ) {
			return null;
		}
		return getGameDirectory(game) + "/levels";
	}

    /**
     * Returns the path of the writer in the game's level directory
     * @param game
     * @param writer
     * @return
     */
	static public String getLevelPath(Game game, Writer writer) {
		if( ( writer == null ) || ( game == null ) ) {
			return null;
		}

       return getLevelsPath(game) + "/" + writer.getFilename();
	}

    static public String getLevelPath(Game game, int[] pos) {
        if( game == null ) {
            return null;
        }

        return getLevelsPath(game) + "/" + getLevelFilename(pos);
    }

    static public String getLevelFilename(int[] pos) {
        return pos[0]+","+pos[1];
    }
}
