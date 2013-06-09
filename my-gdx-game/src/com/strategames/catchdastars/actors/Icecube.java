package com.strategames.catchdastars.actors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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

	private static List<Part> availableParts;

	private ArrayList<Part> parts;
	private int partsSize;

	public Icecube() {
		Gdx.app.log("Icecube", "Icecube:");
		if( availableParts == null ) {
			setupAvailableParts();
		}

		if( loader == null ) {
			loader = new BodyEditorLoader(Gdx.files.internal("fixtures/icecube.json"));
		}

		this.parts = new ArrayList<Icecube.Part>();
	}

	public static Icecube create(World world, float x, float y) {
		Gdx.app.log("Icecube", "create:");
		Icecube icecube = new Icecube();
		icecube.setPosition(x, y);
		icecube.setWorld(world);
		icecube.setup();

		//Initial object contains all parts
		Iterator<Part> i = availableParts.iterator(); 
		while (i.hasNext())
			icecube.addPart(i.next());
		return icecube;
	}

	@Override
	TextureRegionDrawable createTexture() {
		Gdx.app.log("Icecube", "createTexture:");
		for(Part part : this.parts) {
			Sprite sprite = new Sprite(part.getTexture());
			sprite.setSize(Game.convertWorldToBox(sprite.getWidth()), Game.convertWorldToBox(sprite.getHeight()));
			part.setImage(sprite);
		}
		return new TextureRegionDrawable(Textures.icecube);
	}

	public void addPart(Part part) {
		Gdx.app.log("Icecube", "addPart: part="+part);
		this.parts.add(part);
		this.partsSize = this.parts.size();
		Gdx.app.log("Icecube", "addPart: this.partsSize="+this.partsSize);
	}

	public void removePart(Part part) {
		this.body.destroyFixture(part.getFixture());
		this.parts.remove(part);
		this.partsSize = this.parts.size();
	}

	@Override
	Body setupBox2D() {
		Gdx.app.log("Icecube", "setupBox2D:");
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
			Body tmpBody = world.createBody(bd);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
			fixtureDef.friction = 0.2f;
			fixtureDef.restitution = 0.01f; // Make it bounce a little bit

			loader.attachFixture(tmpBody, part.getName(), fixtureDef, WIDTH);

			part.setFixtureDef(fixtureDef);

			world.destroyBody(tmpBody);
		}

		for(Part part : this.parts ) {
			Fixture f = this.body.createFixture(part.getFixtureDef());
			part.setFixture(f);
		}

		return this.body;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		setRotation(MathUtils.radiansToDegrees * this.body.getAngle());
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);

		for(int i = 0; i < this.partsSize; i++) {
			Sprite sprite = this.parts.get(i).getImage();
			sprite.setPosition(v.x, v.y);
			sprite.draw(batch, parentAlpha);
		}
	}

	@Override
	public void write(Json json) {
		moveTo(getX(), getY()); // align body with image origin
		super.write(json);
	}

	@Override
	public GameObject createCopy() {
		Gdx.app.log("Icecube", "createCopy");
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
		json.writeValue("parts", "all");
	}

	@Override
	void readValue(String key, Object value) {
		if( key.contentEquals("parts") && 
				(value.toString()).contentEquals("all") ) {
			if( this.parts.size() < 1 ) {
				//Initial object contains all parts
				int size = availableParts.size();
				for(int i = 0; i < size; i++)
					addPart(availableParts.get(i));
			}
		}
	}


	private void setupAvailableParts() {
		availableParts = new ArrayList<Part>();
		availableParts.add(new Part("icecube-part1.png", Textures.icecubePart1));
		availableParts.add(new Part("icecube-part2.png", Textures.icecubePart2));
		availableParts.add(new Part("icecube-part3.png", Textures.icecubePart3));
		availableParts.add(new Part("icecube-part4.png", Textures.icecubePart4));
		availableParts.add(new Part("icecube-part5.png", Textures.icecubePart5));
		availableParts.add(new Part("icecube-part6.png", Textures.icecubePart6));
		availableParts.add(new Part("icecube-part7.png", Textures.icecubePart7));
		availableParts.add(new Part("icecube-part8.png", Textures.icecubePart8));
		availableParts.add(new Part("icecube-part9.png", Textures.icecubePart9));
		availableParts.add(new Part("icecube-part10.png", Textures.icecubePart10));
	}

	private class Part {
		String name;
		Sprite image;
		TextureRegion texture;
		Fixture fixture;
		FixtureDef fixtureDef;

		public Part(String name, TextureRegion texture) {
			this.name = name;
			this.texture = texture;
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

		public void setTexture(TextureRegion texture) {
			this.texture = texture;
		}

		public TextureRegion getTexture() {
			return texture;
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
