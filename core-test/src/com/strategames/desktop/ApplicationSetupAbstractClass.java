package com.strategames.desktop;

import org.junit.Before;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.strategames.engine.game.GameTestClass;
import com.strategames.engine.utils.Textures;

abstract public class ApplicationSetupAbstractClass {
	private static LwjglApplication application;
	private static GameTestClass game;
	private Textures textures;
	
	@Before
	public void setUp() throws Exception {
		if( game == null ) { 
			game = new GameTestClass();
		}
		if(application == null) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "core-test";
			config.width = 504;
			config.height = 800;
			application = new LwjglApplication(game, config);
		}
		//Wait for application to setup
		this.textures  = Textures.getInstance();
		while( ! this.textures.allTexturesLoaded() ) {
			Thread.sleep(100);
		}
		
		while( game.getScreen() == null ) {
			Thread.sleep(100);
		}
	}
	
	public static GameTestClass getGame() {
		return game;
	}
	
	public static LwjglApplication getApplication() {
		return application;
	}
}
