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

		CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(this, this.musicLibrary.getArtistNames(), this);
		this.fragment.setAdapter(adapter);
	}


	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {

	}


	@Override
	public void onCheckBoxChanged(Media item, boolean isChecked) {
		Artist artist;
		Album album;
		switch( this.fragment.getState() ) {
		case ARTISTS:
			artist = this.musicLibrary.get().get(item);
			selectArtist(artist, isChecked);
			break;
		case ALBUMS:
			artist = this.musicLibrary.getSelectedArtist();
			album = artist.getAlbums().get(item);
			selectAlbum(album, artist.getName(), isChecked);
			break;
		case TRACKS:
			artist = this.musicLibrary.getSelectedArtist();
			album = artist.getSelectedAlbum();
			Track track = album.getTracks().get(item);
			selectTrack(track, artist.getName(), album.getName(), isChecked);
			break;
		}
	}

	@Override
	public void onItemClicked(String item) {
		Artist selectedArtist;

		switch( this.fragment.getState() ) {
		case ARTISTS:
			selectedArtist = this.musicLibrary.get(item);
			this.musicLibrary.setSelectedArtist(selectedArtist);

			HashMap<String, Album> albums = selectedArtist.getAlbums();

			String[] albumNames = albums.keySet().toArray(new String[albums.size()]);

			replaceFragment(albumNames, SelectMusicFragment.STATE.ALBUMS);
			break;
		case ALBUMS:
			selectedArtist = this.musicLibrary.getSelectedArtist();
			Album album = selectedArtist.getAlbums().get(item);

			selectedArtist.setSelectedAlbum(album);

			HashMap<String, Track> tracks = album.getTracks();

			String[] trackNames = tracks.keySet().toArray(new String[tracks.size()]);

			replaceFragment(trackNames, SelectMusicFragment.STATE.TRACKS);
			break;
		case TRACKS:
			// play track as a preview?
			break;
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

