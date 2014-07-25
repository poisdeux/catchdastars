package com.strategames.engine.gameobjects;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.BodyEditorLoader;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.Mutex;
import com.strategames.engine.utils.Sounds;
import com.strategames.engine.utils.Textures;

/**
 * @author martijn brekhof
 * TODO Now fixtures get recreated when object is split. 
 *      We might want to reuse fixtures of parent object.
 */
public class Icecube extends GameObject {
	private final static float WIDTH = 0.30f; 


	private ArrayList<Part> parts;
	private int amountOfParts;

	private boolean broken;

	private Fixture breakOnFixture;

	private Color colorActor;

	private static Mutex mutex = new Mutex();

	private static BodyEditorLoader loader;

	private static int rocksHit;
	private static float rocksHitTotalImpulse;

	private static ArrayList<Part> availableParts;

	private static FixtureDef fixtureDef;

	private static BodyDef bodyDef;

	private static TextureRegionDrawable drawable;

	private static long prevPlayRocksRolling;

	private static Sounds sounds = Sounds.getInstance();
	
	private static Textures textures = Textures.getInstance();
	
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
	public static float maxVelocitySquared = 828.05414f;

	public Icecube() {
		super(new Vector2(WIDTH, -1f));

		try {
			mutex.getLockWait();
			if( availableParts == null ) {
				setupStaticResources();
			}
			mutex.releaseLock();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		this.parts = new ArrayList<Icecube.Part>();
	}

	public Icecube(Game game, float x, float y) {
		this();
		setPosition(x, y);
		setGame(game);
		setup();

		//		//Initial object contains all parts
		//		for( Part part : availableParts ) {
		//			addPart(part);
		//		}
	}

	@Override
	protected TextureRegionDrawable createTexture() {
		this.colorActor = getColor();

		return drawable; 
	}

	public void addPart(Part part) {
		this.parts.add(part);
		this.amountOfParts = this.parts.size();
	}

	public boolean isBroken() {
		return this.broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	@Override
	protected Body setupBox2D() {

		World world = getGame().getWorld();

		bodyDef.position.set(getX(), getY());
		bodyDef.angle = getRotation() * MathUtils.degreesToRadians;

		Body body = world.createBody(bodyDef);

		int size = this.parts.size();
		for(int i = 0; i < size; i++) {
			Part part = this.parts.get(i);
			String name = part.getName();
			loader.attachFixture(body, name, i, fixtureDef);
		}

		body.setSleepingAllowed(false);


		return body;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float rotation = MathUtils.radiansToDegrees * this.body.getAngle();
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
		setRotation(rotation);

		for(int i = 0; i < this.amountOfParts; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.getSprite();
			sprite.setPosition(v.x, v.y);
			sprite.setRotation(rotation);
			sprite.draw(batch, this.colorActor.a);
		}

		if( this.breakOnFixture != null ) {
			splitObject();
		}

		//		drawBoundingBox(batch);
	}

	@Override
	public void write(Json json) {
		moveTo(getX(), getY()); // align body with image origin
		super.write(json);
	}

	@Override
	public GameObject copy() {
		Icecube cube = (Icecube) newInstance();
		cube.setPosition(getX(), getY());
		cube.setGame(getGame());

		for( Part part : availableParts ) {
			cube.addPart(part);
		}

		return cube;
	}
	
	@Override
	protected GameObject newInstance() {
		return new Icecube();
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
	public void destroyAction() {
		setCanBeDeleted(true);
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

		if( maxImpulse > 100 ) { // prevent counting rocks hitting when they are lying on top of eachother
			//			game.rockHit(maxImpulse);
			if( maxImpulse > 300 ) { // break object
				//Get colliding fixture for this object
				if(((GameObject) contact.getFixtureA().getBody().getUserData()) == this) {
					this.breakOnFixture = contact.getFixtureA();
				} else {
					this.breakOnFixture = contact.getFixtureB();
				}
				rocksHit++;
			}
			rocksHit++;
			rocksHitTotalImpulse += maxImpulse;
		}
	}

	@Override
	protected void writeValues(Json json) {
		json.writeValue("parts", "all");
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		if( jsonData.name().contentEquals("parts") && 
				(jsonData.asString()).contentEquals("all") ) {
			//Initial object contains all parts
			for( Part part : availableParts ) {
				addPart(part);
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
		if( rocksHit > 2 ) {
			sounds.play(sounds.rockHit, volume);
			sounds.play(sounds.rockBreak, volume);
		} else {
			sounds.play(sounds.rockHit, volume);
		}

		rocksHit = 0;
		rocksHitTotalImpulse = 0;
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		for(int i = 0; i < this.amountOfParts; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.getSprite();
			sprite.setColor(r, g, b, a);
		}
	}



	private void splitObject() {
		// Do not break if object consists of a single part
		if( this.amountOfParts <= 1 ) {
			return;
		}

		Integer userData = (Integer) breakOnFixture.getUserData();
		if( userData == null ) {
			return;
		}

		int partId = userData.intValue();
		Vector2 v = super.body.getPosition();

		Game game = getGame();
		Level level = game.getLevel();
		
		// Create new object with piece that broke off
		Icecube icecube1 = new Icecube();
		icecube1.setPosition(v.x, v.y);
		icecube1.setRotation(getRotation());
		icecube1.addPart(this.parts.get(partId));
		icecube1.setBroken(true);
		icecube1.setGame(game);
		icecube1.setup();
		level.addGameObject(icecube1);

		// Create new object with the pieces that are left
		Icecube icecube2 = new Icecube();
		icecube2.setPosition(v.x, v.y);
		icecube2.setRotation(getRotation());
		int size = this.parts.size();
		for( int i = 0; i < size; i++ ) {
			if( i != partId ) {
				icecube2.addPart(this.parts.get(i));
			}
		}
		icecube2.setBroken(true);
		icecube2.setGame(game);
		icecube2.setup();
		level.addGameObject(icecube2);

		game.deleteGameObject(this);
	}

	private class Part {
		String name;
		Sprite sprite;

		public Part(String name, TextureRegion texture) {
			this.name = name;
			this.sprite = new Sprite(texture);
			this.sprite.setSize(WIDTH, WIDTH);
			setOrigin(new Vector2(0, 0));
		}

		public Sprite getSprite() {
			return sprite;
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

	private void setupStaticResources() {

		availableParts = new ArrayList<Icecube.Part>();
		availableParts.add(new Part("icecube-part01.png", textures.icecubePart1));
		availableParts.add(new Part("icecube-part02.png", textures.icecubePart2));
		availableParts.add(new Part("icecube-part03.png", textures.icecubePart3));
		availableParts.add(new Part("icecube-part04.png", textures.icecubePart4));
		availableParts.add(new Part("icecube-part05.png", textures.icecubePart5));
		availableParts.add(new Part("icecube-part06.png", textures.icecubePart6));
		availableParts.add(new Part("icecube-part07.png", textures.icecubePart7));
		availableParts.add(new Part("icecube-part08.png", textures.icecubePart8));
		availableParts.add(new Part("icecube-part09.png", textures.icecubePart9));
		availableParts.add(new Part("icecube-part10.png", textures.icecubePart10));

		loader = new BodyEditorLoader(Gdx.files.internal("fixtures/icecube.json"));
		int size = availableParts.size();
		for(int i = 0; i < size; i++) {
			Part part = availableParts.get(i);
			try {
				loader.setupVertices(part.name, WIDTH);
			} catch (RuntimeException e) {
				Gdx.app.log("Icecube", "BodyEditorLoader error: "+e.getMessage());
			}
		}


		fixtureDef = new FixtureDef();
		fixtureDef.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = 0.01f; // Make it bounce a little bit


		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.angularDamping = 0.1f;


		drawable = new TextureRegionDrawable(textures.icecube);
	}
}
