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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Textures;

public class Icecube extends GameObject {
	private final static float WIDTH = Game.convertWorldToBox(32f); 
	private Body body;
	private static BodyEditorLoader loader;
	
	private static ArrayList<Part> availableParts;
	
	private ArrayList<Part> parts;
	
	public Icecube() { 
		if( availableParts == null ) {
			setupAvailableParts();
		}
		
		if( loader == null ) {
			loader = new BodyEditorLoader(Gdx.files.internal("fixtures/icecube.json"));
		}
		
		this.parts = new ArrayList<Icecube.Part>();
	}

	public static Icecube create(World world, float x, float y) {
		Icecube icecube = new Icecube();
		icecube.setPosition(x, y);
		icecube.setWorld(world);
		icecube.setup();
		
		//Initial object contains all parts
		for( Part part : availableParts ) {
			icecube.addPart(part);
		}
		return icecube;
	}

	@Override
	TextureRegionDrawable createTexture() {
		return new TextureRegionDrawable(Textures.icecube);
	}

	public void addPart(Part part) {
		this.parts.add(part);
	}
	
	public void removePart(Part part) {
		this.body.destroyFixture(part.getFixture());
		this.parts.remove(part);
	}
	
	@Override
	Body setupBox2D() {
		World world = getWorld();
				
		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.1f;
		this.body = world.createBody(bd);
		
		/**
		 * We need to do this in two steps as we use the BodyEditorLoader
		 * to attach the fixtures. We need the Fixture in the part object
		 * to remove it later on when the object breaks. However, the Fixture
		 * is not returned by the loader. We therefore need to recreate it
		 * using body.createFixture(FixtureDef) that will return the fixture.
		 */
		for(Part part : this.parts ) {
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
			fixtureDef.friction = 0.2f;
			fixtureDef.restitution = 0.01f; // Make it bounce a little bit
			
			loader.attachFixture(this.body, part.getName(), fixtureDef, WIDTH);
			ArrayList<Fixture> fixtures = this.body.getFixtureList();
			for( Fixture fixture : fixtures ) {
				this.body.destroyFixture(fixture);
			}
			
			part.setFixtureDef(fixtureDef);
		}
		
		for(Part part : this.parts ) {
			Fixture f = this.body.createFixture(part.getFixtureDef());
			part.setFixture(f);
		}
		
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
			String partName;
			
			//Get colliding fixture for this object
			if(((GameObject) contact.getFixtureA().getBody().getUserData()) == gameObject) {
				partName = (String) contact.getFixtureB().getUserData();
			} else {
				partName = (String) contact.getFixtureA().getUserData();
			}
			
		}
	}

	@Override
	void writeValues(Json json) {

	}

	@Override
	void readValue(String key, Object value) {

	}
	
	private void setupAvailableParts() {
		availableParts = new ArrayList<Part>();
		Part part = new Part("icecube-part1.png", new Sprite(Textures.icecubePart1));
		availableParts.add(part);
		part = new Part("icecube-part2.png", new Sprite(Textures.icecubePart2));
		availableParts.add(part);
		part = new Part("icecube-part3.png", new Sprite(Textures.icecubePart3));
		availableParts.add(part);
		part = new Part("icecube-part4.png", new Sprite(Textures.icecubePart4));
		availableParts.add(part);
		part = new Part("icecube-part5.png", new Sprite(Textures.icecubePart5));
		availableParts.add(part);
		part = new Part("icecube-part6.png", new Sprite(Textures.icecubePart6));
		availableParts.add(part);
		part = new Part("icecube-part7.png", new Sprite(Textures.icecubePart7));
		availableParts.add(part);
		part = new Part("icecube-part8.png", new Sprite(Textures.icecubePart8));
		availableParts.add(part);
		part = new Part("icecube-part9.png", new Sprite(Textures.icecubePart9));
		availableParts.add(part);
		part = new Part("icecube-part10.png", new Sprite(Textures.icecubePart10));
		availableParts.add(part);
	}
	
	private class Part {
		String name;
		Sprite image;
		Fixture fixture;
		FixtureDef fixtureDef;
		
		public Part(String name, Sprite image) {
			this.name = name;
			this.image = image;
		}
		
		public void setImage(Sprite image) {
			this.image = image;
		}
		
		public Sprite getImage() {
			return image;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setFixture(Fixture fixture) {
			this.fixture = fixture;
		}
		
		public Fixture getFixture() {
			return fixture;
		}
		
		public void setFixtureDef(FixtureDef fixtureDef) {
			this.fixtureDef = fixtureDef;
		}
		
		public FixtureDef getFixtureDef() {
			return fixtureDef;
		}
	}
}
