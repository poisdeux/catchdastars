package com.strategames.catchdastars.interfaces;

import com.strategames.catchdastars.music.Library;

public interface MusicSelector {
	public void selectMusic(OnMusicFilesReceivedListener listener);
	public Library getLibrary();
}
