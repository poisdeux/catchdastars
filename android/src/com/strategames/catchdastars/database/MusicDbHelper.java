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

package com.strategames.catchdastars.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.strategames.catchdastars.database.MusicContract.Songs;
import com.strategames.engine.musiclibrary.Album;
import com.strategames.engine.musiclibrary.Artist;
import com.strategames.engine.musiclibrary.Library;
import com.strategames.engine.musiclibrary.Track;

public class MusicDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "FeedReader.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	private static final String SQL_CREATE_SONGS_TABLE =
			"CREATE TABLE " + Songs.TABLE_NAME + " (" +
					Songs._ID + " INTEGER PRIMARY KEY," +
					Songs.COLUMN_NAME_ARTIST + TEXT_TYPE + COMMA_SEP +
					Songs.COLUMN_NAME_ALBUM + TEXT_TYPE + COMMA_SEP +
					Songs.COLUMN_NAME_TRACK_TITLE + TEXT_TYPE + COMMA_SEP +
					Songs.COLUMN_NAME_TRACK_NUMBER + TEXT_TYPE + COMMA_SEP +
					Songs.COLUMN_NAME_TRACK_PATH + TEXT_TYPE +
					" )";

	private SQLiteDatabase sqliteDatabase;
	
	public MusicDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_SONGS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		this.sqliteDatabase = super.getWritableDatabase();
		return this.sqliteDatabase;
	}
	
	public long addSong(String artist, String album, String trackTitle, String trackNumber, String trackPath) {
		ContentValues values = new ContentValues();
		values.put(Songs.COLUMN_NAME_ARTIST, artist);
		values.put(Songs.COLUMN_NAME_ALBUM, album);
		values.put(Songs.COLUMN_NAME_TRACK_TITLE, trackTitle);
		values.put(Songs.COLUMN_NAME_TRACK_NUMBER, trackNumber);
		values.put(Songs.COLUMN_NAME_TRACK_PATH, trackPath);

		return this.sqliteDatabase.insert(Songs.TABLE_NAME, null, values);
	}

	public int deleteSong(String artist, String album, String trackTitle, String trackNumber, String trackPath) {
		return this.sqliteDatabase.delete(Songs.TABLE_NAME, 
				Songs.COLUMN_NAME_ARTIST + " = ? " + " AND " +
						Songs.COLUMN_NAME_ALBUM + " = ? " + " AND " +
						Songs.COLUMN_NAME_TRACK_TITLE + " = ? " + " AND " +
						Songs.COLUMN_NAME_TRACK_NUMBER + " = ? " + " AND " +
						Songs.COLUMN_NAME_TRACK_PATH + " = ? "
						, new String[] {
				artist,
				album,
				trackTitle,
				trackNumber,
				trackPath
		});
	}

	public void clearDatabase() {
		this.sqliteDatabase.delete(Songs.TABLE_NAME, null, null);
	}

	public Library getAll() {
		Library library = new Library();

		String[] projection = {
				Songs.COLUMN_NAME_ARTIST,
				Songs.COLUMN_NAME_ALBUM,
				Songs.COLUMN_NAME_TRACK_NUMBER,
				Songs.COLUMN_NAME_TRACK_PATH,
				Songs.COLUMN_NAME_TRACK_TITLE,
		};
		Cursor cursor = this.sqliteDatabase.query(Songs.TABLE_NAME, projection, null, null, null, null, null);
		int indexArtist = cursor.getColumnIndex(Songs.COLUMN_NAME_ARTIST);
		int indexAlbum = cursor.getColumnIndex(Songs.COLUMN_NAME_ALBUM);
		int indexTrackTitle = cursor.getColumnIndex(Songs.COLUMN_NAME_TRACK_TITLE);
		int indexTrackNumber = cursor.getColumnIndex(Songs.COLUMN_NAME_TRACK_NUMBER);
		int indexTrackPath = cursor.getColumnIndex(Songs.COLUMN_NAME_TRACK_PATH);
		while(cursor.moveToNext()) {
			String artistName = cursor.getString(indexArtist);
			String albumTitle = cursor.getString(indexAlbum);
			String trackTitle = cursor.getString(indexTrackTitle);
			String trackNumber = cursor.getString(indexTrackNumber);
			String trackPath = cursor.getString(indexTrackPath);
			
			Artist artist = library.getArtist(artistName);
			if( artist == null ) {
				artist = new Artist(artistName);
				library.addArtist(artist);
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

		return library;
	}
	
	public boolean isTrackInDatabase(String artist, String album, String trackTitle, String trackNumber, String trackPath) {
		String[] projection = {
				Songs.COLUMN_NAME_ARTIST,
				Songs.COLUMN_NAME_ALBUM,
				Songs.COLUMN_NAME_TRACK_NUMBER,
				Songs.COLUMN_NAME_TRACK_PATH,
				Songs.COLUMN_NAME_TRACK_TITLE,
		};
		String whereClausule = Songs.COLUMN_NAME_ARTIST + " = ? " + " AND " +
				Songs.COLUMN_NAME_ALBUM + " = ? " + " AND " +
				Songs.COLUMN_NAME_TRACK_TITLE + " = ? " + " AND " +
				Songs.COLUMN_NAME_TRACK_NUMBER + " = ? " + " AND " +
				Songs.COLUMN_NAME_TRACK_PATH + " = ? ";
		String[] args = new String[] {
				artist,
				album,
				trackTitle,
				trackNumber,
				trackPath
		};
		Cursor cursor = this.sqliteDatabase.query(Songs.TABLE_NAME, projection, whereClausule, args, null, null, null);
		return cursor.getCount() > 0;
	}
}
