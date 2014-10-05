package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Star;
import com.strategames.engine.sounds.GlassSound;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.Textures;

public class StarYellow extends Star {

	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().starYellow;
	}

	@Override
	protected GameObject newInstance() {
		return new StarYellow();
	}
	
	@Override
	protected SoundEffect getSoundCollected() {
		return new GlassSound();
	}
}
