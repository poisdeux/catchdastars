package com.strategames.catchdastars.music;

public class Track {
	private String data;
	private String title;
	private String number;
	private boolean include;
	
	public Track(String title, String data, String number) {
		this.title = title;
		this.data = data;
		this.number = number;
	}

	public void setSelected(boolean include) {
		this.include = include;
	}
	
	public boolean getInclude() {
		return this.include;
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
