package com.strategames.catchdastars.test;

import java.util.HashMap;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
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

	public static final int ADAPTER_COUNT = 9;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 3;
	public static final int AMOUNT_OF_ARTISTS = 4;
	public static final int AMOUNT_OF_ALBUMS = 3;
	public static final int AMOUNT_OF_TRACKS = 8;

	private ListView listview;
	private SelectMusicActivity activity;
	private SelectMusicFragment fragment;
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
				
		this.listview = (ListView) this.activity.findViewById(com.strategames.catchdastars.R.id.listview);
		
		this.fragment = (SelectMusicFragment) this.activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		
		this.activity.setLibrary(library);
		
		HashMap<String, Artist> artists = library.getArtists();
		final CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(activity, artists.values().toArray(new Artist[artists.size()]), activity);
		this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fragment.setAdapter(adapter);
			}
		});
		
		this.solo.waitForFragmentById(R.id.fragment_container);

		//Clear database
		this.dbHelper = new MusicDbHelper(this.activity);
		this.dbHelper.getWritableDatabase();
		this.dbHelper.clearDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	/**
	 * TODO fix race condition where listview has not been setup yet with new adapter.
	 */
	public void test1PreConditions() {
		assertNotNull(this.fragment);
		assertNotNull(this.listview);
		assertNotNull(this.listview.getOnItemClickListener());
		assertTrue(this.listview.getChildCount() > 0);
		View view = this.listview.getChildAt(0);
		assertTrue("ListView item at position 0 is null", view != null);
		assertTrue("ListView item does not contain a TextView identified by R.id.textview", view.findViewById(R.id.textview) instanceof TextView);
		assertTrue("ListView item does not contain a CheckBox identified by R.id.checkbox", view.findViewById(R.id.checkbox) instanceof CheckBox);
		
		//Check that database is really empty
		assertTrue(this.dbHelper.getAll().getArtists().isEmpty());
	}

	public void test2AmountOfItemsShown() {
		testAmountOfItemsShown(AMOUNT_OF_ARTISTS);
		solo.clickInList(1);
		testAmountOfItemsShown(AMOUNT_OF_ALBUMS);
		solo.clickInList(1);
		testAmountOfItemsShown(AMOUNT_OF_TRACKS);
	}
	
	public void test3ListViewContentForArtists() {
		HashMap<String, Artist> artists = this.library.getArtists();
		LibraryItem[] libraryItems = artists.values().toArray(new LibraryItem[artists.size()]);
		assertTrue("Library contains "+libraryItems.length+" artists, but should be "+AMOUNT_OF_ARTISTS, libraryItems.length == AMOUNT_OF_ARTISTS);
		testContentOfItemsShown(libraryItems);
	}
	
	public void test4ListViewContentForAlbum() {
		TextView tv = (TextView) this.listview.getChildAt(1).findViewById(R.id.textview);
		String artistName = tv.getText().toString();
		
		HashMap<String, Album> albums = this.library.getArtist(artistName).getAlbums();
		LibraryItem[] libraryItems = albums.values().toArray(new LibraryItem[albums.size()]);
		assertTrue("Library contains "+libraryItems.length+" albums, but should be "+AMOUNT_OF_ALBUMS, libraryItems.length == AMOUNT_OF_ALBUMS);
		
		//Go into albums view
		solo.clickInList(1);
		testContentOfItemsShown(libraryItems);
	}

	public void test5ListViewContentForTracks() {
		TextView tv = (TextView) this.listview.getChildAt(1).findViewById(R.id.textview);
		String artistName = tv.getText().toString();
		
		//Go into albums view
		solo.clickInList(1);
		tv = (TextView) this.listview.getChildAt(2).findViewById(R.id.textview);
		String albumTitle = tv.getText().toString();
		
		HashMap<String, Track> tracks = this.library.getArtist(artistName).getAlbum(albumTitle).getTracks();
		LibraryItem[] libraryItems = tracks.values().toArray(new LibraryItem[tracks.size()]);
		assertTrue(albumTitle+" for "+artistName+" in library contains "+libraryItems.length+" tracks, but should be "+AMOUNT_OF_TRACKS, libraryItems.length == AMOUNT_OF_TRACKS);
		
		//Go into tracks view
		solo.clickInList(1);
		testContentOfItemsShown(libraryItems);
	}
	
	public void test6SelectArtist() {
		testSelectAllForArtist(0);
		testSelectAllForArtist(3);
	}

	public void test7SelectSingleAlbum() {
		//Select album 2 for artist 1
		solo.clickInList(1);

		assertTrue("Artist1 album2 checkbox not checked", solo.isCheckBoxChecked(2));
	}

	private Library createLibrary() {
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
		//Select artist
		solo.clickOnCheckBox(number);
		//Check if checkbox is selected
		assertTrue("Artist"+number+" checkbox not checked", solo.isCheckBoxChecked(number));

		//Check if all albums are selected
		solo.clickInList(0);
		for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
			assertTrue("Artist"+number+" album"+j+" checkbox not checked", solo.isCheckBoxChecked(j));

			//Check if all tracks are selected
			solo.clickInList(j);
			for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
				assertTrue("Artist"+number+" album"+j+" track"+k+" checkbox not checked", solo.isCheckBoxChecked(k));
			}
			solo.goBack();
		}
		solo.goBack();
	}
	
	private void testAmountOfItemsShown(int shouldBeAmount) {
		int childCount = this.listview.getChildCount();
		assertTrue("Amount of items ("+childCount+") in listview does not equal "+shouldBeAmount, shouldBeAmount == childCount);
	}
	
	private void testContentOfItemsShown(LibraryItem[] items) {
		int amount = this.listview.getChildCount();
		for(int i = 0; i < amount; i++) {
			TextView tv = (TextView) this.listview.getChildAt(i).findViewById(R.id.textview);
			String listViewItemText = tv.getText().toString();
			String nameInLibrary = items[i].getName();
			assertTrue("Content of listview item at position "+i+" is "+listViewItemText+" which does not equal "+nameInLibrary, 
					nameInLibrary.contentEquals(listViewItemText));
		}
	}
}
