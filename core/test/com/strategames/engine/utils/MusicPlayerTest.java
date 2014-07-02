package com.strategames.engine.utils;

import org.junit.Test;

import com.strategames.engine.musiclibrary.Library;

public class MusicPlayerTest {

	@Test
	public void testPlayNext() {
		MusicPlayer player = MusicPlayer.getInstance();
		
		Library library = createLibrary();
		
		player.setLibrary(library);
	}
	
	private Library createLibrary() {
		Library library = new Library();
		
		return library;
	}

}
