package com.strategames.catchdastars.music;

public class Track extends LibraryItem {
	private String data;
	private String number;
	private Album album;
	
	private Track() {
		super(null);
	}
	
	public Track(String title, String data, String number, Album album) {
		super(title);
		this.data = data;
		this.number = number;
		this.album = album;
	}

	public String getData() {
		return data;
	}

	public String getNumber() {
		return number;
	}
	
	public Album getAlbum() {
		return album;
	}
}
