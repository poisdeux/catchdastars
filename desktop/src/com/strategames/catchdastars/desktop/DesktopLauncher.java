package com.strategames.catchdastars.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.strategames.catchdastars.CatchDaStars;
import com.strategames.engine.interfaces.ExportImport;
import com.strategames.engine.interfaces.MusicSelector;
import com.strategames.engine.interfaces.OnLevelsReceivedListener;
import com.strategames.engine.interfaces.OnMusicFilesReceivedListener;
import com.strategames.engine.musiclibrary.Library;

public class DesktopLauncher implements ExportImport, MusicSelector {
	public static void main (String[] arg) {
		new DesktopLauncher();
	}
	
	public DesktopLauncher() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Catch Da Stars";
		config.width = 510;
		config.height = 810;

		CatchDaStars game = new CatchDaStars();
		game.setExporterImporter(this);
		game.setMusicSelector(this);
		new LwjglApplication(game, config);
	}

	@Override
	public void selectMusic(OnMusicFilesReceivedListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Library getLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void export(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void importLevels(OnLevelsReceivedListener listener) {
		// TODO Auto-generated method stub
		
	}
}
