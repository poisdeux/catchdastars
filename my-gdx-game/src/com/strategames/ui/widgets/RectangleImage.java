package com.strategames.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class RectangleImage extends Image {

	private ShapeRenderer shapeRenderer;
	
	public RectangleImage() {
		this.shapeRenderer = new ShapeRenderer();
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		this.shapeRenderer.setColor(r, g, b, a);
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		this.shapeRenderer.setColor(color);
	}
	
	@Override
	public void setY(float y) {
		super.setY(Gdx.app.getGraphics().getHeight() - y);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.end();
		Gdx.gl.glEnable(GL10.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.shapeRenderer.begin(ShapeType.FilledRectangle);
		this.shapeRenderer.filledRect(getX(), getY(), getWidth(), getHeight());
		this.shapeRenderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);
		batch.begin();
	}
}
