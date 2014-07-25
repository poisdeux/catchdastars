package com.strategames.engine.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.ConfigurationItem;

/**
 * GameObject assumes sprite origin is located at bottom left. If you extend this
 * class for an object where the sprite origin is somewhere else you need to override
 * the following methods
 * <br/> 
 * {@link #draw(SpriteBatch, float)}
 * <br/>
 * {@link #moveTo(float, float)}
 * <br/>
 * and make sure your images and bodies are aligned. You can check this by calling
 * {@link #drawBoundingBox(SpriteBatch)} in {@link #draw(SpriteBatch, float)}.
 * 
 * @author martijn brekhof
 *
 */
abstract public class GameObject extends Image implements Json.Serializable {
	protected Body body;
	private ArrayList<ConfigurationItem> configurationItems;
	protected float halfWidth;
	protected float halfHeight;
	private ShapeRenderer shapeRenderer;
	protected boolean canBeDeleted;
	protected boolean isHit;
	protected boolean isCollectible;

	protected Vector2 initialPosition; 

	private boolean isMenuItem;

	protected Game game;

	protected Vector2 size;

	private boolean saveToFile = true;

	public static enum Type {
		WALL, BALLOON, ROCK
	}

	public Type type;

	/**
	 * Constructor for creating a game object
	 * @param size in meters. size.x = width, size.y = height. If size.y < 0 height of the game object is calculated using size.x and image size. 
	 */
	public GameObject(Vector2 size) {
		this.size = size;
		init();
	}

	public GameObject(Game game) {
		setGame(game);
		init();
	}

	public GameObject(Drawable trd) {
		super(trd, Scaling.none);
		init();
	}

	private void init() {
		setName(getClass().getSimpleName());
		this.type = setType();
		this.initialPosition = new Vector2();
	}

	public Game getGame() {
		return game;
	}

	/**
	 * Sets the game and calls {@link #setup()} to initialize gameobject
	 * @param game holding the World object
	 */
	public void setGame(Game game) {
		this.game = game;
		if( game != null ) {
			setup();
		}
	}

	/**
	 * Set to false to prevent saving this game object
	 * to a level file
	 * @param save
	 */
	public void setSaveToFile(boolean save) {
		this.saveToFile = save;
	}

	public boolean getSaveToFile() {
		return this.saveToFile;
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

	/**
	 * Specify this gameobject as menu item
	 * @param isMenuItem true if object is part of a menu
	 */
	public void setMenuItem(boolean isMenuItem) {
		this.isMenuItem = isMenuItem;
	}

	public boolean isMenuItem() {
		return isMenuItem;
	}

	public Vector2 getInitialPosition() {
		return initialPosition;
	}

	public void setInitialPosition(Vector2 initialPosition) {
		this.initialPosition = initialPosition;
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
		World world = this.game.getWorld();
		if(world.isLocked()) {
			return false;
		}

		if( this.body != null ) {
			world.destroyBody(this.body);
			this.body = null;
		}
		return true;
	}

	/**
	 * Use this to mark this object for deletion.
	 * @param delete
	 */
	synchronized public void setCanBeDeleted(boolean delete) {
		this.canBeDeleted = delete;
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
	 * Note: if you want to change the objects configuration (e.g. position, size, ...). Make sure
	 * you do it BEFORE calling setup
	 * <br/>
	 * This will add the GameObject as user data to the Box2D body. This can be retrieved using body.getUserData().
	 */
	public void setup() {
		TextureRegionDrawable trd = createTexture();
//		Gdx.app.log("GameObject", "setup: gameObject="+this+", trd="+trd);
		if( trd != null ) {
			setDrawable(trd);
			setScaling(Scaling.stretch);
			if( this.size.y < 0 ) {
				double aspectRatio = trd.getRegion().getRegionHeight() / (double) trd.getRegion().getRegionWidth();
				this.size.y = (float) (this.size.x * aspectRatio);
			}
			setWidth(this.size.x);
			setHeight(this.size.y);
		}

//		Gdx.app.log("GameObject", "setup: gameObject="+this+", world="+this.world);
		if( ( this.game != null ) && ( this.game.getWorld() != null ) ) {
			this.body = setupBox2D();
			this.body.setUserData(this);
		}

		this.canBeDeleted = false;
		this.isHit = false;
	}

	/**
	 * Moves a gameobject to location x, y. Note that you should use
	 * {@link Actor#setPosition(float, float)} if you only want to change
	 * the position of the drawable used by this gameobject
	 * @param x
	 * @param y
	 */
	public void moveTo(float x, float y) {
		if( this.body != null ) {
			this.body.setTransform(x, y, this.body.getAngle());
		} 
		setPosition(x, y);
	}
	
	/**
	 * Returns the bounding rectangle for this game object.
	 * If you reposition or resize the game object you should again call this
	 * method to get the realigned bounding rectangle
	 * @return Rectangle
	 */
	public Rectangle getBoundingRectangle() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * Sets up the shapeRenderes used by {@link GameObject#drawBodyCenterMass(SpriteBatch, Color)}, 
	 * <br/>{@link GameObject#drawBodyPosition(SpriteBatch, Color)}, and {@link GameObject#drawBoundingBox(SpriteBatch)}
	 * <br/>You need to run this before using any of the above methods
	 */
	public void enableDebugMode() {
		this.shapeRenderer = new ShapeRenderer();
		this.shapeRenderer.scale(Game.BOX_TO_WORLD, Game.BOX_TO_WORLD, 1f);
	}
	
	public void drawBoundingBox(SpriteBatch batch) {
		if( this.shapeRenderer == null ) {
			Gdx.app.log("GameObject", "Run enableDebugMode() on gameobject before using drawBoundingBox(...)"); 
			return;
		}
		batch.end();
		this.shapeRenderer.begin(ShapeType.Line);
		this.shapeRenderer.setColor(1f, 1f, 1f, 0.5f);
		Rectangle rec = getBoundingRectangle();
		this.shapeRenderer.rect(rec.x, rec.y, rec.width, rec.height);
		this.shapeRenderer.end();
		batch.begin();
	}

	public void drawBodyCenterMass(SpriteBatch batch, Color color) {
		if( this.shapeRenderer == null ) {
			Gdx.app.log("GameObject", "Run enableDebugMode() on gameobject before using drawBodyCenterMass(...)");
			return;
		}
		batch.end();
		this.shapeRenderer.begin(ShapeType.Point);
		this.shapeRenderer.setColor(color);
		Vector2 v = this.body.getWorldCenter();
		this.shapeRenderer.point(v.x, v.y, 0f);
		this.shapeRenderer.end();
		batch.begin();
	}

	public void drawBodyPosition(SpriteBatch batch, Color color) {
		if( this.shapeRenderer == null ) {
			Gdx.app.log("GameObject", "Run enableDebugMode() on gameobject before using drawBodyPosition(...)");
			return;
		}
		batch.end();
		this.shapeRenderer.begin(ShapeType.Point);
		this.shapeRenderer.setColor(color);
		Vector2 v = this.body.getPosition();
		this.shapeRenderer.point(v.x, v.y, 0f);
		this.shapeRenderer.end();
		batch.begin();
	}

	/**
	 * Called to create the image for the game object
	 */
	abstract protected TextureRegionDrawable createTexture();

	/**
	 * Called after {@link #createTexture()} to create the Box2D body of the game object.
	 * @return the created body
	 */
	abstract protected Body setupBox2D();

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
	abstract protected void writeValues(Json json);

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
	abstract protected void readValue(JsonValue jsonData);

	@Override
	public void read(Json json, JsonValue jsonData) {
		for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
			for(JsonValue element = entry.child; element != null; element = element.next) {
				String name = element.name;
				if ( name.contentEquals("x")) {
					float value = element.asFloat();
					setX(value);
					this.initialPosition.x = value;
				} else if ( name.contentEquals("y")) {
					float value = element.asFloat();
					setY(value);
					this.initialPosition.y = value;
				} else {
					readValue(element);
				}
			}
		}
	}

	abstract public GameObject copy();
	
	/**
	 * Should create the most basic instance of this gameobject
	 * @return new instance of GameObject
	 */
	abstract protected GameObject newInstance();
	
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
	 * Note that this sets object can be deleted using {@link #setCanBeDeleted(boolean)}
	 */
	synchronized public void destroy() {
		if( this.isHit ) { //prevent object from being destroyed multiple times during a removal animation
			return;
		}
		this.isHit = true;
		destroyAction();
	}

	/**
	 * Called by {@link #destroy()} to start any animation or sound when object is destroyed
	 * <br/>
	 * Be sure to call {@link #setCanBeDeleted(boolean)} and set it to true when object can
	 * safely be removed from game. Otherwise object will not be removed.
	 */
	abstract protected void destroyAction();
	
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
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(System.identityHashCode(this) + " ");
		messageBuffer.append(super.toString());
		messageBuffer.append(", position=("+getX()+","+getY()+")");
		messageBuffer.append(", halfWidth="+this.halfWidth);
		messageBuffer.append(", halfHeight="+this.halfHeight);
		return messageBuffer.toString();
	}
}
