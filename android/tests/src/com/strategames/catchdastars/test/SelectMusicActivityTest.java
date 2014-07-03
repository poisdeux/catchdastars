package com.strategames.catchdastars.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.strategames.catchdastars.activities.SelectMusicActivity;
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter;

public class SelectMusicActivityTest extends ActivityInstrumentationTestCase2<SelectMusicActivity> {

	private ListView listview;
	private CheckBoxTextViewAdapter adapter;
	private Activity activity;
	
	public SelectMusicActivityTest() {
		super(SelectMusicActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
