package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Balloon;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.utils.Textures;

public class BalloonRed extends Balloon {

	public BalloonRed() {
		super();
	}
	
	public BalloonRed(Game game, float x, float y) {
		super(game, x, y);
	}
	
	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.getInstance().balloonRed);
	}

	@Override
	public GameObject copy() {
		BalloonRed balloon = new BalloonRed(getGame(), 
				getX(), 
				getY());
		balloon.setLiftFactor(getLiftFactor());
		return balloon;
	}

}
