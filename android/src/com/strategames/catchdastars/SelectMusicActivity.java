package com.strategames.catchdastars;

import java.util.ArrayList;
import java.util.Collection;
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

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader,
			Cursor cursor) {

		int indexArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
		int indexAlbum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
		int indexData = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		int indexTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
		int indexTrack = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);

		HashMap<String, Artist> artistMap = new HashMap<String, Artist>();

		while(cursor.moveToNext()) {
			String albumTitle = cursor.getString(indexAlbum);
			String data = cursor.getString(indexData);
			String songTitle = cursor.getString(indexTitle);
			String trackNumber = cursor.getString(indexTrack);

			String artistName = cursor.getString(indexArtist);
			if( ! artistMap.containsKey(artistName) ) {
				artistMap.put(artistName, new Artist(artistName));
			}

			Artist artist = artistMap.get(artistName);
			artist.addTrack(albumTitle, songTitle, trackNumber, data);
		}

		ListView lv = (ListView) findViewById(R.id.PhoneMusicList);

		this.musicAdapter = new MusicAdapter(this, artistMap);

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
		
		public void addTrack(String albumTitle, String trackTitle, String trackNumber, String data) {
			if( ! this.albums.containsKey(albumTitle) ) {
				this.albums.put(albumTitle, new Album(albumTitle));
			}
			Album album = this.albums.get(albumTitle);
			album.addTrack(trackTitle, data, trackNumber);
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
		
		public void addTrack(String title, String data, String number) {
			this.tracks.add(new Track(title, data, number));
		}
		
		public ArrayList<Track> getTracks() {
			return tracks;
		}
	}
	
	private class Track {
		private String data;
		private String title;
		private String number;
		
		public Track(String title, String data, String number) {
			this.title = title;
			this.data = data;
			this.number = number;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getData() {
			return data;
		}
		
		public String getNumber() {
			return number;
		}
	}
	
	private class MusicAdapter extends BaseAdapter {
		private Context context;
		private Artist[] artists;
		
		public MusicAdapter(Context c, HashMap<String, Artist> artists) {
			this.context = c;
			this.artists = artists.values().toArray(new Artist[artists.size()]);
		}

		public int getCount() {
			return this.artists.length;
		}

		public Object getItem(int position) {
			return this.artists[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.selectmusiclistviewitem, null);
			}

			Artist artist = this.artists[position];
			TextView tv = (TextView) convertView.findViewById(R.id.textview);
			tv.setText(artist.getName());

			return convertView;
		}
	}
}

