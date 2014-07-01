package com.strategames.engine.utils;

public class Collectable {
	private int total;
	private int collected;
	
	public Collectable() {
		this.total = 0;
		this.collected = 0;
	}
	
	public int getCollected() {
		return collected;
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
	public void collect() {
		this.collected++;
	}
	
	public boolean allCollected() {
		if( this.total <= this.collected ) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Collectable: total="+total+", collected="+collected;
	}
}
