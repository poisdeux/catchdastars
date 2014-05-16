package com.strategames.catchdastars;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.strategames.catchdastars.interfaces.Importer;

public class MainActivity extends AndroidApplication implements Importer {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useAccelerometer = true;
        cfg.useCompass = false;
        
        CatchDaStars game = new CatchDaStars();
        game.setExporter(new ExportAndroid(game, this));
        game.setImporter(this);
        
        initialize(game, cfg);
    }

	@Override
	public void importLevels() {
		Log.d("MainActivity", "importLevels");
		startActivity(new Intent(this, ImportAndroidActivity.class));
	}
}