package com.strategames.engine.utils;

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
	
	static public String getLevelPath(Game game, int[] position) {
		if( ( position.length < 2 ) || ( game == null ) ) {
			return null;
		}
		return getLevelsPath(game) + "/" + position[0] +"," +position[1];
	}
	
	static public String getLevelPath(Game game, Level level) {
		if( ( level == null ) || ( game == null ) ) {
			return null;
		}
		
		int[] position = level.getPosition();
		return getLevelsPath(game) + "/" + position[0] +"," +position[1];
	}
}
