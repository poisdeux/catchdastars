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

package com.strategames.catchdastars.test;


import java.util.HashMap;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.strategames.catchdastars.R;
import com.strategames.catchdastars.activities.SelectMusicActivity;
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter;
import com.strategames.catchdastars.database.MusicDbHelper;
import com.strategames.catchdastars.fragments.SelectMusicFragment;
import com.strategames.engine.musiclibrary.Album;
import com.strategames.engine.musiclibrary.Artist;
import com.strategames.engine.musiclibrary.Library;
import com.strategames.engine.musiclibrary.LibraryItem;
import com.strategames.engine.musiclibrary.Track;

public class SelectMusicActivityTest extends ActivityInstrumentationTestCase2<SelectMusicActivity> {

	public static final int AMOUNT_OF_ARTISTS = 4;
	public static final int AMOUNT_OF_ALBUMS = 3;
	public static final int AMOUNT_OF_TRACKS = 8;

	private String ARTIST0_NAME;
	private String ALBUM0_NAME;
	private String TRACK0_NAME;

	private SelectMusicActivity activity;
	private MusicDbHelper dbHelper;
	private Library library;

	private Solo solo;

	public SelectMusicActivityTest() {
		super(SelectMusicActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);

		this.activity = getActivity();

		this.solo = new Solo(getInstrumentation(), this.activity);

		this.library = createLibrary();

		this.activity.setLibrary(library);

		final SelectMusicFragment fragment = (SelectMusicFragment) this.activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);

		HashMap<String, Artist> artists = library.getArtists();
		final CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(activity, artists.values().toArray(new Artist[artists.size()]), activity);
		this.activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				fragment.setAdapter(adapter);
			}
		});

		//Clear database
		this.dbHelper = new MusicDbHelper(this.activity);
		this.dbHelper.getWritableDatabase();
		this.dbHelper.clearDatabase();

		waitForArtistList();
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	@MediumTest
	public void test1PreConditions() {
		ListView listview = getListView();

		View view = listview.getChildAt(0);
		assertTrue("ListView item at position 0 is null", view != null);
		assertTrue("ListView item does not contain a TextView identified by R.id.textview", view.findViewById(R.id.textview) instanceof TextView);
		assertTrue("ListView item does not contain a CheckBox identified by R.id.checkbox", view.findViewById(R.id.checkbox) instanceof CheckBox);

		//Check that database is really empty
		assertTrue(this.dbHelper.getAll().getArtists().isEmpty());

		/**
		 * Check none of the items are checked
		 * Note we only do this here as it takes a long time to complete
		 * We assume (yeah i know) that if it succeeds here all tests will
		 * start with a clean unchecked setup
		 */
		testIfAllItemsAreUnchecked();
	}

	@SmallTest
	public void test2AmountOfItemsShown() {
		testAmountOfItemsShown(AMOUNT_OF_ARTISTS);

		solo.clickInList(1);
		waitForAlbumList();
		testAmountOfItemsShown(AMOUNT_OF_ALBUMS);

		solo.clickInList(1);
		waitForTrackList();
		testAmountOfItemsShown(AMOUNT_OF_TRACKS);
	}

	@SmallTest
	public void test3ListViewContentForArtists() {
		HashMap<String, Artist> artists = this.library.getArtists();
		LibraryItem[] libraryItems = artists.values().toArray(new LibraryItem[artists.size()]);
		assertTrue("Library contains "+libraryItems.length+" artists, but should be "+AMOUNT_OF_ARTISTS, libraryItems.length == AMOUNT_OF_ARTISTS);
		testContentOfItemsShown(libraryItems);
	}

	@SmallTest
	public void test4ListViewContentForAlbum() {
		ListView listview = getListView();
		TextView tv = (TextView) listview.getChildAt(1).findViewById(R.id.textview);
		String artistName = tv.getText().toString();

		HashMap<String, Album> albums = this.library.getArtist(artistName).getAlbums();
		LibraryItem[] libraryItems = albums.values().toArray(new LibraryItem[albums.size()]);
		assertTrue("Library contains "+libraryItems.length+" albums, but should be "+AMOUNT_OF_ALBUMS, libraryItems.length == AMOUNT_OF_ALBUMS);

		//Go into albums view
		solo.clickInList(1);
		waitForAlbumList();
		testContentOfItemsShown(libraryItems);
	}

	@SmallTest
	public void test5ListViewContentForTracks() {
		ListView listview = getListView();
		TextView tv = (TextView) listview.getChildAt(1).findViewById(R.id.textview);
		String artistName = tv.getText().toString();

		//Go into albums view
		solo.clickInList(1);
		waitForAlbumList();
		listview = getListView();
		tv = (TextView) listview.getChildAt(2).findViewById(R.id.textview);
		String albumTitle = tv.getText().toString();

		HashMap<String, Track> tracks = this.library.getArtist(artistName).getAlbum(albumTitle).getTracks();
		LibraryItem[] libraryItems = tracks.values().toArray(new LibraryItem[tracks.size()]);
		assertTrue(albumTitle+" for "+artistName+" in library contains "+libraryItems.length+" tracks, but should be "+AMOUNT_OF_TRACKS, libraryItems.length == AMOUNT_OF_TRACKS);

		//Go into tracks view
		solo.clickInList(1);
		waitForTrackList();
		testContentOfItemsShown(libraryItems);
	}

	@MediumTest
	public void test6SelectArtist() {
		testSelectAllForArtist(0);
		testSelectAllForArtist(3);
	}

	@SmallTest
	public void test7SelectSingleAlbum() {
		assertFalse("Artist1 is checked", solo.isCheckBoxChecked(1));
		//Select album 2 for artist 1
		solo.clickInList(1);
		waitForAlbumList();
		assertFalse("Album2 is checked", solo.isCheckBoxChecked(2));
		solo.goBack();
		waitForArtistList();

		//Select album 2 for artist 1
		solo.clickInList(1);
		waitForAlbumList();
		solo.clickOnCheckBox(2);
		assertTrue("Artist1 album2 checkbox not checked", solo.isCheckBoxChecked(2));
		//Check that artist is selected
		solo.goBack();

	}

	private Library createLibrary() {
		ARTIST0_NAME = "artist0";
		ALBUM0_NAME = "album0";
		TRACK0_NAME = "track0";

		Library library = new Library();
		for(int i = 0; i < AMOUNT_OF_ARTISTS; i++) {
			for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
				for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
					library.addTrack("artist"+i, "album"+j, "track"+k, ""+k, "/opt/storage/album"+j+"/track"+k);
				}
			}
		}
		return library;
	}

	private void testSelectAllForArtist(int number) {
		waitForArtistList();

		//Select artist
		solo.clickOnCheckBox(number);
		//Check if checkbox is selected
		assertTrue("Artist"+number+" checkbox not checked", solo.isCheckBoxChecked(number));

		//Check if all albums and tracks are selected
		solo.clickInList(0);
		waitForAlbumList();
		for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
			assertTrue("Artist"+number+" album"+j+" checkbox not checked", solo.isCheckBoxChecked(j));

			//Check if all tracks are selected
			solo.clickInList(j);
			waitForTrackList();
			for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
				assertTrue("Artist"+number+" album"+j+" track"+k+" checkbox not checked", solo.isCheckBoxChecked(k));
			}
			solo.goBack();
		}
		solo.goBack();
	}

	private void testAmountOfItemsShown(int shouldBeAmount) {
		ListView listview = getListView();
		int childCount = listview.getChildCount();
		assertTrue("Amount of items ("+childCount+") in listview does not equal "+shouldBeAmount, shouldBeAmount == childCount);
	}

	private void testContentOfItemsShown(LibraryItem[] items) {
		ListView listview = getListView();
		int amount = listview.getChildCount();
		for(int i = 0; i < amount; i++) {
			TextView tv = (TextView) listview.getChildAt(i).findViewById(R.id.textview);
			String listViewItemText = tv.getText().toString();
			String nameInLibrary = items[i].getName();
			assertTrue("Content of listview item at position "+i+" is "+listViewItemText+" which does not equal "+nameInLibrary, 
					nameInLibrary.contentEquals(listViewItemText));
		}
	}

	private void testIfAllItemsAreUnchecked() {
		waitForArtistList();

		for(int artistNumber = 0; artistNumber < AMOUNT_OF_ARTISTS; artistNumber++) {
			//Check if none of the albums and tracks are selected
			solo.clickInList(artistNumber);
			waitForAlbumList();
			for(int albumNumber = 0; albumNumber < AMOUNT_OF_ALBUMS; albumNumber++) {
				assertFalse("Artist"+artistNumber+" album"+albumNumber+" checkbox checked", solo.isCheckBoxChecked(albumNumber));

				//Check if all tracks are selected
				solo.clickInList(albumNumber);
				waitForTrackList();
				for(int trackNumber = 0; trackNumber < AMOUNT_OF_TRACKS; trackNumber++) {
					assertFalse("Artist"+artistNumber+" album"+albumNumber+" track"+trackNumber+" checkbox checked", solo.isCheckBoxChecked(trackNumber));
				}
				solo.goBack();
			}
			solo.goBack();
		}
	}

	private ListView getListView() {
		ListView listview = (ListView) this.activity.findViewById(com.strategames.catchdastars.R.id.listview);
		assertNotNull("ListView in activity is null", listview);
		assertNotNull("ListView has no OnItemClickListener connected", listview.getOnItemClickListener());
		assertTrue("ListView has no rows", listview.getChildCount() > 0);
		return listview;
	}

	private void waitForArtistList() {
		assertTrue(solo.waitForText(ARTIST0_NAME, 1, 5000));
	}

	private void waitForAlbumList() {
		assertTrue(solo.waitForText(ALBUM0_NAME, 1, 5000));
	}

	private void waitForTrackList() {
		assertTrue(solo.waitForText(TRACK0_NAME, 1, 5000));
	}
}
