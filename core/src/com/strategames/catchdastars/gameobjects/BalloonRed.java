package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.gameobjects.Balloon;
import com.strategames.engine.utils.Textures;

public class BalloonRed extends Balloon {
		
	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.getInstance().balloonRed);
	}

	@Override
	protected Balloon newInstance() {
		return new BalloonRed();
	}

}
