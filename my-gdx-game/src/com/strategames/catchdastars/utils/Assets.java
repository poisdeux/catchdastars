package com.strategames.catchdastars.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

	private static String texturesFilename = "packed/pack.atlas";
	
	/**
	 * Loads assets asyncronous
	 */
	static public void load(AssetManager manager) {
		manager.load(texturesFilename, TextureAtlas.class);
	}
	
	/**
	 * @return true if all assets are loaded, false otherwise
	 */
	static public boolean finishedLoading(AssetManager manager) {
		return manager.update();
	}
	
	/**
	 * Unloads all loaded assets
	 */
	static public void dispose(AssetManager manager) {
		manager.unload(texturesFilename);
	}
	
	static public TextureAtlas getTextureAtlas(AssetManager manager) {
		return manager.get(texturesFilename, TextureAtlas.class);
	}
}
