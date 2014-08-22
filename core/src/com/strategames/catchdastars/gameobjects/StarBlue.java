package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.gameobjects.Star;
import com.strategames.engine.sounds.GlassSound;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.Textures;

public class StarBlue extends Star {

	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().starBlue;
	}

	@Override
	protected GameObject newInstance() {
		return new StarBlue();
	}

	@Override
	protected SoundEffect getSoundCollected() {
		return new GlassSound();
	}
	
	@Override
	public void applyForce() {
	}
}
