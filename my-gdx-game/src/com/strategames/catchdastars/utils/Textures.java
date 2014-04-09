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
	public static Texture 		bricksHorizontal;
	public static Texture 		bricksHorizontalEndLeft;
	public static Texture 		bricksHorizontalEndRight;
	public static Texture 		bricksVertical;
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
	public static TextureRegion level;
	public static TextureRegion digit0;
	public static TextureRegion digit1;
	public static TextureRegion digit2;
	public static TextureRegion digit3;
	public static TextureRegion digit4;
	public static TextureRegion digit5;
	public static TextureRegion digit6;
	public static TextureRegion digit7;
	public static TextureRegion digit8;
	public static TextureRegion digit9;
	public static TextureRegion gridPoint;
	public static TextureRegion	menu;
	public static Texture		Loading;
	public static Texture		dot;
	
	private static String atlasFilename;
	private static String screenDensity;

	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	/**
	 * Loads assets asynchronous
	 */
	static public void load(AssetManager manager) {
		if( screenDensity == null ) {
			screenDensity = getScreenDensity();
		}

		atlasFilename = "packed/"+screenDensity+".atlas";
		manager.load(atlasFilename, TextureAtlas.class);
	}

	/**
	 * Unloads all loaded assets
	 */
	static public void dispose(AssetManager manager) {
		manager.unload(atlasFilename);
	}

	/**
	 * Call this to fill the different textures from the AssetManager
	 * <br/>
	 * Note you cannot access the textures before calling this method
	 * @param manager
	 */
	public static void setup(AssetManager manager) {
		if( atlasFilename == null ) {
			load(manager);
		}

		String path = "images/"+screenDensity;
		bricksHorizontal = new Texture(path+"/bricks-texture-horizontal.png");
		bricksHorizontalEndRight = new Texture(path+"/bricks-texture-horizontal-right-end.png");
		bricksHorizontalEndLeft = new Texture(path+"/bricks-texture-horizontal-left-end.png");
		bricksVertical = new Texture(path+"/bricks-texture-vertical.png");
		dot = new Texture(path+"/dot.png");
		Loading = new Texture(path+"/Loading.png");

		TextureAtlas atlas = manager.get(atlasFilename, TextureAtlas.class);

		blueBalloon = atlas.findRegion("aj_balloon_blue");
		redBalloon = atlas.findRegion("aj_balloon_red");
		starBlue = atlas.findRegion("star_blue");
		starRed = atlas.findRegion("star_red");
		starYellow = atlas.findRegion("star_yellow");
		chalk1 = atlas.findRegion("Chalk-01");
		chalk2 = atlas.findRegion("Chalk-02");
		chalk3 = atlas.findRegion("Chalk-03");
		chalk4 = atlas.findRegion("Chalk-04");
		chalk5 = atlas.findRegion("Chalk-05");
		cashRegister = atlas.findRegion("cash_register");
		icecube = atlas.findRegion("icecube");
		icecubePart1 = atlas.findRegion("icecube-part01");
		icecubePart2 = atlas.findRegion("icecube-part02");
		icecubePart3 = atlas.findRegion("icecube-part03");
		icecubePart4 = atlas.findRegion("icecube-part04");
		icecubePart5 = atlas.findRegion("icecube-part05");
		icecubePart6 = atlas.findRegion("icecube-part06");
		icecubePart7 = atlas.findRegion("icecube-part07");
		icecubePart8 = atlas.findRegion("icecube-part08");
		icecubePart9 = atlas.findRegion("icecube-part09");
		icecubePart10 = atlas.findRegion("icecube-part10");
		level = atlas.findRegion("Level");
		digit0 = atlas.findRegion("0");
		digit1 = atlas.findRegion("1");
		digit2 = atlas.findRegion("2");
		digit3 = atlas.findRegion("3");
		digit4 = atlas.findRegion("4");
		digit5 = atlas.findRegion("5");
		digit6 = atlas.findRegion("6");
		digit7 = atlas.findRegion("7");
		digit8 = atlas.findRegion("8");
		digit9 = atlas.findRegion("9");
		gridPoint = atlas.findRegion("gridpoint");
		menu = atlas.findRegion("icon-menu");
	}

	public static Texture getSplashScreen() {
		if( screenDensity == null ) {
			screenDensity = getScreenDensity();
		}

		return new Texture( "images/"+screenDensity+"/splashscreen.png" );
	}

	private static String getScreenDensity() {
		String densityModifier = "mdpi";

		float density = Gdx.graphics.getDensity();
		
		/**
		 *  we originally designed the game for a 800x480 hdpi screen
		 *  mdpi	533.33333
		 *  hdpi	800
		 *  xhdpi	1066.66666
		 *  xxhdpi	1600	
		 */

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		float size = width > height ? width : height;
		//		if( size >= 1599 ) {
		//			densityModifier = "xxhdpi";
		//		} else if ( size >= 1066 ) {
		//			densityModifier = "xhdpi";
		//		} else if ( size >= 799 ) {
		//			densityModifier = "hdpi";
		//		}
		if( density >= 3 ) {
			densityModifier = "xxhdpi";
		} else if ( density >= 2 ) {
			densityModifier = "xhdpi";
		} else if ( density >= 1.5 ) {
			densityModifier = "hdpi";
		}
		Gdx.app.log("Textures", "getScreenDensity(): density="+densityModifier+", screenWidth="+size+
				", density="+density);
		return densityModifier;
	}
}
