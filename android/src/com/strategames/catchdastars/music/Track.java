package com.strategames.catchdastars.music;

public class Track {
	private String data;
	private String title;
	private String number;

	public Track(String title, String data, String number) {
		this.title = title;
		this.data = data;
		this.number = number;
	}

	public String getName() {
		return title;
	}

	public String getData() {
		return data;
	}

	public String getNumber() {
		return number;
	}
}
