/**
 * 
 * Copyright 2014 Martijn Brekhof
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

package com.strategames.catchdastars.tests.desktop.engine.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.assets.AssetManager;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GdxTestRunner;
import com.strategames.engine.utils.Textures;

@RunWith(GdxTestRunner.class)
public class TexturesTest {

	@Test
	public void testSetup() {
		AssetManager assetManager = new AssetManager();
		
		addTexturesToAssetManagerQueue(assetManager);
		loadAssets(assetManager);
		Textures textures = Textures.getInstance();
		try {
			textures.setup();
		} catch (Exception e) {
			fail("Setting up textures failed: "+e.getMessage());
		}
		assertNotNull("bricksHorizontal texture is null", textures.bricksHorizontal);
		assertNotNull("blueBalloon texture is null", textures.balloonBlue);
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
