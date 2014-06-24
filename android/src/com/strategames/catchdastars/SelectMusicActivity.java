package com.strategames.catchdastars;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class SelectMusicActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, 
OnItemClickListener {
	public static final String BUNDLE_KEY_MUSICLIST = "bundle_key_musiclist";

	private MusicAdapter musicAdapter;
	private ArrayList<String> selectedFiles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmusicactivity);

		this.selectedFiles = new ArrayList<String>();

		getSupportLoaderManager().initLoader(0, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TRACK,
				MediaStore.Audio.Media.TITLE};

		return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, MediaStore.Audio.Media.ARTIST +","+ MediaStore.Audio.Media.TRACK);
	}

	public class MusicAdapter extends BaseAdapter {
		private Context context;
		private Cursor cursor;

		public MusicAdapter(Context c, Cursor cursor) {
			this.context = c;
			this.cursor = cursor;
		}

		public int getCount() {
			return cursor.getCount();
		}

		public Object getItem(int position) {
			cursor.moveToPosition(position);
			return cursor;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.selectmusiclistviewitem, null);
			}

			int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
			cursor.moveToPosition(position);
			String artist = cursor.getString(index);
			TextView tv = (TextView) convertView.findViewById(R.id.textview);
			tv.setText(artist);

			return convertView;
		}
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader,
			Cursor cursor) {

		int indexArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
		int indexAlbum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
		int indexData = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		int indexTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
		int indexTrack = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);

		HashMap<String, Artist> mediaMap = new HashMap<String, Artist>();

		while(cursor.moveToNext()) {
			String albumTitle = cursor.getString(indexAlbum);
			String data = cursor.getString(indexData);
			String songTitle = cursor.getString(indexTitle);
			String trackNumber = cursor.getString(indexTrack);

			String artistName = cursor.getString(indexArtist);
			if( ! mediaMap.containsKey(artistName) ) {
				mediaMap.put(artistName, new Artist(artistName));
			}

			Artist artist = mediaMap.get(artistName);
			artist.addTrack(albumTitle, songTitle, data);
		}

		ListView lv = (ListView) findViewById(R.id.PhoneMusicList);

		this.musicAdapter = new MusicAdapter(this, cursor);

		lv.setAdapter(this.musicAdapter);

		lv.setOnItemClickListener(this);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor cursor = (Cursor) this.musicAdapter.getItem(position);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		this.selectedFiles.add(cursor.getString(index));
	}

	private class Artist {
		private String name;
		private HashMap<String, Album> albums;

		public Artist(String name) {
			this.name = name;
			this.albums = new HashMap<String, Album>();
		}

		public String getName() {
			return name;
		}
		
		public void addTrack(String albumTitle, String trackTitle, String data) {
			if( ! this.albums.containsKey(albumTitle) ) {
				this.albums.put(albumTitle, new Album(albumTitle));
			}
			Album album = this.albums.get(albumTitle);
			album.addTrack(trackTitle, data);
		}
		
		public HashMap<String, Album> getAlbums() {
			return albums;
		}
	}
	
	private class Album {
		private ArrayList<Track> tracks;
		private String title;
		
		public Album(String title) {
			this.title = title;
			this.tracks = new ArrayList<Track>();
		}
		
		public String getTitle() {
			return title;
		}
		
		public void addTrack(String title, String data) {
			this.tracks.add(new Track(title, data));
		}
		
		public ArrayList<Track> getTracks() {
			return tracks;
		}
	}
	
	private class Track {
		private String data;
		private String title;
		
		public Track(String title, String data) {
			this.title = title;
			this.data = data;
		}
	}
}

