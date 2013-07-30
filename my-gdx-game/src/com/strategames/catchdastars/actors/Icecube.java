package com.strategames.catchdastars.actors;

import java.util.ArrayList;

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
import com.badlogic.gdx.physics.box2d.Shape;
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
 * TODO Now fixtures get recreated when object is split. 
 *      We might want to reuse fixtures of parent object.
 */
public class Icecube extends GameObject {
	private final static float WIDTH = Game.convertWorldToBox(32f); 

	private static int rocksHit;
	private static float rocksHitTotalImpulse;

	private static ArrayList<Part> availableParts;
	private static FixtureDef fixtureDef;
	private static boolean fixturesSetup;
	
	private ArrayList<Part> parts;
	private int amountOfParts;

	private Fixture breakOnFixture;

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
	public static float maxVelocitySquared = 90f * 90f;
	
	public Icecube() {
		super();

		if( availableParts == null ) {
			setupAvailableParts();
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
	TextureRegionDrawable createTexture() {
		for(Part part : this.parts) {
			Sprite sprite = new Sprite(part.texture);
			sprite.setSize(Game.convertWorldToBox(sprite.getWidth()), Game.convertWorldToBox(sprite.getHeight()));
			part.setSprite(sprite);
		}
		return new TextureRegionDrawable(Textures.icecube);
	}

	public void addPart(Part part) {
		this.parts.add(part);
		this.amountOfParts = this.parts.size();
	}

//	public boolean isBroken() {
//		return this.broken;
//	}
//	
//	public void setBroken(boolean broken) {
//		this.broken = broken;
//	}
	
	@Override
	Body setupBox2D() {
		World world = getWorld();
		
		if( ! fixturesSetup ) {
			setupFixtures(world);
		}
		
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.angle = getRotation() * MathUtils.degreesToRadians;
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.1f;
		Body body = world.createBody(bd);

		int size = this.parts.size();
		for(int i = 0; i < size; i++) {
			Part part = this.parts.get(i);
			int shapesSize = part.shapes.size();
			for(int j = 0; j < shapesSize; j++) {
				fixtureDef.shape = part.shapes.get(j);
				body.createFixture(fixtureDef);
			}
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
		for(int i = 0; i < this.amountOfParts; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.sprite;
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
	}

	@Override
	void readValue(String key, Object value) {

		int size = availableParts.size();
		for( int i = 0; i < size; i++ ) {
			addPart(availableParts.get(i));
		}
	}
	
	public static void playRocksHitSound() {
		if( ( rocksHit == 0 ) || ( rocksHitTotalImpulse == 0 ) ){
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
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		for(int i = 0; i < this.amountOfParts; i++) {
			Part part = this.parts.get(i);
			part.sprite.setColor(r, g, b, a);
		}
	}
	
	private void setupAvailableParts() {
		availableParts = new ArrayList<Icecube.Part>();
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
	
	private void setupFixtures(World world) {
		fixtureDef = new FixtureDef();
		fixtureDef.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.01f; // Make it bounce a little bit
		
		Gdx.app.log("Icecube", "Before getWorld: isLocked?"+world.isLocked());
		
		Body body = world.createBody(new BodyDef());
		Gdx.app.log("Icecube", "After getWorld");
		
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/icecube.json"));
		
		int size = availableParts.size();
		Gdx.app.log("Icecube", "size="+size);
		for(int i = 0; i < size; i++) {
			Part part = availableParts.get(i);
			ArrayList<Shape> shapes = loader.attachFixture(body, part.name, i, fixtureDef, WIDTH);
			part.setShapes(shapes);
			part.setOrigin(loader.getOrigin(part.name, WIDTH).cpy());
			
			Gdx.app.log("Icecube", "shapes.size()="+shapes.size());
		}
		
		world.destroyBody(body);

		fixturesSetup = true;
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

		// Create new object with piece that broke off
		Icecube icecube1 = new Icecube();
		icecube1.setPosition(v.x, v.y);
		icecube1.setRotation(getRotation());
		icecube1.addPart(availableParts.get(partId));
//		icecube1.setBroken(true);
		game.addGameObject(icecube1);

		// Create new object with the pieces that are left
		Icecube icecube2 = new Icecube();
		icecube2.setPosition(v.x, v.y);
		icecube2.setRotation(getRotation());
		int size = availableParts.size();
		for( int i = 0; i < size; i++ ) {
			if( i != partId ) {
				icecube2.addPart(availableParts.get(i));
			}
		}
//		icecube2.setBroken(true);
		game.addGameObject(icecube2);

		game.deleteGameObject(this);
	}

	private class Part {
		protected String name;
		protected Sprite sprite;
		protected TextureRegion texture;
		protected ArrayList<Shape> shapes;
		
		public Part(String name, TextureRegion texture) {
			this.name = name;
			this.texture = texture;
		}

		public void setSprite(Sprite sprite) {
			this.sprite = sprite;
		}

		public void setShapes(ArrayList<Shape> shapes) {
			this.shapes = shapes;
		}

		public void setOrigin(Vector2 origin) {
			if( sprite != null ) {
				sprite.setOrigin(origin.x, origin.y);
			}
		}
	}
}
