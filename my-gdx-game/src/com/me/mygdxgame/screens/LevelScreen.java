package com.me.mygdxgame.screens;

import java.util.ArrayList;
import java.util.Iterator;

import sun.font.CreatedFontTracker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.me.mygdxgame.Textures;
import com.me.mygdxgame.gameobjects.Balloon;
import com.me.mygdxgame.gameobjects.Star;
import com.me.mygdxgame.gameobjects.Wall;
import com.me.mygdxgame.gameobjects.Wall.Type;

public class LevelScreen extends AbstractScreen
{

	//	private final Profile profile;
	//	private final Level level;

	private float viewportWidth = 800;
	private float viewportHeight = 480;
	private float gravityFactor = 10;

	private ArrayList<Wall> walls;
	private ArrayList<Star> stars;
	private ArrayList<Balloon> balloons;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector2 gravity;

	private World world;
	static final float WORLD_TO_BOX = 0.01f;
	static final float BOX_TO_WORLD = 100f;
	private Box2DDebugRenderer debugRenderer;

	boolean accelerometerAvailable;
	

	public LevelScreen(
			Game game,
			int level )
	{
		super( game );

		// set the basic attributes
		//		profile = game.getProfileManager().retrieveProfile();
		//		level = game.getLevelManager().findLevelById( targetLevelId );
	}

	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	public void show()
	{
		super.show();

		//		// create the ship and add it to the stage
		//		ship2d = Ship2D.create( profile.getShip(), getAtlas() );
		//
		//		// center the ship horizontally
		//		ship2d.setInitialPosition( ( stage.getWidth() / 2 - ship2d.getWidth() / 2 ),
		//				ship2d.getHeight() );
		//
		//		// add the ship to the stage
		//		stage.addActor( ship2d );

		// add a fade-in effect to the whole stage
//		stage.getRoot().getColor().a = 0f;
//		stage.getRoot().addAction( Actions.fadeIn( 0.5f ) );

		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, viewportWidth, viewportHeight);

		this.batch = new SpriteBatch();
		this.batch.setProjectionMatrix(camera.combined);

		this.gravity = new Vector2();
		gravity.set(0, -this.gravityFactor);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

		this.world = new World(gravity, true);

		this.debugRenderer = new Box2DDebugRenderer();

		this.balloons = new ArrayList<Balloon>();
		this.balloons.add(new Balloon(world, (viewportWidth - 48) / 2f, 20f));

		this.walls = new ArrayList<Wall>();
		this.walls.add(new Wall(world,
				400,
				240,
				200f,
				Type.HORIZONTAL)
				);
		this.walls.add(new Wall(world,
				440,
				240,
				100f,
				Type.VERTICAL)
				);

		this.stars = new ArrayList<Star>();
		this.stars.add(new Star(world, 
				400, 
				200, Star.Type.RED));
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
//		super.render(delta);
		
		Gdx.gl.glClearColor(0, 0, 0.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if( Gdx.app.getType() == ApplicationType.Android ) {
			gravity.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
			//		orientation.nor();
			gravity.mul(this.gravityFactor);
			world.setGravity(gravity);
		} 

		this.world.step(1/45f, 6, 2);
		this.debugRenderer.render(world, camera.combined);

		this.batch.begin();

		Iterator<Balloon> bi = this.balloons.iterator();
		while (bi.hasNext()) {
			Balloon b = bi.next();
			Body body = b.getBody();

			boolean objectRequiresTranslation = false;

			Vector2 position = body.getPosition();
			float x = position.x;
			float y = position.y;

			if( x < 0 ) {
				x = 800;
				objectRequiresTranslation = true;
			} else if ( x > 800 ) {
				x = 0;
				objectRequiresTranslation = true;
			}

			if( y < 0 ) {
				y = 480;
				objectRequiresTranslation = true;
			} else if ( y > 480 ) {
				y = 0;
				objectRequiresTranslation = true;
			}

			b.applyForce(this.world.getGravity().mul(body.getMass()).mul(-5f));


			b.setAngle(MathUtils.radiansToDegrees * body.getAngle());
			b.setPosition(x, y, objectRequiresTranslation);
			b.draw(batch);
		}

		Iterator<Wall> iw = walls.iterator();
		while(iw.hasNext()) {
			Wall w = iw.next();
			w.draw(batch);
		}

		Iterator<Star> is = this.stars.iterator();
		while(is.hasNext()) {
			Star s = is.next();
			s.draw(batch);
		}

		this.batch.end();		

	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}
