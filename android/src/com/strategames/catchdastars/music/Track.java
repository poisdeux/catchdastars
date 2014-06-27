package com.strategames.catchdastars.music;

public class Track extends Media {
	private String data;
	private String number;
	
	public Track(String title, String data, String number) {
		super(title);
		this.data = data;
		this.number = number;
	}

	public String getData() {
		return data;
	}

	public String getNumber() {
		return number;
	}
}
