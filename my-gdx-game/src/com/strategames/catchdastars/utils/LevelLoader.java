package com.strategames.catchdastars.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;

public class LevelLoader extends AsynchronousAssetLoader<Level, LevelLoader.LevelLoaderParameter> {

	public LevelLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	static public class LevelLoaderParameter extends AssetLoaderParameters<Level> {
		public int levelNumber = 0;
	}

	@Override
	public void loadAsync(AssetManager arg0, String arg1,
			LevelLoaderParameter parameter) {
		Level level = Level.loadLocal(parameter.levelNumber);
		
	}

	@Override
	public Level loadSync(AssetManager arg0, String arg1,
			LevelLoaderParameter arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String arg0,
			LevelLoaderParameter arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
