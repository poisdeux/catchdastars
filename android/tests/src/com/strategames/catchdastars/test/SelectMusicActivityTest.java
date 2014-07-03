package com.strategames.catchdastars.test;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.ListView;

import com.strategames.catchdastars.activities.SelectMusicActivity;
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter;
import com.strategames.catchdastars.database.MusicDbHelper;

public class SelectMusicActivityTest extends ActivityInstrumentationTestCase2<SelectMusicActivity> {

	public static final int ADAPTER_COUNT = 9;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 5;

	private ListView listview;
	private CheckBoxTextViewAdapter adapter;
	private SelectMusicActivity activity;
	private String mSelection;
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
		assertTrue(this.listview.getOnItemSelectedListener() != null);
		assertTrue(this.adapter != null);
		assertEquals(this.adapter.getCount(), ADAPTER_COUNT);
	}
	
	public void testListViewUI() {
		this.activity.runOnUiThread(
				new Runnable() {
					public void run() {
						listview.requestFocus();
						listview.setSelection(INITIAL_POSITION);
					} 
				}
				);

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		for (int i = 1; i <= TEST_POSITION; i++) {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		} // end of for loop

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

		mPos = this.listview.getSelectedItemPosition();
		mSelection = (String) this.listview.getItemAtPosition(mPos);

		//assertEquals(resultText,mSelection);

	}
}
