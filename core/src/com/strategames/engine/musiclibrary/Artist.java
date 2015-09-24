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

import java.util.HashMap;
import java.util.Iterator;

public class Artist extends LibraryItem {
	private HashMap<String, Album> albums;
	private Album currentAlbum;
	
	private Iterator<Album> albumIterator;
	
	private Artist() {
		super(null);
	}
	
	public Artist(String name) {
		super(name);
		this.albums = new HashMap<String, Album>();
	}

	public void addAlbum(Album album) {
		this.albums.put(album.getName(), album);	
	}
	
	public HashMap<String, Album> getAlbums() {
		return albums;
	}
	
	public Album getAlbum(String albumTitle) {
		return this.albums.get(albumTitle);
	}
	
	public Track getNextTrack() {
		if( this.albumIterator == null ) {
			this.albumIterator = this.albums.values().iterator();
			if( this.albumIterator.hasNext() ) {
				this.currentAlbum = this.albumIterator.next();
			} else {
				//Empty album list
				this.albumIterator = null;
				return null;
			}
		}
		
		Track track = this.currentAlbum.getNextTrack();
		if( track == null ) {
			if( this.albumIterator.hasNext() ) {
				this.currentAlbum = this.albumIterator.next();
				track = this.currentAlbum.getNextTrack();
			} else {
				this.albumIterator = null;
			}
		}
		return track;
	}
}
