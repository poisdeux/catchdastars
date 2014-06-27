package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Album extends Media {
	private HashMap<String, Track> tracks;
	
	public Album(String name) {
		super(name);
		this.tracks = new HashMap<String, Track>();
	}
	
	public void addTrack(String title, String data, String number) {
		this.tracks.put(title, new Track(title, data, number));
	}

	public HashMap<String, Track> getTracks() {
		return tracks;
	}
}
