package com.strategames.catchdastars.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.strategames.catchdastars.game.CatchDaStars;
import com.strategames.catchdastars.database.MusicDbHelper;
import com.strategames.engine.interfaces.ExportImport;
import com.strategames.engine.interfaces.MusicSelector;
import com.strategames.engine.interfaces.OnLevelsReceivedListener;
import com.strategames.engine.interfaces.OnMusicFilesReceivedListener;
import com.strategames.engine.musiclibrary.Library;

public class MainActivity extends AndroidApplication implements ExportImport, MusicSelector {

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
		game.setExporterImporter(this);
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
			break;
		case REQUEST_CODE_SELECTMUSIC:
			if( ( resultCode == Activity.RESULT_OK ) ) {
				this.onMusicFilesReceivedListener.onMusicFilesReceived();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void selectMusic(OnMusicFilesReceivedListener listener) {
		this.onMusicFilesReceivedListener = listener;
		startActivityForResult(new Intent(this, SelectMusicActivity.class), REQUEST_CODE_SELECTMUSIC);
	}

	@Override
	public Library getLibrary() {
		MusicDbHelper helper = new MusicDbHelper(this);
		helper.getWritableDatabase();
		Library library = helper.getAll();
		helper.close();
		return library;
	}

	@Override
	public void export(String text) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, getTitle());
		sendIntent.putExtra(Intent.EXTRA_TITLE, getTitle());
		sendIntent.setType("application/octet-stream");
		startActivity(sendIntent);
	}
}