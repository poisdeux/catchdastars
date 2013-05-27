package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.strategames.catchdastars.utils.ConfigurationItem;

public class Icecube extends GameObject {
	private Body body;
	
	private float maxVolume = 0.2f;
	/**
	 * Box2D limits the acceleration per time step to 2 m/s.
	 * Therefore the maximum speed any object can obtain is
	 * maxSpeed = worldTimeStep * 2
	 */
	private final float maxVelocitySquared = 8100f * (1/maxVolume); // (45 * 2) ^ 2  * maxVolume
	

	public Icecube() { }

	public static Icecube create(World world, float x, float y) {
		Icecube balloon = new Icecube();
		balloon.setPosition(x, y);
		balloon.setWorld(world);
		balloon.setup();
		return balloon;
	}
	
	@Override
	TextureRegionDrawable createTexture() {
//		trd = new TextureRegionDrawable(Textures.icecube);
//		return trd;
		return null;
	}

	@Override
	Body setupBox2D() {
		World world = getWorld();
		float width = getPrefWidth() * getScaleX();

		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("fixtures/balloon.json"));

		//Balloon body
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 0.8f;
		this.body = world.createBody(bd);
		
		FixtureDef fixture = new FixtureDef();
		fixture.density = 931f;  // Ice density 0.931 g/cm3 == 931 kg/m3
		fixture.friction = 0.2f;
		fixture.restitution = 0.6f; // Make it bounce a little bit
		loader.attachFixture(this.body, "Icecube_part1", fixture, width);
		
		return this.body;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		setRotation(MathUtils.radiansToDegrees * this.body.getAngle());
		Vector2 v = super.body.getPosition();
		setPosition(v.x, v.y);
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
		return Type.BALLOON;
	}

	@Override
	public void handleCollision(Contact contact, GameObject gameObject) {
		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 normal = worldManifold.getNormal();
		float bounceVelocity = this.body.getLinearVelocity().mul(normal.x, normal.y).len2();
		if( bounceVelocity > 100 ) {
			//Sounds.rockHit.play(bounceVelocity / this.maxVelocitySquared);
		}
	}

	@Override
	void writeValues(Json json) {
		
	}

	@Override
	void readValue(String key, Object value) {
		
	}
}
