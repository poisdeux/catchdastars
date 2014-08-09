package com.strategames.engine.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class RockHitSound extends SoundEffect {

	@Override
	protected Sound getSound() {
		return Gdx.audio.newSound(Gdx.files.internal("sounds/rock_hit.ogg"));
	}
}
