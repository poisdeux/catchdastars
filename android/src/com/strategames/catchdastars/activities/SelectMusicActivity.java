package com.strategames.catchdastars.activities;

import java.util.Collection;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.strategames.catchdastars.R;
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter;
import com.strategames.catchdastars.database.MusicDbHelper;
import com.strategames.catchdastars.fragments.SelectMusicFragment;
import com.strategames.catchdastars.music.Album;
import com.strategames.catchdastars.music.Artist;
import com.strategames.catchdastars.music.Library;
import com.strategames.catchdastars.music.Media;
import com.strategames.catchdastars.music.Track;



public class SelectMusicActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, 
SelectMusicFragment.OnItemSelectedListener {
	public static final String BUNDLE_KEY_MUSICLIST = "bundle_key_musiclist";

	private Library musicLibrary;
	private SelectMusicFragment fragment;

	private MusicDbHelper musicDbHelper;
	private SQLiteDatabase sqliteDatabase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmusicactivity);

		this.musicLibrary = new Library();
		this.musicDbHelper = new MusicDbHelper(this);

		this.fragment = new SelectMusicFragment();
		this.fragment.setState(SelectMusicFragment.STATE.ARTISTS);
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();

		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.sqliteDatabase = this.musicDbHelper.getWritableDatabase();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.sqliteDatabase.close();
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TRACK,
				MediaStore.Audio.Media.TITLE };

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

		while(cursor.moveToNext()) {
			String albumTitle = cursor.getString(indexAlbum);
			String data = cursor.getString(indexData);
			String trackTitle = cursor.getString(indexTitle);
			String trackNumber = cursor.getString(indexTrack);

			String artistName = cursor.getString(indexArtist);

			Artist artist = this.musicLibrary.get(artistName);
			if( artist == null ) {
				artist = new Artist(artistName);
				this.musicLibrary.add(artist);
			}
			artist.addTrack(albumTitle, trackTitle, trackNumber, data);
		}

		this.fragment.setState(SelectMusicFragment.STATE.ARTISTS);

		HashMap<String, Artist> artistMap = this.musicLibrary.get();
		Artist[] artists = artistMap.values().toArray(new Artist[artistMap.size()]);
		
		CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(this, artists, this);
		this.fragment.setAdapter(adapter);
	}


	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {

	}


	@Override
	public void onCheckBoxChanged(Media item, boolean isChecked) {
		Artist artist;
		Album album;
		if(item instanceof Artist) {
			selectArtist((Artist) item, isChecked);
		} else if(item instanceof Album) {
			artist = this.musicLibrary.getSelectedArtist();
			selectAlbum((Album) item, artist.getName(), isChecked);
		} else if(item instanceof Track) {
			artist = this.musicLibrary.getSelectedArtist();
			album = artist.getSelectedAlbum();
			selectTrack((Track) item, artist.getName(), album.getName(), isChecked);
		}
	}

	@Override
	public void onItemClicked(Media item) {
		Artist selectedArtist;

		if(item instanceof Artist) {
			selectedArtist = (Artist) item;
			
			this.musicLibrary.setSelectedArtist(selectedArtist);

			HashMap<String, Album> albums = selectedArtist.getAlbums();

			Album[] albumNames = albums.values().toArray(new Album[albums.size()]);

			replaceFragment(albumNames, SelectMusicFragment.STATE.ALBUMS);
		} else if(item instanceof Album) {
			selectedArtist = this.musicLibrary.getSelectedArtist();
			
			Album album = (Album) item;

			selectedArtist.setSelectedAlbum(album);

			HashMap<String, Track> tracks = album.getTracks();

			Track[] trackNames = tracks.values().toArray(new Track[tracks.size()]);

			replaceFragment(trackNames, SelectMusicFragment.STATE.TRACKS);
		}  else if(item instanceof Track) {
			// play track as a preview?
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FragmentManager manager = getSupportFragmentManager();
		this.fragment = (SelectMusicFragment) manager.findFragmentById(R.id.fragment_container);
	}

	private void replaceFragment(Media[] items, SelectMusicFragment.STATE state) {
		this.fragment = new SelectMusicFragment();
		this.fragment.setState(state);

		CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(this, items, this);
		this.fragment.setAdapter(adapter);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
	}

	private void selectArtist(Artist artist, boolean select) {
		Collection<Album> albums = artist.getAlbums().values();
		String artistName = artist.getName();
		artist.setSelected(select);
		//select all albums
		for(Album album : albums) {
			selectAlbum(album, artistName, select);
		}
	}

	private void selectAlbum(Album album, String artistName, boolean select) {
		Collection<Track> tracks = album.getTracks().values();
		String albumTitle = album.getName();
		album.setSelected(select);
		for(Track track : tracks) {
			selectTrack(track, artistName, albumTitle, select);
		}
	}

	private void selectTrack(Track track, String artistName, String albumTitle, boolean select) {
		if( select ) {
			track.setSelected(true);
			this.musicDbHelper.addSong(this.sqliteDatabase, artistName, albumTitle, 
					track.getName(), track.getNumber(), track.getData());
		} else {
			track.setSelected(false);
			this.musicDbHelper.deleteSong(this.sqliteDatabase, artistName, albumTitle, 
					track.getName(), track.getNumber(), track.getData());
		}
	}
}

