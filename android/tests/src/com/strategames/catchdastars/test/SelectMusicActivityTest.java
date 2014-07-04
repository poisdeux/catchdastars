package com.strategames.catchdastars.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.strategames.catchdastars.R;
import com.strategames.catchdastars.activities.SelectMusicActivity;
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter;
import com.strategames.engine.musiclibrary.Artist;

public class SelectMusicActivityTest extends ActivityInstrumentationTestCase2<SelectMusicActivity> {

	public static final int ADAPTER_COUNT = 9;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 5;

	private ListView listview;
	private CheckBoxTextViewAdapter adapter;
	private SelectMusicActivity activity;
	private Artist selectedArtist;
	private int mPos;
	  
	public SelectMusicActivityTest() {
		super(SelectMusicActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		this.activity = getActivity();
		this.listview = (ListView) this.activity.findViewById(com.strategames.catchdastars.R.id.listview);
		this.adapter = (CheckBoxTextViewAdapter) this.listview.getAdapter();
	}

	public void testPreConditions() {
		assertNotNull(this.listview);
		assertNotNull(this.listview.getOnItemClickListener());
		assertTrue(this.listview.getChildCount() > 0);
		
		View view = this.listview.getChildAt(0);
		assertNotNull(view);
		assertTrue(view.findViewById(R.id.checkbox) instanceof CheckBox);
		assertTrue(view.findViewById(R.id.textview) instanceof TextView);
	}
	
	public void testListViewUI() {
		this.activity.runOnUiThread(
				new Runnable() {
					public void run() {
						listview.requestFocus();
						listview.setSelection(INITIAL_POSITION);
						
						listview.getChildAt(0);
					} 
				}
				);

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		for (int i = 1; i <= TEST_POSITION; i++) {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		} // end of for loop

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

		mPos = this.listview.getSelectedItemPosition();
		selectedArtist = (Artist) this.listview.getItemAtPosition(mPos);

		//assertEquals(resultText,mSelection);

	}
}
