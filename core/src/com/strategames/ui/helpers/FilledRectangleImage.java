package com.strategames.ui.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class FilledRectangleImage extends Image {

	private ShapeRenderer shapeRenderer;
	
	public FilledRectangleImage(Stage stage) {
		super();
		this.shapeRenderer = new ShapeRenderer();
		setStage(stage);
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
	protected void setStage(Stage stage) {
		super.setStage(stage);
		if( stage != null ) {
			this.shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
		this.shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
