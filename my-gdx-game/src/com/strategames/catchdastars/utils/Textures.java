package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {

	public static TextureRegion blueBalloon;
	public static TextureRegion redBalloon;
	public static TextureRegion starBlue;
	public static TextureRegion starYellow;
	public static TextureRegion starRed;
	public static TextureRegion bricksHorizontal;
	public static TextureRegion bricksHorizontalEndLeft;
	public static TextureRegion bricksHorizontalEndRight;
	public static TextureRegion bricksVertical;
	public static TextureRegion chalk1;
	public static TextureRegion chalk2;
	public static TextureRegion chalk3;
	public static TextureRegion chalk4;
	public static TextureRegion chalk5;
	public static TextureRegion cashRegister;
	public static TextureRegion icecube;
	public static TextureRegion icecubePart1;
	public static TextureRegion icecubePart2;
	public static TextureRegion icecubePart3;
	public static TextureRegion icecubePart4;
	public static TextureRegion icecubePart5;
	public static TextureRegion icecubePart6;
	public static TextureRegion icecubePart7;
	public static TextureRegion icecubePart8;
	public static TextureRegion icecubePart9;
	public static TextureRegion icecubePart10;
	
	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	/**
	 * Loads assets asynchronous
	 */
	static public void load(AssetManager manager) {
		manager.load("packed/pack.atlas", TextureAtlas.class);
	}
	
	/**
	 * Unloads all loaded assets
	 */
	static public void dispose(AssetManager manager) {
		manager.unload("packed/pack.atlas");
	}
	
	/**
	 * Call this to fill the different textures from the AssetManager
	 * <br/>
	 * Note you cannot access the textures before calling this method
	 * @param manager
	 */
	public static void setup(AssetManager manager) {
		Gdx.app.log("Textures", "setup:");
		TextureAtlas atlas = manager.get("packed/pack.atlas", TextureAtlas.class);
		
		blueBalloon = atlas.findRegion("aj_balloon_blue");
		redBalloon = atlas.findRegion("aj_balloon_red");
		starBlue = atlas.findRegion("star_blue");
		starRed = atlas.findRegion("star_red");
		starYellow = atlas.findRegion("star_yellow");
		bricksHorizontal = atlas.findRegion("bricks-texture-horizontal");
		bricksHorizontalEndRight = atlas.findRegion("bricks-texture-horizontal-right-end");
		bricksHorizontalEndLeft = atlas.findRegion("bricks-texture-horizontal-left-end");
		bricksVertical = atlas.findRegion("bricks-texture-vertical");
		chalk1 = atlas.findRegion("Chalk-01");
		chalk2 = atlas.findRegion("Chalk-02");
		chalk3 = atlas.findRegion("Chalk-03");
		chalk4 = atlas.findRegion("Chalk-04");
		chalk5 = atlas.findRegion("Chalk-05");
		cashRegister = atlas.findRegion("cash_register");
		icecube = atlas.findRegion("icecube");
		icecubePart1 = atlas.findRegion("icecube-part1");
		icecubePart2 = atlas.findRegion("icecube-part2");
		icecubePart3 = atlas.findRegion("icecube-part3");
		icecubePart4 = atlas.findRegion("icecube-part4");
		icecubePart5 = atlas.findRegion("icecube-part5");
		icecubePart6 = atlas.findRegion("icecube-part6");
		icecubePart7 = atlas.findRegion("icecube-part7");
		icecubePart8 = atlas.findRegion("icecube-part8");
		icecubePart9 = atlas.findRegion("icecube-part9");
		icecubePart10 = atlas.findRegion("icecube-part10");
	}
}
