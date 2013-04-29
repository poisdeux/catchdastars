package com.strategames.catchdastars.actors;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.utils.ConfigurationItem;

abstract public class GameObject extends Image implements Json.Serializable {
	private String name;
	private World world;
	private Body body;
	private ArrayList<ConfigurationItem> configurationItems;
	protected float halfWidth;
	protected float halfHeight;
	private ShapeRenderer shapeRenderer;

	public GameObject() {
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
	}
	
	public float getHalfHeight() {
		return halfHeight;
	}
	
	public float getHalfWidth() {
		return halfWidth;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return this.world;
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
	 * Setup the image and body of this game object. 
	 * @param world Box2D world that should hold the body. If null only image will be set and no body will be created.
	 */
	public void setup() {
		TextureRegionDrawable trd = createTexture();
		if( trd != null ) {
			setDrawable(trd);
			setScaling(Scaling.none);
			setWidth(trd.getRegion().getRegionWidth());
			setHeight(trd.getRegion().getRegionHeight());
			this.halfWidth = trd.getRegion().getRegionWidth() / 2f;
			this.halfHeight = trd.getRegion().getRegionHeight() / 2f;
		}

		if( this.world != null ) {
			this.body = setupBox2D();
		}
	}

	public void moveTo(float x, float y) {
		if( this.body != null ) {
			this.body.setTransform(x, y, this.body.getAngle());
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
		json.writeValue("x", getX());
		json.writeValue("y", getY());
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
}
