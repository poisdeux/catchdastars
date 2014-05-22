package com.strategames.catchdastars.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.strategames.catchdastars.CatchDaStars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Catch Da Stars";
		config.width = 504;
		config.height = 800;

		new LwjglApplication(new CatchDaStars(), config);
	}
}
