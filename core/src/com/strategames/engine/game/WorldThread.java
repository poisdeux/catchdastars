package com.strategames.engine.game;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.DynamicBody;
import com.strategames.engine.gameobject.GameObject;

public class WorldThread extends Thread {

	private float timeStepSeconds;
	private long timeStepMillis;
	private int velocityIterations;
	private int positionIterations;
	private World world;
	private Game game;
	private long previousTime;
	private boolean stopThread = false;
	private Array<GameObject> gameObjectsForAddition;
	private Array<GameObject> gameObjectsForDeletion;

	public WorldThread(Game game, float timeStepSeconds, int velocityIterations, int positionIterations) {
		super();
		this.game = game;
		this.world = game.getWorld();
		this.timeStepSeconds = timeStepSeconds;
		this.velocityIterations = velocityIterations;
		this.positionIterations = positionIterations;
		this.timeStepMillis = (long) (timeStepSeconds * 1000f);
		this.gameObjectsForAddition = new Array<GameObject>();
		this.gameObjectsForDeletion = new Array<GameObject>();
	}

	@Override
	public void run() {
		while( ! this.stopThread ) {
			this.game.updateWorld();
			
			long delta = System.currentTimeMillis() - previousTime;
			//			Gdx.app.debug("WorldThread", "delta="+delta+", timeStepMillis="+timeStepMillis);
			if( delta < timeStepMillis ) {
				try {
					Thread.sleep(timeStepMillis - delta);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			previousTime = System.currentTimeMillis();

			handleAddedGameObjectsQueue();
			handleDeletedGameObjectsQueue();
			applyForces();
			
			world.step(timeStepSeconds, velocityIterations, positionIterations);
		}
	}

	public void stopThread() {
		this.stopThread = true;
	}

	public void addGameObject(GameObject object) {
		synchronized (gameObjectsForAddition) {
			gameObjectsForAddition.add(object);
		}
	}

	public void deleteGameObject(GameObject object) {
		synchronized (gameObjectsForDeletion) {
			gameObjectsForDeletion.add(object);
		}
	}

	private void handleAddedGameObjectsQueue() {
		synchronized (gameObjectsForAddition) {
			for(int i = 0; i < gameObjectsForAddition.size; i++) {
				gameObjectsForAddition.get(i).setupBody();
			}
			gameObjectsForAddition.clear();
		}
	}
	
	private void handleDeletedGameObjectsQueue() {
		synchronized (gameObjectsForDeletion) {
			for(int i = 0; i < gameObjectsForDeletion.size; i++) {
				gameObjectsForDeletion.get(i).getBody().setActive(false);
			}
			gameObjectsForDeletion.clear();
		}
	}
	
	/**
	 * TODO create separate arrays for dynamic and static gameobjects to minimize 
	 * this loop
	 */
	private void applyForces() {
		Array<GameObject> objectsInGame = this.game.getGameObjectsInGame();
		synchronized (objectsInGame) {
			for(int i = 0; i < objectsInGame.size; i++) {
				GameObject object = objectsInGame.get(i);
				if( object instanceof DynamicBody ) {
					((DynamicBody) object).applyForce();
				}
			}
		}
	}
}
