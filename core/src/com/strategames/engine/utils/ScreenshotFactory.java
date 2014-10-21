package com.strategames.engine.utils;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenshotFactory {
	private static final String DIRECTORY = "screenshots";

	public static boolean saveScreenshot(String name, int x, int y, int width, int height){
		try{
			FileHandle fh = Gdx.files.local(DIRECTORY+"/"+name+".png");
			Pixmap pixmap = getScreenshot(x, y, width, height, true);
			PixmapIO.writePNG(fh, pixmap);
			pixmap.dispose();
		}catch (Exception e){
			Gdx.app.log("ScreenshotFactory", "saveScreenshot: "+e.getMessage());
			return false;
		}
		return true;
	}

	private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
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
