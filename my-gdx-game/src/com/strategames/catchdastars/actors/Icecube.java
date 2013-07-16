package com.strategames.catchdastars.actors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
import com.strategames.catchdastars.utils.BodyEditorLoader;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

/**
 * @author martijn brekhof
 *
 */
public class Icecube extends GameObject {
	private final static float WIDTH = Game.convertWorldToBox(32f); 
	private static BodyEditorLoader loader;

	private static int rocksHit;
	private static float rocksHitTotalImpulse;

	private static HashMap<String, Part> availableParts;

	private ArrayList<Part> parts;
	private int partsSize;

	private Fixture breakOnFixture;

	private static float maxVolume = 1f;
	
	private static long prevPlayRocksRolling;
	
	/**
	 * New velocity is calculated as follows by Box2D
	 * 
	 * velocity += Game.UPDATE_FREQUENCY_SECONDS * (Game.GRAVITY + ((1f/this.balloon.getMass()) * (this.upwardLift * Game.GRAVITY)));
	 * velocity *= 1.0f - (Game.UPDATE_FREQUENCY_SECONDS * bd.linearDamping);
	 * 
	 * Where bd.linearDamping is set in setupBox2D()
	 * Following value of 28.77593 was determined empirically by checking maximum speed of icecube
	 * in game
	 */
	public static float maxVelocitySquared = 90f * 90f * (1/maxVolume);

	public Icecube() {
		super();

		if( availableParts == null ) {
			setupAvailableParts();
		}

		if( loader == null ) {
			loader = new BodyEditorLoader(Gdx.files.internal("fixtures/icecube.json"));
		}

		this.parts = new ArrayList<Icecube.Part>();
	}

	public Icecube(Game game, float x, float y) {
		this();
		setPosition(x, y);
		setGame(game);
		setup();

		//Initial object contains all parts
		Collection<Part> parts = availableParts.values();
		for( Part part : parts ) {
			addPart(part);
		}
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

		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.angle = getRotation() * MathUtils.degreesToRadians;
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.1f;
		Body body = world.createBody(bd);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.01f; // Make it bounce a little bit

		for(Part part : this.parts ) {
			loader.attachFixture(body, part.getName(), fixtureDef, WIDTH);
			part.setOrigin(loader.getOrigin(part.getName(), WIDTH).cpy());
		}

		body.setSleepingAllowed(false);

		return body;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float rotation = MathUtils.radiansToDegrees * this.body.getAngle();
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
		setRotation(rotation);
		for(int i = 0; i < this.partsSize; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.getSprite();
			sprite.setPosition(v.x, v.y);
			sprite.setRotation(rotation);
			sprite.draw(batch, parentAlpha);
		}

		if( this.breakOnFixture != null ) {
			splitObject();
		}
	}

	@Override
	public void write(Json json) {
		moveTo(getX(), getY()); // align body with image origin
		super.write(json);
	}

	@Override
	public GameObject createCopy() {
		GameObject object = new Icecube(getGame(), 
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
		int size = impulse.getCount();

		for (int i = 0; i < size; ++i)
		{
			if(impulses[i] > maxImpulse) {
				maxImpulse = impulses[i];
			}
		}

		//		Gdx.app.log("Icecube", "handleCollisiong: maxVelocitySquared="+this.maxVelocitySquared+", maxImpulse="+maxImpulse);
		if( maxImpulse > 500 ) { // prevent adding rocks hitting when they are lying on top of eachother
			//			game.rockHit(maxImpulse);
			if( maxImpulse > 1000 ) { // break object
				//Get colliding fixture for this object
				if(((GameObject) contact.getFixtureA().getBody().getUserData()) == this) {
					this.breakOnFixture = contact.getFixtureA();
				} else {
					this.breakOnFixture = contact.getFixtureB();
				}
				Icecube.rocksHit++;
			}
			Icecube.rocksHit++;
			Icecube.rocksHitTotalImpulse += maxImpulse;
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
				Collection<Part> parts = availableParts.values();
				for( Part part : parts ) {
					addPart(part);
				}
			}
		}
	}
	
	public static void playRocksHitSound() {
		if( rocksHit == 0 ) {
			return;
		}
		
		long epoch = System.currentTimeMillis();
		if( ( prevPlayRocksRolling + 300 ) > epoch ) { //prevent playing sound too fast
			return;
		}
		prevPlayRocksRolling = epoch;

		float volume = rocksHitTotalImpulse / (maxVelocitySquared * rocksHit);
		if( rocksHit > 10 ) {
			Sounds.rockHit.play(volume);
			Sounds.rockBreak.play(volume);
		} else if( rocksHit > 2 ) {
			Sounds.rockHit.play(volume);
			Sounds.rockBreak.play(volume);
		} else {
			Sounds.rockHit.play(volume);
		}
		
		rocksHit = 0;
		rocksHitTotalImpulse = 0;
	}
	
	private void setupAvailableParts() {
		availableParts = new HashMap<String, Icecube.Part>();
		availableParts.put("icecube-part1.png", new Part("icecube-part1.png", Textures.icecubePart1));
		availableParts.put("icecube-part2.png", new Part("icecube-part2.png", Textures.icecubePart2));
		availableParts.put("icecube-part3.png", new Part("icecube-part3.png", Textures.icecubePart3));
		availableParts.put("icecube-part4.png", new Part("icecube-part4.png", Textures.icecubePart4));
		availableParts.put("icecube-part5.png", new Part("icecube-part5.png", Textures.icecubePart5));
		availableParts.put("icecube-part6.png", new Part("icecube-part6.png", Textures.icecubePart6));
		availableParts.put("icecube-part7.png", new Part("icecube-part7.png", Textures.icecubePart7));
		availableParts.put("icecube-part8.png", new Part("icecube-part8.png", Textures.icecubePart8));
		availableParts.put("icecube-part9.png", new Part("icecube-part9.png", Textures.icecubePart9));
		availableParts.put("icecube-part10.png", new Part("icecube-part10.png", Textures.icecubePart10));
	}

	private void splitObject() {
		// Do not break if object consists of a single part
		if( this.partsSize <= 1 ) {
			return;
		}

		String partName = (String) breakOnFixture.getUserData();
		if( partName == null ) {
			Gdx.app.log("Icecube", "splitObject: breakOnFixture="+breakOnFixture);
			return;
		}

		Vector2 v = super.body.getPosition();

		Game game = getGame();

		// Create new object with piece that broke off
		Icecube icecube1 = new Icecube();
		icecube1.setPosition(v.x, v.y);
		icecube1.setRotation(getRotation());
		icecube1.addPart(availableParts.get(partName));
		game.addGameObject(icecube1);

		// Create new object with pieces that are left
		Icecube icecube2 = new Icecube();
		icecube2.setPosition(v.x, v.y);
		icecube2.setRotation(getRotation());
		//TODO using string comparison is VERY expensive. We need to redesign this to use integers instead
		for( Part part : this.parts ) {
			if( ! part.getName().contentEquals(partName) ) {
				icecube2.addPart(part);
			}
		}
		game.addGameObject(icecube2);

		game.deleteGameObject(this);
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
