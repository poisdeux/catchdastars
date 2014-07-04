package com.strategames.catchdastars.test;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
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
		this.listview = (ListView) this.activity.findViewById(com.strategames.catchdastars.R.id.listview);
		this.fragment = (SelectMusicFragment) this.activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		
		//Clear database
		this.dbHelper = new MusicDbHelper(this.activity);
		this.dbHelper.getWritableDatabase();
		this.dbHelper.clearDatabase();
		
		this.solo = new Solo(getInstrumentation(), this.activity);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		solo.finishOpenedActivities();
	}

	public void testPreConditions() {
		assertNotNull(this.listview);
		assertNotNull(this.listview.getOnItemClickListener());
		assertTrue(this.listview.getChildCount() > 0);
		
		View view = this.listview.getChildAt(0);
		assertNotNull(view);
		assertTrue(view.findViewById(R.id.checkbox) instanceof CheckBox);
		assertTrue(view.findViewById(R.id.textview) instanceof TextView);
		
		//Check that database is really empty
		assertTrue(this.dbHelper.getAll().getArtists().isEmpty());
	}
	
	public void testListViewUI() {
		this.library = createLibrary();
		
		this.activity.runOnUiThread(new Runnable() {
			HashMap<String, Artist> artists = library.getArtists();
			@Override
			public void run() {
				CheckBoxTextViewAdapter adapter = new CheckBoxTextViewAdapter(activity, artists.values().toArray(new Artist[artists.size()]), activity);
				fragment.setAdapter(adapter);
			}
		});
		
		
		testSelectAllForArtist(0);
		testSelectAllForArtist(3);
		
		//Select album 2 for artist 1
		solo.clickInList(1);
		solo.clickOnCheckBox(2);
		if(solo.isCheckBoxChecked(2)) {
			fail("Artist1 album2 checkbox not checked");
		}
		
		solo.sleep(5000);
	}
	
	private Library createLibrary() {
		Library library = new Library();
		for(int i = 0; i < AMOUNT_OF_ARTISTS; i++) {
			Artist artist = new Artist("artist"+i);
			for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
				Album album = new Album("album"+j, artist);
				artist.addAlbum(album);
				for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
					Track track = new Track("track"+k, "/opt/storage/"+album.getName()+"/track"+k, ""+k, album);
					album.addTrack(track);
					this.activity.addTrack("artist"+i, "album"+j, "track"+k, ""+k, "/opt/storage/album"+j+"/track"+k);
				}
			}
			library.addArtist(artist);
		}
		
		return library;
	}
	
	private void testSelectAllForArtist(int number) {
		//Select artist
		solo.clickOnCheckBox(number);
		//Check if checkbox is selected
		if(solo.isCheckBoxChecked(number)) {
			fail("Artist"+number+" checkbox not checked");
		}
		
		//Check if all albums are selected
		solo.clickInList(0);
		for(int j = 0; j < AMOUNT_OF_ALBUMS; j++) {
			assertTrue(solo.isCheckBoxChecked(j));
			if(solo.isCheckBoxChecked(number)) {
				fail("Artist"+number+" album"+j+" checkbox not checked");
			}
			
			//Check if all tracks are selected
			solo.clickInList(j);
			for(int k = 0; k < AMOUNT_OF_TRACKS; k++) {
				assertTrue(solo.isCheckBoxChecked(k));
				if(solo.isCheckBoxChecked(number)) {
					fail("Artist"+number+" album"+j+" track"+k+" checkbox not checked");
				}
			}
			solo.goBack();
		}
		solo.goBack();
	}
}
