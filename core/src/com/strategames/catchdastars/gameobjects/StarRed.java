package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.catchdastars.Game;
import com.strategames.engine.utils.Textures;
import com.strategames.gameobjects.GameObject;
import com.strategames.gameobjects.Star;

public class StarRed extends Star {

	public StarRed() {
		super();
	}
	
	public StarRed(Game game, float x, float y) {
		super(game, x, y);
	}
	
	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.starRed);
	}
	
	@Override
	public GameObject copy() {
		return new StarRed(getGame(), getX(), getY());
	}
}
