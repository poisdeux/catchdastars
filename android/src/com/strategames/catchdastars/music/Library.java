package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Library {

	public interface library {
		public boolean isSelected();
		public String getName();
	}
	
	private HashMap<String, Artist> artists;
	private Artist selectedArtist;

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

	public Artist getSelectedArtist() {
		return selectedArtist;
	}

	public void setSelectedArtist(Artist selectedArtist) {
		this.selectedArtist = selectedArtist;
	}
	
	public String[] getArtistNames() {
		return this.artists.keySet().toArray(new String[this.artists.size()]);
	}
}
