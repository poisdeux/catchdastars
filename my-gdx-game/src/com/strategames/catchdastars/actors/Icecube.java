package com.strategames.catchdastars.actors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.BodyEditorLoader;
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
		Iterator<Part> i = availableParts.iterator(); 
		while (i.hasNext())
			icecube.addPart(i.next());
		return icecube;
	}

	@Override
	TextureRegionDrawable createTexture() {
		for(Part part : this.parts) {
			Sprite sprite = new Sprite(part.getTexture());
			sprite.setSize(Game.convertWorldToBox(sprite.getWidth()), Game.convertWorldToBox(sprite.getHeight()));
			part.setSprite(sprite);
		}
		return new TextureRegionDrawable(Textures.icecube);
	}

	public void addPart(Part part) {
		this.parts.add(part);
		this.partsSize = this.parts.size();
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

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.01f; // Make it bounce a little bit
		
		for(Part part : this.parts ) {
			loader.attachFixture(this.body, part.getName(), fixtureDef, WIDTH);
			part.setOrigin(loader.getOrigin(part.getName(), WIDTH).cpy());
		}

		return this.body;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float rotation = MathUtils.radiansToDegrees * this.body.getAngle();
		Vector2 v = super.body.getPosition();

		for(int i = 0; i < this.partsSize; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.getSprite();
			sprite.setPosition(v.x, v.y);
			sprite.setRotation(rotation);
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
		Sprite sprite;
		TextureRegion texture;
		
		public Part(String name, TextureRegion texture) {
			this.name = name;
			this.texture = texture;
		}

		public void setSprite(Sprite sprite) {
			this.sprite = sprite;
		}

		public Sprite getSprite() {
			return sprite;
		}

		public TextureRegion getTexture() {
			return texture;
		}

		public String getName() {
			return name;
		}
		
		public void setOrigin(Vector2 origin) {
			if( sprite != null ) {
				sprite.setOrigin(origin.x, origin.y);
			}
		}
	}
}
