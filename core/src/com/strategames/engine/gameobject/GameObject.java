package com.strategames.engine.gameobject;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
 *  TODO: create subclasses DynamicObject and StaticObject replacing createBody with createFixture 
 *  and applyForce and handleCollision only available in DynamicObject
 * @author martijn brekhof
 *
 */
abstract public class GameObject extends Image implements Json.Serializable {
	protected Body body;
	private ArrayList<ConfigurationItem> configurationItems;
	protected float halfWidth;
	protected float halfHeight;
	private ShapeRenderer shapeRenderer;
	protected boolean canBeRemoved;
	protected boolean isHit;
	protected boolean isCollectible;

	protected Vector2 initialPosition = new Vector2(); 

	private boolean isMenuItem;

	private Game game;

	protected Vector2 size = new Vector2();

	private boolean saveToFile = true;

	private TextureRegion textureRegion;

	private boolean inGame;
	
	/**
	 * Constructor for creating a game object
	 * @param size in meters. size.x = width, size.y = height. 
	 * <br/>
	 * If size.y < 0 height of the game object is calculated using size.x and image size when {@link #setup()} is called. 
	 */
	public GameObject(Vector2 size) {
		this.size = size;
		if( size != null ) {
			setWidth(this.size.x);
			setHeight(this.size.y);
		}
		setName(getClass().getSimpleName());
	}

	protected GameObject() {
		this(new Vector2(0.3f, -1));
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
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

	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public boolean canBeRemoved() {
		return this.canBeRemoved;
	}

	/**
	 * Use this to mark this object for deletion.
	 * @param delete
	 */
	synchronized public void setCanBeRemoved(boolean remove) {
		this.canBeRemoved = remove;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	public boolean isInGame() {
		return inGame;
	}
	
	public ArrayList<ConfigurationItem> getConfigurationItems() {
		return this.configurationItems;
	}

	public boolean isHit() {
		return this.isHit;
	}

	public void setHit(boolean hit) {
		this.isHit = hit;
	}

	/**
	 * Setup the image and body for this game object.
	 * <br/>
	 * Note: if you want to change the objects configuration (e.g. position, size, ...). Make sure
	 * you do it BEFORE calling setup
	 * <br/>
	 * This will add the GameObject as user data to the Box2D body. This can be retrieved using body.getUserData().
	 * TODO replace setup method with a builder pattern create method
	 */
	public void setupImage() {
		this.textureRegion = createImage();
		//		Gdx.app.debug("GameObject", "setup: gameObject="+this+", trd="+trd);
		if( this.textureRegion != null ) {
			setDrawable(new TextureRegionDrawable(this.textureRegion));
			setScaling(Scaling.stretch);
			if( this.size.y < 0 ) {
				double aspectRatio = this.textureRegion.getRegionHeight() / (double) this.textureRegion.getRegionWidth();
				this.size.y = (float) (this.size.x * aspectRatio);
				setHeight(this.size.y);
			}
		}
	}

	public void setupBody() {

		if( this.game != null ) {
			World world = this.game.getWorld();
			if ( world != null )  {
				this.body = createBody(world);
				if( this.body != null ) {
					this.body.setUserData(this);
				}
			} else {
				Gdx.app.log("GameObject", "setupBody: world is null for "+getName());
			}
		} else {
			Gdx.app.log("GameObject", "setupBody: game is null for "+getName());
		}
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

	public void moveX(float x) {
		if( this.body != null ) {
			this.body.setTransform(x, getY(), this.body.getAngle());
		} 
		setX(x);
	}

	public void moveY(float y) {
		if( this.body != null ) {
			this.body.setTransform(getX(), y, this.body.getAngle());
		} 
		setY(y);
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

	@Override
	public String toString() {
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(System.identityHashCode(this) + " ");
		messageBuffer.append(super.toString());
		messageBuffer.append(", position=("+getX()+","+getY()+")");
		messageBuffer.append(", halfWidth="+this.halfWidth);
		messageBuffer.append(", halfHeight="+this.halfHeight);
		messageBuffer.append(", saveToFile="+this.saveToFile);
		return messageBuffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		GameObject object;
		if( obj instanceof GameObject ) {
			object = (GameObject) obj;
		} else {
			return false;
		}
		
		if( ( body != null ) && (! body.equals(object.getBody())) ) {
			return false;
		} else if( object.getBody() != null ) {
			return false;
		}
		
		return (
				(getX() == object.getX()) &&
				(getY() == object.getY()) &&
				(getWidth() == object.getWidth()) &&
				(getHeight() == object.getHeight()) &&
				(getRotation() == object.getRotation()) &&
				(isCollectible == object.isCollectible) &&
				(halfWidth == object.getHalfWidth()) &&
				(halfHeight == object.getHalfHeight())
				);
	}
	/**
	 * Called to create the image for the game object
	 */
	abstract protected TextureRegion createImage();

	/**
	 * Called after {@link #createTextureRegionDrawable()} to create the Box2D body of the game object.
	 * @return the created body
	 */
	abstract protected Body createBody(World world);

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
	 * Use this to remove object from game during gameplay. It starts the {@link #destroyAction()}
	 * and sets {@link #isHit} to true. 
	 * <br/>
	 * Note that is {@link #isHit} is true {@link #destroyAction()} will not be called.
	 */
	synchronized public void startRemoveAnimation() {
		if( this.isHit ) { //prevent object from being destroyed multiple times during a removal animation
			return;
		}
		this.isHit = true;
		destroyAction();
	}

	/**
	 * Called by {@link #startRemoveAnimation()} to start any animation or sound when object is destroyed
	 * <br/>
	 * Be sure to call {@link #setCanBeRemoved(boolean)} and set it to true when object can
	 * safely be removed from game. Otherwise object will not be removed.
	 */
	abstract protected void destroyAction();

	/**
	 * Depending on the game engine this gets called when object collides with another object
	 * @param gameObject object that collided
	 */
	abstract public void handleCollision(Contact contact, ContactImpulse impulse, GameObject gameObject);

	/**
	 * Soundeffects get disposed when screen closes. {@link Game} will call this method
	 * to load sounds when starting a level. Make sure you load all sounds needed here.
	 */
	abstract public void loadSounds();

//	/**
//	 * Called prior to updating the physics world (Box2D) so you can
//	 * apply forces to the gameobject.
//	 */
//	abstract public void applyForce();
}
