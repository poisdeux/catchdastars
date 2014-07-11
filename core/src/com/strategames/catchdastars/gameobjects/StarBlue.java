package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.gameobjects.Star;
import com.strategames.engine.utils.Textures;

public class StarBlue extends Star {

	public StarBlue() {
		super();
	}
	
	public StarBlue(Game game, float x, float y) {
		super(game, x, y);
	}
	
	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.getInstance().starBlue);
	}

	@Override
	public GameObject copy() {
		return new StarBlue(getGame(), getX(), getY());
	}
}
