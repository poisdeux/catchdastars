package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
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
	
	public static void load() {
		glass = Gdx.audio.newSound(Gdx.files.internal("sounds/glass.ogg"));
		balloonPop = Gdx.audio.newSound(Gdx.files.internal("sounds/balloon_pop.mp3"));
		balloonBounce = Gdx.audio.newSound(Gdx.files.internal("sounds/single_balloon_bounce.ogg"));
		tinyBell = Gdx.audio.newSound(Gdx.files.internal("sounds/tiny_bell.ogg"));
		cashRegisterOpen = Gdx.audio.newSound(Gdx.files.internal("sounds/cash_register_open.ogg"));
		coinsDrop = Gdx.audio.newSound(Gdx.files.internal("sounds/coins_drop_1_beginning.ogg"));
		coinsDropMany = Gdx.audio.newSound(Gdx.files.internal("sounds/coins_drop_1.ogg"));
		singleCoinDrop = Gdx.audio.newSound(Gdx.files.internal("sounds/single_coin_drop_1.ogg"));
		drawChalkLine = Gdx.audio.newSound(Gdx.files.internal("sounds/draw_line.ogg"));
		drawChalkLineShort1 = Gdx.audio.newSound(Gdx.files.internal("sounds/draw_short_line.ogg"));
		drawChalkLineShort2 = Gdx.audio.newSound(Gdx.files.internal("sounds/draw_short_line_2.ogg"));
	}
	
	public static void dispose() {
		glass.dispose();
		balloonPop.dispose();
		balloonBounce.dispose();
		tinyBell.dispose();
		cashRegisterOpen.dispose();
		coinsDrop.dispose();
		coinsDropMany.dispose();
		singleCoinDrop.dispose();
		drawChalkLine.dispose();
		drawChalkLineShort1.dispose();
		drawChalkLineShort2.dispose();
	}
	
	public static Sound getSoundForIncrement(int increment) {
		Sound sound = Sounds.singleCoinDrop;
		
		if( increment > 9 ) {
			sound = Sounds.coinsDrop;
		} else if ( increment > 49 ) {
			sound = Sounds.coinsDropMany;
		}
		
		return sound;
	}
}
