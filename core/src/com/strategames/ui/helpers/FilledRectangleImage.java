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
	private Color color;
	private Color shapeRendererColor;
	private ShapeRenderer shapeRenderer;
	
	public FilledRectangleImage(Stage stage) {
		super();
		this.shapeRenderer = new ShapeRenderer();
		setStage(stage);
		this.color = getColor();
		this.shapeRendererColor = this.shapeRenderer.getColor();
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
	    this.shapeRendererColor.a = this.color.a;
	    this.shapeRendererColor.r = this.color.r;
	    this.shapeRendererColor.g = this.color.g;
	    this.shapeRendererColor.b = this.color.b;
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
		this.shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
