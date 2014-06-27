package com.strategames.catchdastars.database;

import android.provider.BaseColumns;

public class MusicContract {

	public MusicContract() {}
	
	public static abstract class Songs implements BaseColumns {
        public static final String TABLE_NAME = "artists";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_ALBUM = "album";
        public static final String COLUMN_NAME_TRACK_TITLE = "track_title";
        public static final String COLUMN_NAME_TRACK_NUMBER = "track_number";
    }
}
