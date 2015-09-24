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

package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.strategames.engine.musiclibrary.Library;
import com.strategames.engine.musiclibrary.Track;

public class MusicPlayer implements Music.OnCompletionListener {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final MusicPlayer INSTANCE = new MusicPlayer();
	}
	
	private Library library;
	
	private Music music;
	private float volume = 0.7f;
	
	private MusicPlayer() {	
		this.volume = Settings.getInstance().getMusicVolume();
	}
	
	public static MusicPlayer getInstance() {
        return SingletonHolder.INSTANCE;
    }
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public void setLibrary(Library library) {
		this.library = library;
	}
	
	/**
	 * Plays next music file. If next does not exist it resets the counter
	 * and plays the first music file
	 */
	public void playNext() {
		if( this.library == null ) {
			return;
		}
		
		Track track = this.library.getNexTrack();
		if( track == null ) {
			return;
		}
		
		//Dispose any previous music file as these are pretty resource hungry
		if( this.music != null ) {
			this.music.dispose();
		}
		
		this.music = Gdx.audio.newMusic(Gdx.files.absolute(track.getData()));
		this.music.setVolume(this.volume);
		this.music.play();
		this.music.setOnCompletionListener(this);
	}
	
	public void pause() {
		if( this.music != null ) {
			this.music.pause();
		}
	}
	
	public void resume() {
		if( this.music != null ) {
			this.music.play();
		}
	}
	
	@Override
	public void onCompletion(Music music) {
		playNext();
	}
}
