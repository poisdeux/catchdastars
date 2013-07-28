package com.strategames.catchdastars.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;

public class LevelLoader extends AsynchronousAssetLoader<Level, LevelLoader.LevelLoaderParameter> {
	private Level level;
	
	public LevelLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	static public class LevelLoaderParameter extends AssetLoaderParameters<Level> {
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			LevelLoaderParameter parameter) {
		
	}

	@Override
	public Level loadSync(AssetManager manager, String fileName,
			LevelLoaderParameter parameter) {
		this.level = Level.load(fileName);
		return this.level;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String arg0,
			LevelLoaderParameter arg1) {
		return null;
	}
}
