package com.strategames.engine.musiclibrary;

import java.util.HashMap;
import java.util.Iterator;

public class Library {

	private HashMap<String, Artist> artists;
	private Iterator<Artist> artistIterator;
	
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
	
	public Track getNexTrack() {
		if( ( artistIterator == null ) || ( ! this.artistIterator.hasNext() ) ) {
			this.artistIterator = this.artists.values().iterator();
		}
		Track track = null;
		if( artistIterator.hasNext() ) {
			track = this.artistIterator.next().getNextTrack();
		}
		
		return track;
	}
}
