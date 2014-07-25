package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.gameobjects.Star;
import com.strategames.engine.utils.Textures;

public class StarRed extends Star {

	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.getInstance().starRed);
	}
	
	@Override
	protected GameObject newInstance() {
		return new StarRed();
	}
}
