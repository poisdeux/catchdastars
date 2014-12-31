package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;

public class ScreenshotImage extends Image {
	private EventHandler eventHandler = new EventHandler(this);
	private Object tag;
	private Array<Overlay> overlays = new Array<Overlay>();
	private Vector2 defaultOverlaySize = new Vector2(30, 30);
	private int defaultOverlayAlignment = Align.center;

	private class Overlay {
		private Vector2 size;
		private TextureRegion image;
		private Vector2 position;

		public Overlay(TextureRegion image, Vector2 size) {
			this.image = image;
			this.size = size;
		}
	}

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
	public void addOverlay(TextureRegion image, Vector2 size, int alignment) {
		if( image == null ) {
			return;
		}

		if( size == null ) {
			size = defaultOverlaySize;
		}

		if( alignment == 0 ) {
			alignment = defaultOverlayAlignment;
		}

		Overlay overlay = new Overlay(image, size);
		alignOverlay(overlay, alignment);
		this.overlays.add(overlay);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		float x = getX();
		float y = getY();
		for(Overlay overlay : this.overlays ) {
			batch.draw(overlay.image, x + overlay.position.x, y + overlay.position.y, 0, 0, overlay.size.x, overlay.size.y, 1f, 1f, 0);
		}
	}

	@Override
	public void layout() {
		super.layout();
	}
	
	private void alignOverlay(Overlay overlay, int align) {
		float width = getWidth();
		float height = getHeight();
		float x = 0;
		float y = 0;

		if( ( align & Align.center ) != 0 ) {
			x = ( width / 2f ) - (overlay.size.x / 2f);
			y = ( height / 2f ) - (overlay.size.y / 2f);
		}

		if( ( align & Align.right ) != 0 ) {
			x = width - overlay.size.x; 
		} else if( ( align & Align.left ) != 0 ) {
			x = 0;
		}

		if( ( align & Align.top ) != 0 ) {
			y = height - overlay.size.y;
		} else if( ( align & Align.bottom ) != 0 ) {
			y = 0;
		}
		
		overlay.position = new Vector2(x, y);
		
		Gdx.app.log("ScreenshotImage", "alignOverlay: align="+align+", x="+x+", y="+y+", width="+width+
				", height="+height+", overlay.size="+overlay.size);
		
	}
}
