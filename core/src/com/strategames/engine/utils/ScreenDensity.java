package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;


public class ScreenDensity {
	
	public enum Densities {
		mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
	}
	
	private static Densities screenDensity;
	
	public static void setScreenDensity(Densities density) {
		ScreenDensity.screenDensity = density;
	}
	
	public static String getDensityName() {
		if( screenDensity == null ) {
			getScreenDensity();
		}
		return screenDensity.name();
	}
	
	public static Densities getScreenDensity() {
		screenDensity = Densities.mdpi;

		float density = Gdx.graphics.getDensity();

		if( density >= 3 ) {
			screenDensity = Densities.xxhdpi;
		} else if ( density >= 2 ) {
			screenDensity = Densities.xhdpi;
		} else if ( density >= 1.5 ) {
			screenDensity = Densities.hdpi;
		}
		return screenDensity;
	}
}
