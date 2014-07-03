package com.strategames.engine.musiclibrary;


abstract public class LibraryItem {
	private String name;
	private boolean selected;
	
	private LibraryItem() {
	}
	
	protected LibraryItem(String name) {
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
