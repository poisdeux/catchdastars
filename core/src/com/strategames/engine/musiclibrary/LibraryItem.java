package com.strategames.engine.musiclibrary;


abstract public class LibraryItem {
	private String name;
	private boolean selected;
	
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
	
	@Override
	public String toString() {
		return this.name + " selected="+this.selected;
	}
}
