package com.strategames.engine.musiclibrary;

import java.util.HashMap;
import java.util.Iterator;

public class Album extends LibraryItem {
	private HashMap<String, Track> tracks;
	private Artist artist;
	private Iterator<Track> trackIterator;
	
	private Album() {
		super(null);
	};
	
	public Album(String name, Artist artist) {
		super(name);
		this.tracks = new HashMap<String, Track>();
		this.artist = artist;
	}

	public void addTrack(Track track) {
		this.tracks.put(track.getName(), track);
	}
	
	public HashMap<String, Track> getTracks() {
		return tracks;
	}
	
	public Track getTrack(String trackTitle) {
		return this.tracks.get(trackTitle);
	}
	
	public Artist getArtist() {
		return artist;
	}
	
	public Track getNextTrack() {
		if( ( trackIterator == null ) || ( ! this.trackIterator.hasNext() ) ) {
			this.trackIterator = this.tracks.values().iterator();
		}
		
		Track track = null;
		if( this.trackIterator.hasNext() ) {
			track = this.trackIterator.next();
		}
		return track;
	}
}
