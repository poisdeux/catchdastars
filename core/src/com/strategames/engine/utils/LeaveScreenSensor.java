package com.strategames.engine.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Wall;
import com.strategames.engine.interfaces.SensorObject;

public class LeaveScreenSensor implements SensorObject {
	static private Game sGame;
	static private LeaveScreenSensor instance;
	static private Body body;
	private boolean isHit;
	
	static public void create(Game game) {
		sGame = game;
		if( instance == null ) {
			instance = new LeaveScreenSensor();
		} else {
			//recreate body if deleted from world
			World world = sGame.getWorld();
			Array<Body> bodies = new Array<Body>();
			world.getBodies(bodies);
			if( ! bodies.contains(body, true) ) {
				instance = new LeaveScreenSensor();
			}
		}
	}
	
	private LeaveScreenSensor() {
		Vector3 worldSize = sGame.getWorldSize();
		Vector2 beginning = new Vector2(0, 0).add(-Wall.WIDTH, -Wall.HEIGHT);
		Vector2 end = new Vector2(worldSize.x, worldSize.y).add(Wall.WIDTH, Wall.HEIGHT);

		Vector2 leftBottom = new Vector2(beginning.x, beginning.y);
		Vector2 rightBottom = new Vector2(end.x, beginning.y);
		Vector2 rightTop = new Vector2(end.x, end.y);
		Vector2 leftTop = new Vector2(beginning.x, end.y);
		
		ChainShape chain = new ChainShape();
		chain.createLoop(new Vector2[] {leftBottom, rightBottom, rightTop, leftTop});

		BodyDef bd = new BodyDef();  
//		bd.position.set(beginning);
		bd.type = BodyType.StaticBody;
		body = sGame.getWorld().createBody(bd);
		Fixture fixture = body.createFixture(chain, 0.0f);
		fixture.setSensor(true);
		
		body.setUserData(this);
	}

	@Override
	public boolean isHit() {
		return isHit;
	}
	
	@Override
	public void setHit(boolean hit) {
		this.isHit = hit;
	}
}
