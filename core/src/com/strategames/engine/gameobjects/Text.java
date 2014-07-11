package com.strategames.gameobjects;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Text extends Image {

	private static BitmapFont font;
	private static ShaderProgram fontShader;

	private String text;

	public Text(String text) {
		super();

		synchronized (this) {
			if( font == null ) {
				Texture texture = new Texture(Gdx.files.internal("fonts/vSHandprinted_distancefield.png"), true);
				texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
				font = new BitmapFont(Gdx.files.internal("fonts/vSHandprinted_distancefield.fnt"), new TextureRegion(texture), false);
			}

			if( fontShader == null ) {
				fontShader = new ShaderProgram(Gdx.files.internal("fonts/font.vert"), Gdx.files.internal("fonts/font.frag"));
				if (!fontShader.isCompiled()) {
					Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
				}
			}
		}

		this.text = text;

		updateActorSize();
	}

	@Override
	public void scaleBy(float scale) {
		super.scaleBy(scale);
		font.scale(scale);
		updateActorSize();
	}

	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		font.setScale(scale);
		updateActorSize();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setShader(fontShader);
		font.draw(batch, text, getX(), getY());
		batch.setShader(null);
	}

	private void updateActorSize() {
		// Make sure actor's size is updated properly
		TextBounds bounds = font.getBounds(text);
		setWidth(bounds.width);
		setHeight(bounds.height);
	}
}
