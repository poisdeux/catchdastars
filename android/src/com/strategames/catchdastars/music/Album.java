package com.strategames.catchdastars.music;

import java.util.HashMap;

public class Album {
	private HashMap<String, Track> tracks;
	private String title;
	private boolean includeAll;
	
	public Album(String title) {
		this.title = title;
		this.tracks = new HashMap<String, Track>();
	}

	public String getTitle() {
		return title;
	}

	public void setSelected(boolean includeAll) {
		this.includeAll = includeAll;
	}
	
	public boolean getIncludeAll() {
		return this.includeAll;
	}
	
	public void addTrack(String title, String data, String number) {
		this.tracks.put(title, new Track(title, data, number));
	}

	public HashMap<String, Track> getTracks() {
		return tracks;
	}
}
