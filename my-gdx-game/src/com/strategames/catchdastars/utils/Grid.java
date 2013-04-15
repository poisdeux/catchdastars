package com.strategames.catchdastars.utils;

import com.badlogic.gdx.math.Vector2;

public class Grid {

	private static int cellWidth = 32;
	private static int cellHeight = 32;
	
	static public Vector2 map(Vector2 vector) {
		return map(vector.x, vector.y);
	}
	
	static public Vector2 map(float x, float y) {
		Vector2 mapped = new Vector2();
		mapped.x = Math.round( x / cellWidth ) * cellWidth;
		mapped.y = Math.round( y / cellHeight ) * cellHeight;
		return mapped;
	}
}
