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

package com.strategames.engine.utils;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.storage.LevelWriter;

public class ScreenshotFactory {

	public static Pixmap takeScreenshot(Stage stage, Level level) {
		// As screen may be zoomed out we need to compensate for zooming
		Vector2 worldSize = level.getWorldSize().cpy();
		worldSize.y = 0f;
		stage.stageToScreenCoordinates(worldSize);

		Vector2 worldOrigin = level.getWorldSize().cpy();
		worldOrigin.x = 0f;
		stage.stageToScreenCoordinates(worldOrigin);

		worldSize.x -= worldOrigin.x;
		worldSize.y -= worldOrigin.y;

		return takeScreenShot((int) worldOrigin.x, (int) worldOrigin.y, (int) worldSize.x, (int) worldSize.y, true);
	}


	private static Pixmap takeScreenShot(int x, int y, int w, int h, boolean yDown){
		final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

		if (yDown) {
			// Flip the pixmap upside down
			ByteBuffer pixels = pixmap.getPixels();
			int numBytes = w * h * 4;
			byte[] lines = new byte[numBytes];
			int numBytesPerLine = w * 4;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);
		}

		return pixmap;
	}
}
