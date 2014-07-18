package com.strategames.engine.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class Sounds {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final Sounds INSTANCE = new Sounds();
	}

	private float globalVolume = 1f;

	public Sound glass;
	public Sound balloonPop;
	public Sound balloonBounce;
	public Sound tinyBell;
	public Sound cashRegisterOpen;
	public Sound coinsDrop;
	public Sound coinsDropMany;
	public Sound singleCoinDrop;
	public Sound drawChalkLine;
	public Sound drawChalkLineShort1;
	public Sound drawChalkLineShort2;
	public Sound rockHit;
	public Sound rockBreak;
	
	private Sounds() {
		this.globalVolume = Settings.getInstance().getSfxVolume();
	}
	
	public static Sounds getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Adds the sounds to the AssetManager load queue
	 * <br/>
	 * Note this does not actually load the assets just yet.
	 * <br/>
	 * Use {@link AssetManager#update()} to load the actual assets
	 * @param manager
	 */
	public void addToAssetManager(AssetManager manager) {
		manager.load("sounds/glass.ogg", Sound.class);
		manager.load("sounds/balloon_pop.mp3", Sound.class);
		manager.load("sounds/single_balloon_bounce.ogg", Sound.class);
		manager.load("sounds/tiny_bell.ogg", Sound.class);
		manager.load("sounds/cash_register_open.ogg", Sound.class);
		manager.load("sounds/coins_drop_1_beginning.ogg", Sound.class);
		manager.load("sounds/coins_drop_1.ogg", Sound.class);
		manager.load("sounds/single_coin_drop_1.ogg", Sound.class);
		manager.load("sounds/draw_line.ogg", Sound.class);
		manager.load("sounds/draw_short_line.ogg", Sound.class);
		manager.load("sounds/draw_short_line_2.ogg", Sound.class);
		manager.load("sounds/rock_hit.ogg", Sound.class);
		manager.load("sounds/rock_hit_break1.ogg", Sound.class);
	}

	/**
	 * Unloads all loaded assets
	 */
	public void dispose(AssetManager manager) {
		manager.unload("sounds/glass.ogg");
		manager.unload("sounds/balloon_pop.mp3");
		manager.unload("sounds/single_balloon_bounce.ogg");
		manager.unload("sounds/tiny_bell.ogg");
		manager.unload("sounds/cash_register_open.ogg");
		manager.unload("sounds/coins_drop_1_beginning.ogg");
		manager.unload("sounds/coins_drop_1.ogg");
		manager.unload("sounds/single_coin_drop_1.ogg");
		manager.unload("sounds/draw_line.ogg");
		manager.unload("sounds/draw_short_line.ogg");
		manager.unload("sounds/draw_short_line_2.ogg");
		manager.unload("sounds/rock_hit.ogg");
		manager.unload("sounds/rock_hit_break1.ogg");
	}

	/**
	 * Call this to fill the different sounds from the AssetManager
	 * <br/>
	 * Note you cannot access the sounds before calling this method
	 * @param manager
	 */
	public void setup(AssetManager manager) {
		glass = manager.get("sounds/glass.ogg", Sound.class);
		balloonPop = manager.get("sounds/balloon_pop.mp3", Sound.class);
		balloonBounce = manager.get("sounds/single_balloon_bounce.ogg", Sound.class);
		tinyBell = manager.get("sounds/tiny_bell.ogg", Sound.class);
		cashRegisterOpen = manager.get("sounds/cash_register_open.ogg", Sound.class);
		coinsDrop = manager.get("sounds/coins_drop_1_beginning.ogg", Sound.class);
		coinsDropMany = manager.get("sounds/coins_drop_1.ogg", Sound.class);
		singleCoinDrop = manager.get("sounds/single_coin_drop_1.ogg", Sound.class);
		drawChalkLine = manager.get("sounds/draw_line.ogg", Sound.class);
		drawChalkLineShort1 = manager.get("sounds/draw_short_line.ogg", Sound.class);
		drawChalkLineShort2 = manager.get("sounds/draw_short_line_2.ogg", Sound.class);
		rockHit = manager.get("sounds/rock_hit.ogg", Sound.class);
		rockBreak = manager.get("sounds/rock_hit_break1.ogg", Sound.class);
	}

	public Sound getSoundForIncrement(int increment) {
		Sound sound = this.singleCoinDrop;

		if( increment > 49 ) {
			sound = this.coinsDropMany;
		} else if ( increment > 9 ) {
			sound = this.coinsDrop;
		}

		return sound;
	}

	/**
	 * Plays given sound relative to global volume setting
	 * @param sound
	 * @param volume the volume of this sound in relation to the global volume setting
	 */
	public void play(Sound sound, float volume) {
		sound.play(volume * globalVolume);
	}

	public void play(Sound sound) {
		sound.play(globalVolume);
	}
	
	/**
	 * Sets the volume in the range [0,1]
	 * @param volume in the range [0,1]. 
	 * Levels lower then 0 are set to 0 and levels higher then 1 are set 1
	 */
	public void setVolume(float volume) {
		if( volume < 0f ) {
			this.globalVolume = 0f;
		} else if( volume > 1f ){
			this.globalVolume = 1f;
		} else {
			this.globalVolume = volume;
		}
	}
}
