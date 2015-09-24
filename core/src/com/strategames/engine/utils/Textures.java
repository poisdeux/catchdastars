/**
 * 
 * Copyright 2013 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.utils;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final Textures INSTANCE = new Textures();
	}

	public TextureRegion balloonBlue;
	public TextureRegion balloonRed;
	public TextureRegion starBlue;
	public TextureRegion starYellow;
	public TextureRegion starRed;
	public TextureRegion chalk1;
	public TextureRegion chalk2;
	public TextureRegion chalk3;
	public TextureRegion chalk4;
	public TextureRegion chalk5;
	public TextureRegion cashRegister;
	public TextureRegion icecube;
	public TextureRegion icecubePart1;
	public TextureRegion icecubePart2;
	public TextureRegion icecubePart3;
	public TextureRegion icecubePart4;
	public TextureRegion icecubePart5;
	public TextureRegion icecubePart6;
	public TextureRegion icecubePart7;
	public TextureRegion icecubePart8;
	public TextureRegion icecubePart9;
	public TextureRegion icecubePart10;
	public TextureRegion level;
	public TextureRegion digit0;
	public TextureRegion digit1;
	public TextureRegion digit2;
	public TextureRegion digit3;
	public TextureRegion digit4;
	public TextureRegion digit5;
	public TextureRegion digit6;
	public TextureRegion digit7;
	public TextureRegion digit8;
	public TextureRegion digit9;
	public TextureRegion gridPoint;
	public TextureRegion menu;
	public TextureRegion passageToNextLevel;
	public TextureRegion arrowLeft;
	public TextureRegion arrowRight;
	public TextureRegion arrowTop;
	public TextureRegion arrowBottom;
	public Texture		 Loading;
	public Texture		 dot;
	public Texture 		 bricksHorizontal;
	public Texture 		 bricksHorizontalEndLeft;
	public Texture 		 bricksHorizontalEndRight;
	public Texture 		 bricksVertical;
	
	private boolean texturesLoaded = false;
	
	private String atlasFilename;
	private AssetManager assetManager;
	
	private Textures() {
		
	}
	
	public static Textures getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public boolean allTexturesLoaded() {
		return texturesLoaded;
	}
	
	/**
	 * Adds the textures to the AssetManager loadSync queue
	 * <br/>
	 * Note this does not actually loadSync the assets just yet.
	 * <br/>
	 * Use {@link AssetManager#update()} to loadSync the actual assets
	 * @param manager
	 * @throws FileNotFoundException
	 */
	public void addAllToAssetManager(AssetManager manager) throws FileNotFoundException {
		this.assetManager = manager;
		
		atlasFilename = "packed/"+ScreenDensity.getDensityName()+".atlas";
		if( Gdx.files.internal(atlasFilename).exists() ) {
			this.assetManager.load(atlasFilename, TextureAtlas.class);
		} else {
			throw new FileNotFoundException("Could not find atlas "+atlasFilename);
		}
	}

	/**
	 * Unloads all loaded assets
	 * @throws Exception 
	 */
	public void dispose() throws Exception {
		if( this.assetManager == null ) {
			throw new Exception("AssetManager not available");
		}
		this.texturesLoaded = false;
		this.assetManager.unload(atlasFilename);
	}

	/**
	 * Call this to fill create the different textures.
	 * <br/>
	 * Note DO NOT call this method if AssetManager is not ready loading.
	 * <br/>
	 * Use {@link #addAllToAssetManager(AssetManager)} setup the asset manager and use {@link AssetManager#update()}
	 * to loadSync assets.
	 * <br/>
	 * Note you cannot access the textures before calling this method
	 * @param manager
	 * @throws Exception 
	 */
	public void setup() throws Exception {
		if( this.assetManager == null ) {
			throw new Exception("AssetManager not available");
		}
		
		TextureAtlas atlas = this.assetManager.get(atlasFilename, TextureAtlas.class);

		balloonBlue = atlas.findRegion("aj_balloon_blue");
		balloonRed = atlas.findRegion("aj_balloon_red");
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
		passageToNextLevel = atlas.findRegion("passagetonextlevel");
		arrowLeft = atlas.findRegion("tango_arrow_left");
		arrowRight = atlas.findRegion("tango_arrow_right");
		arrowTop = atlas.findRegion("tango_arrow_top");
		arrowBottom = atlas.findRegion("tango_arrow_bottom");
		
		String path = "images/"+ScreenDensity.getDensityName();
		bricksHorizontal = new Texture(path+"/bricks-texture-horizontal.png");
		bricksHorizontalEndRight = new Texture(path+"/bricks-texture-horizontal-right-end.png");
		bricksHorizontalEndLeft = new Texture(path+"/bricks-texture-horizontal-left-end.png");
		bricksVertical = new Texture(path+"/bricks-texture-vertical.png");
		dot = new Texture(path+"/dot.png");
		Loading = new Texture(path+"/Loading.png");
		this.texturesLoaded = true;
	}

	public Texture getSplashScreen() {
		return new Texture( "images/"+ScreenDensity.getDensityName()+"/splashscreen.png" );
	}
}
