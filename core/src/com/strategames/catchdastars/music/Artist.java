package com.strategames.catchdastars.music;

import java.util.HashMap;
import java.util.Iterator;

public class Artist extends LibraryItem {
	private HashMap<String, Album> albums;
	private Album selectedAlbum;
	
	private Iterator<Album> albumIterator;
	
	private Artist() {
		super(null);
	}
	
	public Artist(String name) {
		super(name);
		this.albums = new HashMap<String, Album>();
	}

	public void setSelectedAlbum(Album selectedAlbum) {
		this.selectedAlbum = selectedAlbum;
	}
	
	public Album getSelectedAlbum() {
		return selectedAlbum;
	}
	
	public void addTrack(String albumTitle, String trackTitle, String trackNumber, String data) {
		if( ! this.albums.containsKey(albumTitle) ) {
			this.albums.put(albumTitle, new Album(albumTitle, this));
		}
		Album album = this.albums.get(albumTitle);
		album.addTrack(trackTitle, data, trackNumber);
	}

	public HashMap<String, Album> getAlbums() {
		return albums;
	}
	
	public Track getNextTrack() {
		if( ( albumIterator == null ) || ( ! this.albumIterator.hasNext() ) ) {
			this.albumIterator = this.albums.values().iterator();
		}
		Album album = this.albumIterator.next();
		return album.getNextTrack();
	}
}
