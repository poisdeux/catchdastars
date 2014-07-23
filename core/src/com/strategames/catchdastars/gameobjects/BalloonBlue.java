package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Balloon;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.utils.Textures;

public class BalloonBlue extends Balloon {

	public BalloonBlue() {
		super();
	}
	
	public BalloonBlue(Game game, float x, float y) {
		super(game, x, y);
	}
	
	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.getInstance().balloonBlue);
	}

	@Override
	public GameObject copy() {
		BalloonBlue balloon = new BalloonBlue(getGame(), 
				getX(), 
				getY());
		balloon.setLiftFactor(getLiftFactor());
		return balloon;
	}

}
