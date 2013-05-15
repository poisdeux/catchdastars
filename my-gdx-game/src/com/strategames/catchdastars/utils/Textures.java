package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

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
	
	private static TextureAtlas atlas;
	
	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load () {		
		atlas = new TextureAtlas(Gdx.files.internal("packed/pack.atlas"));
		
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
	}

	public static void dispose() {
		atlas.dispose();
	}
}
