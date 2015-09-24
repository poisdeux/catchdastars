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

public class Album extends LibraryItem {
	private HashMap<String, Track> tracks;
	private Artist artist;
	private Iterator<Track> trackIterator;

	private Album() {
		super(null);
	}

	public Album(String name, Artist artist) {
		super(name);
		this.tracks = new HashMap<String, Track>();
		this.artist = artist;
	}

	public void addTrack(Track track) {
		this.tracks.put(track.getName(), track);
	}

	public HashMap<String, Track> getTracks() {
		return tracks;
	}

	public Track getTrack(String trackTitle) {
		return this.tracks.get(trackTitle);
	}

	public Artist getArtist() {
		return artist;
	}

	public Track getNextTrack() {
		if( trackIterator == null ) {
			this.trackIterator = this.tracks.values().iterator();
		}

		if( this.trackIterator.hasNext() ) {
			return this.trackIterator.next();
		} else {
			this.trackIterator = null;
			return null;
		}
	}
	
	@Override
	public String toString() {
		return super.toString()+", Artist="+this.artist;
	}
}
