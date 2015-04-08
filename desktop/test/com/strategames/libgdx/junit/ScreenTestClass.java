package com.strategames.libgdx.junit;

import java.io.FileNotFoundException;

import com.badlogic.gdx.assets.AssetManager;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.Textures;

public class ScreenTestClass extends AbstractScreen {

	public ScreenTestClass(GameEngine game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		AssetManager assetManager = getGameEngine().getManager();
		
		Textures textures = Textures.getInstance();
		try {
			textures.addAllToAssetManager(assetManager);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while( ! assetManager.update() ) {};
		
		try {
			textures.setup();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void setupActors(Stage stage) {
		
	}

}
