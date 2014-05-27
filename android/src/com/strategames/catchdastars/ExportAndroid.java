package com.strategames.catchdastars;

import android.content.Context;
import android.content.Intent;

import com.strategames.catchdastars.interfaces.Exporter;

public class ExportAndroid implements Exporter {

	private Context context;
	private Game game;
	
	public ExportAndroid(Game game, Context context) {
		this.context = context;
		this.game = game;
	}
	
	@Override
	public void export(String text) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.game.getTitle());
		sendIntent.putExtra(Intent.EXTRA_TITLE, this.game.getTitle());
		sendIntent.setType("application/octet-stream");
		context.startActivity(sendIntent);
	}

}
