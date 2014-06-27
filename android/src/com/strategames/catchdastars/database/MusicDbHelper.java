package com.strategames.catchdastars.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.strategames.catchdastars.database.MusicContract.Songs;

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

	public MusicDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_SONGS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public long addSong(SQLiteDatabase db, String artist, String album, String trackTitle, String trackNumber, String trackPath) {
		ContentValues values = new ContentValues();
		values.put(Songs.COLUMN_NAME_ARTIST, artist);
		values.put(Songs.COLUMN_NAME_ALBUM, album);
		values.put(Songs.COLUMN_NAME_TRACK_TITLE, trackTitle);
		values.put(Songs.COLUMN_NAME_TRACK_NUMBER, trackNumber);
		values.put(Songs.COLUMN_NAME_TRACK_PATH, trackPath);

		return db.insert(Songs.TABLE_NAME, null, values);
	}

	public int deleteSong(SQLiteDatabase db, String artist, String album, String trackTitle, String trackNumber, String trackPath) {
		return db.delete(Songs.TABLE_NAME, 
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

	public int deleteAllSongs(SQLiteDatabase db) {
		return db.delete(Songs.TABLE_NAME, null, null);
	}
	
	public Cursor getAllSongs(SQLiteDatabase db) {
		String[] projection = {
				Songs.COLUMN_NAME_ARTIST,
				Songs.COLUMN_NAME_ALBUM,
				Songs.COLUMN_NAME_TRACK_NUMBER,
				Songs.COLUMN_NAME_TRACK_PATH,
				Songs.COLUMN_NAME_TRACK_TITLE,
		};
		return db.query(Songs.TABLE_NAME, projection, null, null, null, null, null);
	}
}
