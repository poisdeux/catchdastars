package com.strategames.engine.game;

import com.badlogic.gdx.physics.box2d.World;

public class WorldThread extends Thread {

	private float timeStepSeconds;
	private long timeStepMillis;
	private int velocityIterations;
	private int positionIterations;
	private World world;
	private long previousTime;
	private boolean stopThread = false;

	private volatile int lock = 0;

	public WorldThread(World world, float timeStepSeconds, int velocityIterations, int positionIterations) {
		super();
		this.world = world;
		this.timeStepSeconds = timeStepSeconds;
		this.velocityIterations = velocityIterations;
		this.positionIterations = positionIterations;
		this.timeStepMillis = (long) (timeStepSeconds * 1000f);
	}

	@Override
	public void run() {
		while( ! this.stopThread ) {
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

			if( lock != 1 ){
				lock = 1;
				world.step(timeStepSeconds, velocityIterations, positionIterations);
				lock = 0;
			}
		}
	}

	public void stopThread() {
		this.stopThread = true;
	}

	public boolean isLocked() {
		return lock == 1;
	}

	public void lock() {
		lock = 1;
	}

	public void unlock() {
		lock = 0;
	}
}
