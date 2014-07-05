package com.strategames.engine.musiclibrary;

import java.util.HashMap;
import java.util.Iterator;

public class Library {

	private HashMap<String, Artist> artists;
	private Iterator<Artist> artistIterator;
	
	public Library() {
		this.artists = new HashMap<String, Artist>();
	}

	public void addArtist(Artist artist) {
		this.artists.put(artist.getName(), artist);
	}

	public HashMap<String, Artist> getArtists() {
		return artists;
	}

	public Artist getArtist(String artist) {
		return this.artists.get(artist);
	}
	
	public String[] getArtistNames() {
		return this.artists.keySet().toArray(new String[this.artists.size()]);
	}
	
	public Track getNexTrack() {
		if( ( artistIterator == null ) || ( ! this.artistIterator.hasNext() ) ) {
			this.artistIterator = this.artists.values().iterator();
		}
		Track track = null;
		if( artistIterator.hasNext() ) {
			track = this.artistIterator.next().getNextTrack();
		}
		
		return track;
	}
	
	/**
	 * Adds a track to the library used to populate the ListView
	 * @param artistName
	 * @param albumTitle
	 * @param trackTitle
	 * @param trackNumber
	 * @param trackPath
	 */
	public void addTrack(String artistName, String albumTitle, String trackTitle, String trackNumber, String trackPath) {
		Artist artist = getArtist(artistName);
		if( artist == null ) {
			artist = new Artist(artistName);
			addArtist(artist);
		}

		Album album = artist.getAlbum(albumTitle);
		if( album == null ) {
			album = new Album(albumTitle, artist);
			artist.addAlbum(album);
		}

		Track track = album.getTrack(trackTitle);
		if( track == null ) {
			track = new Track(trackTitle, trackPath, trackNumber, album);
			album.addTrack(track);
		}
	}
}
