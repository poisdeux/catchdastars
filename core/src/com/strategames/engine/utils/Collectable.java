/**
 * 
 * Copyright 2013 Martijn Brekhof
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

import java.util.HashSet;

import com.strategames.engine.gameobject.GameObject;

public class Collectable {
	private int total;
	private HashSet<GameObject> collected;
	
	public Collectable() {
		this.total = 0;
		this.collected = new HashSet<GameObject>();
	}
	
	public int getAmountCollected() {
		return collected.size();
	}
	
	public int getTotal() {
		return total;
	}
	
	/**
	 * Increases the amount of items to collect by one
	 */
	public void add() {
		this.total++;
	}
	
	/**
	 * Increases the amount of items to collect by amount
	 * @param amount
	 */
	public void add(int amount) {
		this.total += amount;
	}
	
	/**
	 * Increases the amount of collected items by one
	 */
	public void collect(GameObject object) {
		this.collected.add(object);
	}
	
	public boolean allCollected() {
		if( this.total <= this.collected.size() ) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Collectable: total="+total+", collected="+collected;
	}
}
