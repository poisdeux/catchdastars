/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.catchdastars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

public class RetrieveDriveFileContentsAsyncTask
extends AsyncTask<DriveId, Boolean, String> {
	
	public interface OnFileReceivedListener {
		public void onFileReceived(String json);
	}
	
	private GoogleApiClient googleApiClient;
	private Context context;
	private OnFileReceivedListener listener;
	
	public RetrieveDriveFileContentsAsyncTask(Context context, GoogleApiClient googleApiClient,
			OnFileReceivedListener listener) {
		this.context = context;
		this.googleApiClient = googleApiClient;
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(DriveId... params) {
		String contents = null;
		DriveFile file = Drive.DriveApi.getFile(this.googleApiClient, params[0]);
		DriveContentsResult contentsResult =
				file.open(this.googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
		if (!contentsResult.getStatus().isSuccess()) {
			return null;
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(contentsResult.getDriveContents().getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			contents = builder.toString();
		} catch (IOException e) {
			Log.e("ImportAndroidActivity", "IOException while reading from the stream", e);
		}
		return contents;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result == null) {
			Toast.makeText(this.context, 
					"Error while reading from the file", Toast.LENGTH_SHORT).show();
			return;
		}
		listener.onFileReceived(result);
	}
}