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
