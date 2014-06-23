package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer implements Music.OnCompletionListener {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final MusicPlayer INSTANCE = new MusicPlayer();
	}
	
	private ArrayList<String> musicFiles = new ArrayList<String>();
	private int index;
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
	
	public void add(String filename) {
		this.musicFiles.add(filename);
	}
	
	public void reset() {
		this.index = 0;	
	}
	
	/**
	 * Plays next music file. If next does not exist it resets the counter
	 * and plays the first music file
	 */
	public void playNext() {
		if( this.index >= this.musicFiles.size() ) {
			reset();
		}
		
		//Dispose any previous music file as these are pretty resource hungry
		if( this.music != null ) {
			this.music.dispose();
		}
		
		this.music = Gdx.audio.newMusic(Gdx.files.internal(this.musicFiles.get(this.index)));
		this.music.setVolume(this.volume);
		this.music.play();
		this.music.setOnCompletionListener(this);
	}
	
	public void pause() {
		this.music.pause();
	}
	
	public void resume() {
		this.music.play();
	}

	@Override
	public void onCompletion(Music music) {
		playNext();
	}
}
