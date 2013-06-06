package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Textures;

public class Icecube extends GameObject {
	private Body body;

	private ArrayList<Sprite> parts;
	
	public Icecube() { 
		this.parts = new ArrayList<Sprite>();
	}

	public static Icecube create(World world, float x, float y) {
		Icecube icecube = new Icecube();
		icecube.setPosition(x, y);
		icecube.setWorld(world);
		icecube.setup();
		return icecube;
	}

	@Override
	TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.icecube);
	}

	@Override
	Body setupBox2D() {
		World world = getWorld();
		float width = Game.convertWorldToBox(32f);
		
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/icecube.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.1f;
		this.body = world.createBody(bd);

		FixtureDef fixture = new FixtureDef();
		fixture.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
		fixture.friction = 0.2f;
		fixture.restitution = 0.01f; // Make it bounce a little bit
		loader.attachFixture(this.body, "icecube-part1.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part2.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part3.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part4.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part5.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part6.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part7.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part8.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part9.png", fixture, width);
		loader.attachFixture(this.body, "icecube-part10.png", fixture, width);
		
		return this.body;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	@Override
	public void write(Json json) {
		moveTo(getX(), getY()); // align body with image origin
		super.write(json);
	}

	@Override
	public GameObject createCopy() {
		GameObject object = Icecube.create(getWorld(), 
				getX(), 
				getY());
		return object;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		return null;
	}

	@Override
	public void increaseSize() {

	}

	@Override
	public void decreaseSize() {

	}

	@Override
	public void destroy() {
		if(remove()) {
			setDeleted(true);
		}
	}

	@Override
	protected Type setType() {
		return Type.ROCK;
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		float maxImpulse = 0.0f;

		float[] impulses = impulse.getNormalImpulses();
		int size = impulses.length;

		for (int i = 0; i < size; ++i)
		{
			if(impulses[i] > maxImpulse) {
				maxImpulse = impulses[i];
			}
		}

		if (maxImpulse > 40.0f)
		{
			setBreakObject(true);
		}
	}

	@Override
	void writeValues(Json json) {

	}

	@Override
	void readValue(String key, Object value) {

	}
}
