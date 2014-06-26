package com.strategames.catchdastars.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
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
import com.strategames.catchdastars.fragments.SelectMusicFragment;
import com.strategames.catchdastars.music.Album;
import com.strategames.catchdastars.music.Artist;
import com.strategames.catchdastars.music.Library;
import com.strategames.catchdastars.music.Track;



public class SelectMusicActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, 
SelectMusicFragment.OnItemSelectedListener {
	public static final String BUNDLE_KEY_MUSICLIST = "bundle_key_musiclist";

	private ArrayList<String> selectedFiles;
	private Library musicLibrary;
	private SelectMusicFragment fragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmusicactivity);

		this.selectedFiles = new ArrayList<String>();
		this.musicLibrary = new Library();
		
		this.fragment = new SelectMusicFragment();
		this.fragment.setState(SelectMusicFragment.STATE.ARTISTS);
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
		
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
	public void onCheckBoxChanged(int position, boolean isChecked) {

		//		if( isChecked ) {
		//			this.selectedFiles.add(position);
		//		} else {
		//			this.selectedFiles.remove(position);
		//		}
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
			
			ArrayList<Track> tracks = selectedArtist.getAlbums().get(item).getTracks();
			String[] trackNames = new String[tracks.size()];
			for(int i = 0; i < trackNames.length; i++) {
				trackNames[i] = tracks.get(i).getName();
			}
			
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
	
	private void replaceFragment(String[] items, SelectMusicFragment.STATE state) {
		this.fragment = new SelectMusicFragment();
		this.fragment.setState(state);
		
		CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(this, items, this);
		this.fragment.setAdapter(adapter);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
	}
}

