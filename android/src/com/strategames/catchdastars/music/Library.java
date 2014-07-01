package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Library {

	private HashMap<String, Artist> artists;

	public Library() {
		this.artists = new HashMap<String, Artist>();
	}

	public void add(Artist artist) {
		this.artists.put(artist.getName(), artist);
	}

	public HashMap<String, Artist> get() {
		return artists;
	}

	public Artist get(String artist) {
		return this.artists.get(artist);
	}
	
	public String[] getArtistNames() {
		return this.artists.keySet().toArray(new String[this.artists.size()]);
	}
}
