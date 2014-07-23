package com.strategames.engine.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.strategames.desktop.ApplicationSetupAbstractClass;

public class TexturesTest extends ApplicationSetupAbstractClass {
	
	@Test
	public void testSetup() {
		Textures textures = Textures.getInstance();
		assertNotNull("blueBalloon texture is null", textures.balloonBlue);
		assertNotNull("bricksHorizontal texture is null", textures.bricksHorizontal);
	}

}
