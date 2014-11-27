package com.strategames.engine.utils;

public class Files {
	static private final String PATH = "games";
	
	static public String getGamesPath() {
		return PATH;
	}

	static public String getPath(String name) {
		return PATH + "/" + name;
	}
	
	static public String getGamePath(Game game) {
		return PATH + "/" + game.getUuid() + "/";
	}
	
	static public String getLevelsPath(Game game) {
		return PATH + "/" + game.getUuid() + "/levels";
	}
}
