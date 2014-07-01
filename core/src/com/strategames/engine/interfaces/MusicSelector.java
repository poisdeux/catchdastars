package com.strategames.engine.interfaces;

import com.strategames.engine.musiclibrary.Library;

public interface MusicSelector {
	public void selectMusic(OnMusicFilesReceivedListener listener);
	public Library getLibrary();
}
