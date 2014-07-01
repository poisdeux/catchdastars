package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.strategames.catchdastars.music.Library;
import com.strategames.catchdastars.music.Track;

public class MusicPlayer implements Music.OnCompletionListener {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final MusicPlayer INSTANCE = new MusicPlayer();
	}
	
	private Library library;
	
	private Music music;
	private float volume = 0.7f;
	
	private MusicPlayer() {	}
	
	public static MusicPlayer getInstance() {
        return SingletonHolder.INSTANCE;
    }
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public float getVolume() {
		return volume;
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
