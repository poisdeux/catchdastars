package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Artist extends Media {
	private HashMap<String, Album> albums;
	private Album selectedAlbum;
	
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
			this.albums.put(albumTitle, new Album(albumTitle));
		}
		Album album = this.albums.get(albumTitle);
		album.addTrack(trackTitle, data, trackNumber);
	}

	public HashMap<String, Album> getAlbums() {
		return albums;
	}
}
