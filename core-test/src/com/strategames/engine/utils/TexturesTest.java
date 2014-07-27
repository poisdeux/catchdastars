package com.strategames.engine.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.assets.AssetManager;
import com.strategames.engine.screens.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TexturesTest {

	@Test
	public void testSetup() {
		System.out.println("1");
		AssetManager assetManager = new AssetManager();
		
		addTexturesToAssetManagerQueue(assetManager);
		loadAssets(assetManager);
		System.out.println("2");
		Textures textures = Textures.getInstance();
		try {
			textures.setup();
		} catch (Exception e) {
			fail("Setting up textures failed: "+e.getMessage());
		}
		System.out.println("3");
		assertNotNull("bricksHorizontal texture is null", textures.bricksHorizontal);
		assertNotNull("blueBalloon texture is null", textures.balloonBlue);
		System.out.println("4");
	}
	
	private void addTexturesToAssetManagerQueue(AssetManager assetManager) {
		Textures textures = Textures.getInstance();
		try {
			textures.addAllToAssetManager(assetManager);
		} catch (FileNotFoundException e) {
			fail("Assets not found: "+e.getMessage());
		}
	}
	
	private void loadAssets(AssetManager assetManager) {
		int count = 0;
		
		while(! assetManager.update()) {
			if(count++ > 100) {
				fail("AssetManager takes more then 10 seconds");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
