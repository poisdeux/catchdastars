package com.strategames.catchdastars.utils;

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
	
	public void setTotal(int total) {
		this.total = total;
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
