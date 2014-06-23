package com.strategames.catchdastars;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.strategames.catchdastars.interfaces.Importer;
import com.strategames.catchdastars.interfaces.OnLevelsReceivedListener;
import com.strategames.catchdastars.interfaces.OnMusicFilesReceivedListener;
import com.strategames.catchdastars.interfaces.MusicSelector;

public class MainActivity extends AndroidApplication implements Importer, MusicSelector {

	private OnLevelsReceivedListener onLevelsReceivedListener;
	private OnMusicFilesReceivedListener onMusicFilesReceivedListener;
	
	private static final int REQUEST_CODE_IMPORT = 1;
	private static final int REQUEST_CODE_SELECTMUSIC = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useCompass = false;
		
		CatchDaStars game = new CatchDaStars();
		game.setExporter(new ExportAndroid(game, this));
		game.setImporter(this);
		game.setMusicSelector(this);
		initialize(game, cfg);
	}

	@Override
	public void importLevels(OnLevelsReceivedListener listener) {
		this.onLevelsReceivedListener = listener;
		startActivityForResult(new Intent(this, ImportAndroidActivity.class), REQUEST_CODE_IMPORT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_IMPORT:
			String json = null;
			
			if( ( resultCode == Activity.RESULT_OK ) && ( data != null ) ) {
				Bundle b = data.getExtras();
				if( b != null ) {
					json = b.getString(ImportAndroidActivity.BUNDLE_KEY_JSON);
				}
			}
			
			if( this.onLevelsReceivedListener != null ) {
				this.onLevelsReceivedListener.levelsReceived(json);
			}
		case REQUEST_CODE_SELECTMUSIC:
			if( ( resultCode == Activity.RESULT_OK ) && ( data != null ) ) {
				ArrayList<String> musicList = null;
				Bundle b = data.getExtras();
				if( b != null ) {
					musicList = b.getStringArrayList(SelectMusicActivity.BUNDLE_KEY_MUSICLIST);
				}
				this.onMusicFilesReceivedListener.onMusicFilesReceived(musicList);
			}
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void selectMusic(OnMusicFilesReceivedListener listener) {
		this.onMusicFilesReceivedListener = listener;
		startActivityForResult(new Intent(this, SelectMusicActivity.class), REQUEST_CODE_IMPORT);
	}
}