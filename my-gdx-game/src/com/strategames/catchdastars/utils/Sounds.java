package com.strategames.catchdastars.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class Sounds {

	public static Sound glass;
	public static Sound balloonPop;
	public static Sound balloonBounce;
	public static Sound tinyBell;
	public static Sound cashRegisterOpen;
	public static Sound coinsDrop;
	public static Sound coinsDropMany;
	public static Sound singleCoinDrop;
	public static Sound drawChalkLine;
	public static Sound drawChalkLineShort1;
	public static Sound drawChalkLineShort2;
	public static Sound rockHit;
	public static Sound rockBreak;

	/**
	 * Loads assets asynchronous
	 */
	static public void load(AssetManager manager) {
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
	static public void dispose(AssetManager manager) {
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
	public static void setup(AssetManager manager) {
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

	public static Sound getSoundForIncrement(int increment) {
		Sound sound = Sounds.singleCoinDrop;

		if( increment > 49 ) {
			sound = Sounds.coinsDropMany;
		} else if ( increment > 9 ) {
			sound = Sounds.coinsDrop;
		}

		return sound;
	}
}
