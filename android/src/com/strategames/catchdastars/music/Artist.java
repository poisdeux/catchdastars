package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Artist {
	private String name;
	private HashMap<String, Album> albums;
	private Album selectedAlbum;
	
	private boolean selected = false; 
	
	public Artist(String name) {
		this.name = name;
		this.albums = new HashMap<String, Album>();
	}

	public String getName() {
		return name;
	}

	public void setSelectedAlbum(Album selectedAlbum) {
		this.selectedAlbum = selectedAlbum;
	}
	
	public Album getSelectedAlbum() {
		return selectedAlbum;
	}
	
	public void setSelected(boolean includeAll) {
		this.selected = includeAll;
	}
	
	public boolean getIncludeAll() {
		return this.selected;
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
