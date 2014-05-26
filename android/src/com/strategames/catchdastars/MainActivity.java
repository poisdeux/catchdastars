package com.strategames.catchdastars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.strategames.catchdastars.interfaces.Importer;
import com.strategames.catchdastars.interfaces.OnLevelsReceivedListener;

public class MainActivity extends AndroidApplication implements Importer {

	private OnLevelsReceivedListener listener;

	private static final int REQUEST_CODE_IMPORT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useCompass = false;
		
		CatchDaStars game = new CatchDaStars();
		game.setExporter(new ExportAndroid(game, this));
		game.setImporter(this);

		initialize(game, cfg);
	}

	@Override
	public void importLevels(OnLevelsReceivedListener listener) {
		Log.d("MainActivity", "importLevels");
		this.listener = listener;
		startActivityForResult(new Intent(this, ImportAndroidActivity.class), REQUEST_CODE_IMPORT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("MainActivity", "onActivityResult: requestCode="+requestCode+", resultCode="+resultCode);
		switch (requestCode) {
		case REQUEST_CODE_IMPORT:
			String json = null;
			
			if( ( resultCode == Activity.RESULT_OK ) && ( data != null ) ) {
				Bundle b = data.getExtras();
				if( b != null ) {
					json = b.getString(ImportAndroidActivity.BUNDLE_KEY_JSON);
				}
			}
			
			if( this.listener != null ) {
				this.listener.levelsReceived(json);
			}
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}