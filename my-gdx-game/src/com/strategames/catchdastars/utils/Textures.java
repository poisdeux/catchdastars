package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {

	public static Texture items;
	public static TextureRegion blueBalloon;
	public static TextureRegion starBlue;
	public static TextureRegion starYellow;
	public static TextureRegion starRed;
	public static TextureRegion bricksHorizontal;
	public static TextureRegion bricksHorizontalEndLeft;
	public static TextureRegion bricksHorizontalEndRight;
	public static TextureRegion bricksVertical;
	
	
	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load () {		
		TextureAtlas atlas;
		atlas = new TextureAtlas(Gdx.files.internal("packed/pack.atlas"));
		
		blueBalloon = atlas.findRegion("aj_balloon_blue");
		starBlue = atlas.findRegion("star_blue");
		starRed = atlas.findRegion("star_red");
		starYellow = atlas.findRegion("star_yellow");
		bricksHorizontal = atlas.findRegion("bricks-texture-horizontal");
		bricksHorizontalEndRight = atlas.findRegion("bricks-texture-horizontal-right-end");
		bricksHorizontalEndLeft = atlas.findRegion("bricks-texture-horizontal-left-end");
		bricksVertical = atlas.findRegion("bricks-texture-vertical");
	}

}
