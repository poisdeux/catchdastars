package com.strategames.ui.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;



public class Filter extends Image {
	private ShapeRenderer shapeRenderer;
	private float width;
	private float height;
	private Color color;
	
	public Filter(float width, float height) {
		this.shapeRenderer = new ShapeRenderer();
		this.shapeRenderer.scale(1f, 1f, 1f);
		this.width = width;
		this.height = height;
		setColor(0f, 0f, 0f, 0f);
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		this.color = color;
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		this.color = new Color(r, g, b, a);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.setColor(this.color);
		this.shapeRenderer.rect(0f, 0f, this.width, this.height);
		this.shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
