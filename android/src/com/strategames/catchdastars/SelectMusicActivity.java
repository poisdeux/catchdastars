package com.strategames.catchdastars;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class SelectMusicActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, 
OnItemClickListener {
	public static final String BUNDLE_KEY_MUSICLIST = "bundle_key_musiclist";

	private MusicAdapter musicAdapter;
	private ArrayList<String> selectedFiles;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmusicactivity);
		
		this.selectedFiles = new ArrayList<String>();
		
		getSupportLoaderManager().initLoader(0, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE };

		return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);
	}

	public class MusicAdapter extends BaseAdapter {
		private Context context;
		private Cursor cursor;

		public MusicAdapter(Context c, Cursor cursor) {
			this.context = c;
			this.cursor = cursor;
		}

		public int getCount() {
			return cursor.getCount();
		}

		public Object getItem(int position) {
			cursor.moveToPosition(position);
			return cursor;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(context);
			String id = null;
			if (convertView == null) {
				int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
				cursor.moveToPosition(position);
				id = cursor.getString(index);
				index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
				id += " Size(KB):" + cursor.getString(index);
				tv.setText(id);
			} else
				tv = (TextView) convertView;
			return tv;
		}
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader,
			Cursor cursor) {

		ListView lv = (ListView) findViewById(R.id.PhoneMusicList);

		this.musicAdapter = new MusicAdapter(this, cursor);
		
		lv.setAdapter(this.musicAdapter);

		lv.setOnItemClickListener(this);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor cursor = (Cursor) this.musicAdapter.getItem(position);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		this.selectedFiles.add(cursor.getString(index));
	}
}

