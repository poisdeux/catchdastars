package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.gameobjects.Star;
import com.strategames.engine.utils.Textures;

public class StarYellow extends Star {

	public StarYellow() {
		super();
	}
	
	public StarYellow(Game game, float x, float y) {
		super(game, x, y);
	}
	
	@Override
	protected TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.getInstance().starYellow);
	}
	
	@Override
	public GameObject copy() {
		return new StarYellow(getGame(), getX(), getY());
	}
}
