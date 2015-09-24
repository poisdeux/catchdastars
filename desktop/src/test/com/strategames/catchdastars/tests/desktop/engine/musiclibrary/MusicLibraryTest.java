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

package com.strategames.catchdastars.tests.desktop.engine.musiclibrary;

import org.junit.Assert;
import org.junit.Test;

import com.strategames.engine.musiclibrary.Album;
import com.strategames.engine.musiclibrary.Artist;
import com.strategames.engine.musiclibrary.Library;
import com.strategames.engine.musiclibrary.Track;

public class MusicLibraryTest {
	public static final int AMOUNT_OF_ARTISTS = 4;
	public static final int AMOUNT_OF_ALBUMS = 3;
	public static final int AMOUNT_OF_TRACKS = 8;

	@Test
	public void testEmptyLibrary() {
		Library library = new Library();
		Assert.assertNull("Empty library is not empty", library.getNexTrack());
	}

	@Test
	public void testEmptyAlbum() {
		Library library = new Library();
		Artist artist = new Artist("artist0");
		Album album = new Album("Album0", artist);
		artist.addAlbum(album);
		library.addArtist(artist);
		
		Assert.assertNull("Empty album is not empty", library.getNexTrack());
	}
	
	@Test
	public void testEmptyArtist() {
		Library library = new Library();
		Artist artist = new Artist("artist0");
		library.addArtist(artist);
		
		Assert.assertNull("Empty artist is not empty", library.getNexTrack());
	}
	
	@Test
	public void testGetNextTrack() {
		Library library = createLibrary();

		Track track = library.getNexTrack();
		while(track != null) {
			String trackName = track.getName();
			String albumName = track.getAlbum().getName();
			String artistName = track.getAlbum().getArtist().getName();
			Assert.assertFalse(trackName+" from "+albumName+", "+artistName+" already selected", track.isSelected());
			track.setSelected(true);

			track = library.getNexTrack();
		}

		testAreAllTracksSelected(library);
	}

	private void testAreAllTracksSelected(Library library) {
		for(int i = 0; i < AMOUNT_OF_ARTISTS; i++) {
			Artist artist = library.getArtist("artist"+i);
			for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
				Album album = artist.getAlbum("album"+j);
				for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
					Track track = album.getTrack("track"+k);
					if( track != null ) {
						String artistName = getArtistName(i);
						String albumName = getAlbumName(j);
						String trackName = getTrackName(k);
						Assert.assertTrue(trackName+" from "+albumName+", "+artistName+" not selected", track.isSelected());
						testTrackContent(track, artistName, albumName, trackName);
					} else {
						Assert.fail("Track "+i+", "+j+", "+k+" is null");
					}

				}
			}
		}
	}
	
	private void testTrackContent(Track track, String artistName, String albumName, String trackName) {
		Assert.assertTrue(track.getName() +" does not equal "+trackName, track.getName().contentEquals(trackName));
		Assert.assertTrue(track.getAlbum().getName() +" does not equal "+albumName, track.getAlbum().getName().contentEquals(albumName));
		Assert.assertTrue(track.getAlbum().getArtist().getName()+" does not equal "+artistName, track.getAlbum().getArtist().getName().contentEquals(artistName));
	}
	
	private Library createLibrary() {
		Library library = new Library();
		for(int i = 0; i < AMOUNT_OF_ARTISTS; i++) {
			for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
				for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
					String artistName = getArtistName(i);
					String albumName = getAlbumName(j);
					String trackName = getTrackName(k);
					library.addTrack(artistName, albumName, trackName, ""+k, "/opt/storage/"+albumName+"/"+trackName);
				}
			}
		}
		return library;
	}

	private String getArtistName(int i) {
		return "artist"+i;
	}
	
	private String getAlbumName(int i) {
		return "album"+i;
	}
	
	private String getTrackName(int i) {
		return "track"+i;
	}
}
