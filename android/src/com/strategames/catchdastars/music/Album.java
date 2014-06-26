package com.strategames.catchdastars.music;

import java.util.ArrayList;

public class Album {
	private ArrayList<Track> tracks;
	private String title;

	public Album(String title) {
		this.title = title;
		this.tracks = new ArrayList<Track>();
	}

	public String getTitle() {
		return title;
	}

	public void addTrack(String title, String data, String number) {
		this.tracks.add(new Track(title, data, number));
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}
}
