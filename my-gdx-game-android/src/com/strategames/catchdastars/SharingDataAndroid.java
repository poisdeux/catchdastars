package com.strategames.catchdastars;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.strategames.catchdastars.interfaces.ExportData;

public class SharingDataAndroid implements ExportData {

	private Context context;
	private Game game;
	
	public SharingDataAndroid(Game game, Context context) {
		this.context = context;
		this.game = game;
	}
	
	@Override
	public void export(ArrayList<String> text) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.game.getTitle());
		sendIntent.putExtra(Intent.EXTRA_TITLE, this.game.getTitle());
		sendIntent.setType("application/octet-stream");
		context.startActivity(sendIntent);
	}

}
