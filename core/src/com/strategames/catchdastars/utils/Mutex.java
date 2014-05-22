package com.strategames.catchdastars.utils;

public class Mutex {
	private volatile int lock = 0;
	
	/**
	 * Tries to get lock indefinitely
	 * @throws InterruptedException 
	 */
	public void getLockWait() throws InterruptedException {
		while( lock == 1 ) {
			Thread.sleep(100);
		}
		lock = 1;
	}
	
	public void releaseLock() {
		lock = 0;
	}
}
