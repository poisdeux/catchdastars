/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.utils;

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
