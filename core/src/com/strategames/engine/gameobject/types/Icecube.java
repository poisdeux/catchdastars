/**
 * 
 * Copyright 2013 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.gameobject.types;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.DynamicBody;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.sounds.RockBreakSound;
import com.strategames.engine.sounds.RockHitSound;
import com.strategames.engine.utils.BodyEditorLoader;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.Textures;

/**
 * @author martijn brekhof
 * TODO Now fixtures get recreated when object is split. 
 *      We might want to reuse fixtures of parent object.
 */
public class Icecube extends DynamicBody {
	private final static float WIDTH = 0.30f; 

	private ArrayList<Part> parts;
	private int amountOfParts;

	private boolean broken;

	private Part breakOfPart = null;

	private static BodyEditorLoader loader;

	private static int rocksHit;
	private static float rocksHitTotalImpulse;

	private static ArrayList<Part> availableParts;

	private static FixtureDef fixtureDef;

	private static RockBreakSound rockBreakSound = new RockBreakSound();
	private static RockHitSound rockHitSound = new RockHitSound();
	
	private static Textures textures = Textures.getInstance();

	public static float maximumImpulse = 75f;
	
	public Icecube() {
		super(new Vector2(WIDTH, -1f));

		this.parts = new ArrayList<Icecube.Part>();
	}

	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().icecube; 
	}

	public void addPart(Part part) {
		this.parts.add(part);
		this.amountOfParts = this.parts.size();
	}

	public void addAllParts() {
		if( availableParts == null ) {
			setupStaticResources();
		}
		for( Part part : availableParts ) {
			addPart(part);
		}
	}

	public ArrayList<Part> getParts() {
		return parts;
	}

	/**
	 * Returns the parts that are available for all pieces.
	 * <br/>
	 * Note that this is not the list of parts used by a piece.
	 * You can get the used parts using {@link #getParts()}
	 * @return arraylist containing the available parts to all pieces
	 */
	public static ArrayList<Part> getAvailableParts() {
		return availableParts;
	}

	public boolean isBroken() {
		return this.broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}
	
	public Part getBreakOfPart() {
		return breakOfPart;
	}

	@Override
	protected void setupBody(Body body) {
//		Gdx.app.debug("Icecube", "0");
		
		body.setTransform(body.getPosition(), getRotation() * MathUtils.degreesToRadians);

		int size = this.parts.size();
		for(int i = 0; i < size; i++) {
			Part part = this.parts.get(i);
			String name = part.getName();
			loader.attachFixture(body, name, i, fixtureDef);
		}
		
		body.setSleepingAllowed(false);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float x = getX();
		float y = getY();
		float rotation = getRotation();
		
		for(int i = 0; i < this.amountOfParts; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.getSprite();
			sprite.setPosition(x, y);
			sprite.setRotation(rotation);
			sprite.draw(batch, parentAlpha);
		}

		if( this.breakOfPart != null ) {
			Icecube icecube1 = new Icecube();
			Icecube icecube2 = new Icecube();
			splitObject(this.breakOfPart, icecube1, icecube2);
			GameEngine game = getGame();
			Stage stage = getStage();
			game.addGameObject(icecube1, stage);
			game.addGameObject(icecube2, stage);
			
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

		for( Part part : this.parts ) {
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
		setCanBeRemoved(true);
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject) {
		float maxImpulse = 0.0f;

		float[] impulses = impulse.getNormalImpulses();
		
		maxImpulse = impulses[0] > maximumImpulse ? maximumImpulse : impulses[0];
		
		if( maxImpulse > 20 ) { // prevent counting rocks hitting when they are lying on top of eachother
			rocksHit++;
			rocksHitTotalImpulse += maxImpulse;
			
			if( ( maxImpulse > 50 ) && ( this.amountOfParts > 1 ) ) { // break object
				
				if( this.breakOfPart == null ) {

					Body body = getBody();
					//Get colliding fixture for this object
					Fixture fixture = contact.getFixtureA();
					if( fixture.getBody() != body ) {
						fixture = contact.getFixtureB();
					}

					/**
					 * fixtures get reused and sometimes seem to be available in the wrong object
					 * (fixtures from an old object which has not yet been removed)
					 */
					if( body.getFixtureList().contains(fixture, true) ) {
						Integer userData = (Integer) fixture.getUserData();
						if( userData != null ) {
							try {
								this.breakOfPart = this.parts.get(userData.intValue());
							} catch ( IndexOutOfBoundsException e ) {
								Gdx.app.log("Icecube", "handleCollision: array out of bounds: this="+this+"fixture="+fixture);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void writeValues(Json json) {
	}

	@Override
	protected void readValue(JsonValue jsonData) {
	}

	@Override
	public void loadSounds() {
		rockHitSound.load();
		rockBreakSound.load();
	}
	
	public static void playRocksHitSound() {
		if( rocksHit == 0 ) {
			return;
		}

		float volume = rocksHitTotalImpulse / (maximumImpulse * rocksHit);
//		Gdx.app.log("Icecube", "playRocksHitSound: rocksHit="+rocksHit+
//				", rocksHitTotalImpulse="+rocksHitTotalImpulse+
//				", volume="+volume);
		
		if( rocksHit > 2 ) {
			rockHitSound.play(volume);
			rockBreakSound.play(volume);
		} else {
			rockHitSound.play(volume);
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
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		for(int i = 0; i < this.amountOfParts; i++) {
			Part part = this.parts.get(i);
			Sprite sprite = part.getSprite();
			sprite.setColor(color);
		}
	}

	/**
	 * Splits object in two and deletes this gameobject from game. 
	 * <br/>
	 * The parts will be added to icecube1 and icecube2 arguments. The method assumes
	 * icecube1 and icecube2 are new instances of Icecube.
	 * @param part that should be broken off
	 * @param icecube1 gameobject that will get part broken off
	 * @param icecube2 gameobject that will get all remaining parts
	 */
	public void splitObject(Part part, Icecube icecube1, Icecube icecube2) {
		GameEngine game = getGame();

		Vector2 v = super.body.getPosition();
		float rotation = getRotation();

		// Create new object with piece that broke off
		icecube1.setPosition(v.x, v.y);
		icecube1.setRotation(rotation);
		icecube1.addPart(part);
		icecube1.setBroken(true);

		// Create new object with the pieces that are left
		icecube2.setPosition(v.x, v.y);
		icecube2.setRotation(rotation);
		for( Part lPart : this.parts ) {
			if( lPart != part ) {
				icecube2.addPart(lPart);
			}
		}
		icecube2.setBroken(true);
		
		setCanBeRemoved(true);
		game.deleteGameObject(this);
	}

	public class Part {
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
	}

	@Override
	public void applyForce() {
	}
}
