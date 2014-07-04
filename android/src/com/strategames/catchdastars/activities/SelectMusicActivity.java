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
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter.OnCheckboxChangedListener;
import com.strategames.catchdastars.database.MusicDbHelper;
import com.strategames.catchdastars.fragments.SelectMusicFragment;
import com.strategames.catchdastars.fragments.SelectMusicFragment.SelectMusicFragmentListener;
import com.strategames.engine.musiclibrary.Album;
import com.strategames.engine.musiclibrary.Artist;
import com.strategames.engine.musiclibrary.Library;
import com.strategames.engine.musiclibrary.LibraryItem;
import com.strategames.engine.musiclibrary.Track;



public class SelectMusicActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, 
SelectMusicFragmentListener, OnCheckboxChangedListener {
	public static final String BUNDLE_KEY_MUSICLIST = "bundle_key_musiclist";

	private Library musicOnDeviceLibrary;
	private Library musicInDatabase;
	private SelectMusicFragment fragment;

	private MusicDbHelper musicDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmusicactivity);

		this.musicOnDeviceLibrary = new Library();
		this.musicDbHelper = new MusicDbHelper(this);

		this.fragment = new SelectMusicFragment();
		this.fragment.setState(SelectMusicFragment.STATE.ARTISTS);
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();

		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.musicDbHelper.getWritableDatabase();
		this.musicInDatabase = this.musicDbHelper.getAll();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.musicDbHelper.close();
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
			String trackPath = cursor.getString(indexData);
			String trackTitle = cursor.getString(indexTitle);
			String trackNumber = cursor.getString(indexTrack);
			String artistName = cursor.getString(indexArtist);

			addTrack(artistName, albumTitle, trackTitle, trackNumber, trackPath);
		}

		this.fragment.setState(SelectMusicFragment.STATE.ARTISTS);

		HashMap<String, Artist> artistMap = this.musicOnDeviceLibrary.getArtists();
		Artist[] artists = artistMap.values().toArray(new Artist[artistMap.size()]);

		CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(this, artists, this);
		this.fragment.setAdapter(adapter);
	}


	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {

	}

	@Override
	public void onCheckBoxChanged(CheckBoxTextViewAdapter adapter, LibraryItem item, boolean isChecked) {
		Artist artist;
		Album album;
		if(item instanceof Artist) {
			selectArtist((Artist) item, isChecked);
		} else if(item instanceof Album) {
			album = (Album) item;
			artist = album.getArtist();

			selectAlbum(album, artist, isChecked);

			//update parent selected state
			if( isChecked ) {
				artist.setSelected(isChecked);
			} else {
				boolean itemSelected = itemSelected(adapter.getItems());
				artist.setSelected(itemSelected);
			}

		} else if(item instanceof Track) {
			album = ((Track) item).getAlbum();
			artist = album.getArtist();
			selectTrack((Track) item, artist, album, isChecked);

			//update parent selected state
			if( isChecked ) {
				album.setSelected(true);
				artist.setSelected(true);
			} else {
				boolean itemSelected = itemSelected(adapter.getItems());
				album.setSelected(itemSelected);
				if( itemSelected ) {
					artist.setSelected(true);
				} else {
					Collection<Album> albums = artist.getAlbums().values();
					itemSelected = itemSelected(albums.toArray(new LibraryItem[albums.size()]));
					artist.setSelected(itemSelected);
				}
			}
		}
	}

	@Override
	public void onItemClicked(LibraryItem item) {
		Artist artist;

		if(item instanceof Artist) {
			artist = (Artist) item;

			HashMap<String, Album> albums = artist.getAlbums();

			Album[] albumNames = albums.values().toArray(new Album[albums.size()]);

			replaceFragment(albumNames, SelectMusicFragment.STATE.ALBUMS);
		} else if(item instanceof Album) {
			artist = ((Album) item).getArtist();

			Album album = (Album) item;

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


	private void replaceFragment(LibraryItem[] items, SelectMusicFragment.STATE state) {
		this.fragment = new SelectMusicFragment();
		this.fragment.setState(state);

		CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(this, items, this);
		this.fragment.setAdapter(adapter);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
	}

	/**
	 * Marks the artist as selected and if recursive is set to true will mark
	 * all albums and tracks as selected as well.
	 * @param artist artist to select
	 * @param select true to set selected false to set not-selected
	 * @param recursive true if all albums and tracks should be set to select as well
	 */
	private void selectArtist(Artist artist, boolean select) {
		Collection<Album> albums = artist.getAlbums().values();
		artist.setSelected(select);

		//select all albums
		for(Album album : albums) {
			selectAlbum(album, artist, select);
		}
	}

	/**
	 * Marks the album as selected and if recursive is set to true will mark
	 * all tracks as selected as well
	 * @param album album to select
	 * @param artist the artist of this album
	 * @param select true if album should be selected, false otherwise
	 * @param recursive true to also set all tracks to select as well
	 */
	private void selectAlbum(Album album, Artist artist, boolean select) {
		Collection<Track> tracks = album.getTracks().values();
		album.setSelected(select);

		for(Track track : tracks) {
			selectTrack(track, artist, album, select);
		}

	}

	private void selectTrack(Track track, Artist artist, Album album, boolean select) {
		if( select ) {
			track.setSelected(true);
			this.musicDbHelper.addSong(artist.getName(), album.getName(), 
					track.getName(), track.getNumber(), track.getData());
		} else {
			track.setSelected(false);
			this.musicDbHelper.deleteSong(artist.getName(), album.getName(), 
					track.getName(), track.getNumber(), track.getData());
		}
	}

	private boolean itemSelected(LibraryItem[] items) {
		boolean itemSelected = false;
		for(LibraryItem libraryItem : items) {
			if( libraryItem.isSelected() ) {
				itemSelected = true;
				break;
			}
		}
		return itemSelected;
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
		Artist artist = this.musicOnDeviceLibrary.getArtist(artistName);
		if( artist == null ) {
			artist = new Artist(artistName);
			this.musicOnDeviceLibrary.addArtist(artist);
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

		if( trackInDatabase(artistName, albumTitle, trackTitle) ) {
			artist.setSelected(true);
			album.setSelected(true);
			track.setSelected(true);
		}
	}

	private boolean trackInDatabase(String artistName, String albumTitle, String trackTitle) {
		Artist artistDatabase = this.musicInDatabase.getArtist(artistName);
		if( artistDatabase != null ) {
			Album albumDatabase = artistDatabase.getAlbum(albumTitle);
			if( albumDatabase != null ) {
				if( albumDatabase.getTrack(trackTitle) != null ) {
					return true;
				}
			}
		}
		return false;
	}
}

