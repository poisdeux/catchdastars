/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.musiclibrary;

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
	
	@Override
	public String toString() {
		return super.toString() +", Album="+this.album;
	}
}
