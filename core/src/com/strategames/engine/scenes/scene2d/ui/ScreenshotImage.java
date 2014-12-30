package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;

public class ScreenshotImage extends Image {
	private EventHandler eventHandler = new EventHandler(this);
	private Object tag;
	private Array<TextureRegion> overlayImages = new Array<TextureRegion>();
	private Vector2 overlaySize = new Vector2(30, 30);
	
	public ScreenshotImage(Drawable drawable) {
		super(drawable);
		addListener(this.eventHandler);
	}

	public ScreenshotImage(Texture texture) {
		super(texture);
		addListener(this.eventHandler);
	}

	public void setListener(ActorListener listener) {
		this.eventHandler.setListener(listener);
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public Object getTag() {
		return this.tag;
	}

	/**
	 * Adds an image to draw on top of this ScreenshotImage.
	 * <br/>
	 * Note that the overlay will be positioned relative to the ScreenshotImage.
	 * Thus if overlay is set to 10,20 and ScreenshotImage is set to 100,32 the
	 * overlay will be positioned at absolute coordinates 110,52 (i.e. 100 + 10, 32 + 20)
	 * @param overlay
	 */
	public void addOverlay(TextureRegion overlay) {
		if( overlay == null ) {
			return;
		}
		this.overlayImages.add(overlay);
	}

	public boolean removeOverlay(TextureRegion overlay) {
		return this.overlayImages.removeValue(overlay, true);
	}

	public void setOverlaySize(Vector2 overlaySize) {
		this.overlaySize = overlaySize;
	}
	
	public Vector2 getOverlaySize() {
		return overlaySize;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		float x = getX();
		float y = getY();
		for(TextureRegion overlay : this.overlayImages ) {
			batch.draw(overlay, x, y, 0, 0, overlaySize.x, overlaySize.y, 1f, 1f, 0);
		}
	}
}
