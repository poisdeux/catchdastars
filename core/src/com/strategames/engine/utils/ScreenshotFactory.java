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

public class ScreenshotFactory {
	private static String PATH;

	public static boolean saveScreenshot(Stage stage, Level level) {
		// As screen may be zoomed out we need to compensate for zooming
		Vector2 worldSize = level.getWorldSize().cpy();
		worldSize.y = 0f;
		stage.stageToScreenCoordinates(worldSize);

		Vector2 worldOrigin = level.getWorldSize().cpy();
		worldOrigin.x = 0f;
		stage.stageToScreenCoordinates(worldOrigin);

		worldSize.x -= worldOrigin.x;
		worldSize.y -= worldOrigin.y;

		return saveScreenshot(level.getPositionAsString(), (int) worldOrigin.x, (int) worldOrigin.y, (int) worldSize.x, (int) worldSize.y);
	}

	public static boolean saveScreenshot(String name, int x, int y, int width, int height){
		try{
			FileHandle fh = Gdx.files.local(getDirectoryPath()+name+".png");
			Pixmap pixmap = takeScreenShot(x, y, width, height, true);
			PixmapIO.writePNG(fh, pixmap);
			pixmap.dispose();
		}catch (Exception e){
			Gdx.app.log("ScreenshotFactory", "saveScreenshot: "+e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean deleteScreenshot(Level level) {
		return deleteScreenshot(level.getPositionAsString());
	}

	public static boolean deleteScreenshot(String name) {
		try{
			FileHandle fh = Gdx.files.local(getDirectoryPath()+name+".png");
			if( fh.exists() ) {
				return fh.delete();
			}
		}catch (Exception e){
			Gdx.app.log("ScreenshotFactory", "saveScreenshot: "+e.getMessage());
			return false;
		}
		return true;
	}

	public static Texture loadScreenShot(Level level) {
		return loadScreenShot(level.getPositionAsString());
	}

	/**
	 * Loads the screenshot for the given name
	 * @param name
	 * @return Texture of screenshot or null if not found
	 */
	public static Texture loadScreenShot(String name) {
		Texture texture = null;
		try {
			texture = new Texture(Gdx.files.local(getDirectoryPath()+name+".png"));
		} catch (GdxRuntimeException e) {
			Gdx.app.log("ScreenshotFactory", "loadScreenShot: Error: "+e.getMessage());
		}
		return texture;
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

	private static String getDirectoryPath() {
		if( PATH == null ) {
			PATH = "screenshots/"+ScreenDensity.getDensityName()+"/";
		}
		return PATH;
	}
}
