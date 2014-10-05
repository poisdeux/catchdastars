package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.utils.Textures;

public class BalloonBlue extends Balloon {
		
	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().balloonBlue;
	}

	@Override
	protected Balloon newInstance() {
		return new BalloonBlue();
	}

}
