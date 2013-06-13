package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ConfigurationItem;

abstract public class GameObject extends Image implements Json.Serializable {
	private World world;
	protected Body body;
	private ArrayList<ConfigurationItem> configurationItems;
	protected float halfWidth;
	protected float halfHeight;
	private ShapeRenderer shapeRenderer;
	protected boolean isDeleted;
	protected boolean isHit;
	protected boolean isCollectible;
	
	protected Game game;
	
	public static enum Type {
		WALL, BALLOON, STAR, ROCK
	}
	
	public Type type;
	
	public GameObject() {
		init();
	}
	
	public GameObject(Game game) {
		this.game = game;
		this.world = game.getWorld();
		init();
	}

	public GameObject(Drawable trd) {
		super(trd, Scaling.none);
		init();
		this.configurationItems = createConfigurationItems();
	}

	private void init() {
		setName(getClass().getSimpleName());
		this.shapeRenderer = new ShapeRenderer();
		this.type = setType();
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setGame(Game game) {
		this.game = game;
		if( game != null ) {
			this.world = game.getWorld();
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public float getHalfHeight() {
		return halfHeight;
	}
	
	public float getHalfWidth() {
		return halfWidth;
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.halfHeight = height/2f;
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.halfWidth = width/2f;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return this.world;
	}

	public void setCollectible(boolean isCollectible) {
		this.isCollectible = isCollectible;
	}
	
	/**
	 * Deletes the Box2D body. This can only be used when {@link World#step(float, int, int)} is
	 * not running.
	 * @return
	 */
	public boolean deleteBody() {
		if(this.world.isLocked()) {
			return false;
		}
		
		if( this.body != null ) {
			this.world.destroyBody(this.body);
			this.body = null;
		}
		return true;
	}
	
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public void setBody(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}

	public ArrayList<ConfigurationItem> getConfigurationItems() {
		return this.configurationItems;
	}
	
	/**
	 * Setup the image and body for this game object. 
	 * <br/>
	 * This will add the GameObject as user data to the Box2D body. This can be retrieved using body.getUserData().
	 * @param world Box2D world that should hold the body. If null only image will be set and no body will be created.
	 */
	public void setup() {
		TextureRegionDrawable trd = createTexture();
		if( trd != null ) {
			setDrawable(trd);
			setScaling(Scaling.stretch);
			float width = Game.convertWorldToBox(trd.getRegion().getRegionWidth());
			float height = Game.convertWorldToBox(trd.getRegion().getRegionHeight());
			setWidth(width);
			setHeight(height);
		}

		if( this.world != null ) {
			this.body = setupBox2D();
			this.body.setUserData(this);
		}
		
		this.isDeleted = false;
		this.isHit = false;
	}

	public void moveTo(float x, float y) {
		if( this.body != null ) {
//			Vector2 v = this.body.getLocalCenter(); // make sure we position object on center of mass
			this.body.setTransform(x, y, this.body.getAngle());
			setPosition(x, y);
		} else {
			setPosition(x, y);
		}
	}


	public void drawBoundingBox(SpriteBatch batch) {
		batch.end();
		this.shapeRenderer.begin(ShapeType.Line);
		this.shapeRenderer.setColor(1f, 1f, 1f, 0.5f);
		this.shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
		this.shapeRenderer.end();
		batch.begin();
	}
	
	/**
	 * Called to create the image for the game object
	 */
	abstract TextureRegionDrawable createTexture();

	/**
	 * Called after {@link #createTexture()} to create the Box2D body of the game object.
	 * @return the created body
	 */
	abstract Body setupBox2D();

	/**
	 * Use this to write specific object properties to file(s)
	 * Note that generic properties are saved by GameObject abstract class
	 * and you do not need to write them using {@link #writeValues(Json)}
	 * <br>
	 * Example
	 * <pre>
	 * json.writeValue("type", type.name());
	 * json.writeValue("lift", getLift());
	 * </pre>
	 * @param json
	 */
	abstract void writeValues(Json json);

	@Override
	public void write(Json json) {
		json.writeObjectStart(this.getClass().getSimpleName());
		
		Vector2 position = new Vector2();
		if( this.body != null ) {
			position = this.body.getPosition();
		} else {
			position.x = getX();
			position.y = getY();
		}
		json.writeValue("x", position.x);
		json.writeValue("y", position.y);
		
		writeValues(json);
		json.writeObjectEnd();
	}

	/**
	 * Use this to read specific object properties read from file(s) you
	 * saved using {@link #writeValues(Json)}
	 * <br>
	 * Example
	 * <pre>
	 * if( key.contentEquals("type")) {
	 *   type = Type.valueOf(value.toString());
	 * }
	 * @param key	String holding the name you used in json.writeValue(String name, Object value) 
	 * @param value	The object you set using json.writeValue(String name, Object value)
	 */
	abstract void readValue(String key, Object value);

	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, OrderedMap<String, Object> jsonData) {
		Entries<String, Object> entries = jsonData.entries();

		while(entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			if ( entry.value instanceof OrderedMap ) {
				read(json, (OrderedMap<String, Object>) entry.value);
			} else if ( entry.key.contentEquals("x")) {
				setX(Float.valueOf(entry.value.toString()));
			} else if ( entry.key.contentEquals("y")) {
				setY(Float.valueOf(entry.value.toString()));
			} else {
				readValue(entry.key, entry.value);
			}
		}
	}

	abstract public GameObject createCopy();

	public void initializeConfigurationItems() {
		this.configurationItems = createConfigurationItems();
	}

	/**
	 * Called when game objected is created to set the configuration items for
	 * this game object
	 * @return HashMap<String, Float> the key should hold the name of the configuration item and the value the default value
	 */
	abstract protected ArrayList<ConfigurationItem> createConfigurationItems();


	/**
	 * Should increase the size of the game object one step
	 */
	abstract public void increaseSize();

	/**
	 * Should decrease the size of the game object one step
	 */
	abstract public void decreaseSize();
	
	/**
	 * Called when object must be removed from game
	 * <br/>
	 * This should start any remove animation and set object to deleted afterwards using {@link #setDeleted(boolean)}
	 */
	abstract public void destroy();
	
	/**
	 * Depending on the game engine this gets called when object collides with another object
	 * @param gameObject object that collided
	 */
	abstract public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject);

	
	/**
	 * Called when object is created and should return the {@linkplain #type} of this object
	 * @return Type the generic game object type 
	 */
	abstract protected Type setType();
	
	@Override
	public String toString() {
		String message = super.toString();
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message);
		messageBuffer.append(", halfWidth="+this.halfWidth);
		messageBuffer.append(", halfHeight="+this.halfHeight);
		return messageBuffer.toString();
	}
}
