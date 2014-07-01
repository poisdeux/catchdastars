package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Album extends LibraryItem {
	private HashMap<String, Track> tracks;
	private Artist artist;
	
	private Album() {
		super(null);
	};
	
	public Album(String name, Artist artist) {
		super(name);
		this.tracks = new HashMap<String, Track>();
		this.artist = artist;
	}
	
	public void addTrack(String title, String data, String number) {
		this.tracks.put(title, new Track(title, data, number, this));
	}

	public HashMap<String, Track> getTracks() {
		return tracks;
	}
	
	public Artist getArtist() {
		return artist;
	}
}
