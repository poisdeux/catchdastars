package com.strategames.catchdastars.music;

import com.strategames.catchdastars.music.Library.library;

abstract public class Media implements library {
	private String name;
	private boolean selected;
	
	public Media(String name) {
		this.name = name;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	public String getName() {
		return name;
	}
}
