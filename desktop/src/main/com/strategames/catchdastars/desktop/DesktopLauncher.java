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

package com.strategames.catchdastars.desktop;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.strategames.catchdastars.game.CatchDaStars;
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
		config.vSyncEnabled = true;
		
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
		String path = Gdx.files.getLocalStoragePath()+"/games/";
		try {
			PrintWriter writer = new PrintWriter(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void importLevels(OnLevelsReceivedListener listener) {
		// TODO Auto-generated method stub
		
	}
}
