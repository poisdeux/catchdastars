/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strategames.catchdastars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

/**
 * An activity to illustrate how to pick a file with the
 * opener intent.
 */
public class ImportAndroidActivity extends Activity implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener {

	private static final int REQUEST_CODE_OPENER = 1;
	private static final int REQUEST_CODE_RESOLUTION = 2;

	private GoogleApiClient googleApiClient;

	/**
	 * Called when activity gets visible. A connection to Drive services need to
	 * be initiated as soon as the activity is visible. Registers
	 * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
	 * activities itself.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (this.googleApiClient == null) {
			this.googleApiClient = new GoogleApiClient.Builder(this)
			.addApi(Drive.API)
			.addScope(Drive.SCOPE_FILE)
			.addScope(Drive.SCOPE_APPFOLDER)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();
		}
		this.googleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("ImportAndroidActivity", "onConnected");

		IntentSender intentSender = Drive.DriveApi
				.newOpenFileActivityBuilder()
				.setMimeType(new String[] { "text/plain", "text/html" })
				.build(this.googleApiClient);
		try {
			startIntentSenderForResult(
					intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
		} catch (SendIntentException e) {
			Log.w("ImportAndroid", "Unable to send intent", e);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("ImportAndroidActivity", "onActivityResult: "+requestCode);
		switch (requestCode) {
		case REQUEST_CODE_OPENER:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(
						OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

				Log.d("ImportAndroid", "Selected file's ID: " + driveId);
				RetrieveDriveFileContentsAsyncTask task = new RetrieveDriveFileContentsAsyncTask();
				task.execute(driveId);
			}
			finish();
			break;
		case REQUEST_CODE_RESOLUTION:
			if (resultCode == RESULT_OK) {
				this.googleApiClient.connect();
			}
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("ImportAndroidActivity", "onConnectionFailed: "+result);

		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e("ImportAndroidActivity", "Exception while starting resolution activity", e);
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.d("ImportAndroidActivity", "onConnectionSuspended: "+cause);
	}

	final private class RetrieveDriveFileContentsAsyncTask
	extends AsyncTask<DriveId, Boolean, String> {

		@Override
		protected String doInBackground(DriveId... params) {
			String contents = null;
			DriveFile file = Drive.DriveApi.getFile(googleApiClient, params[0]);
			ContentsResult contentsResult =
					file.openContents(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
			if (!contentsResult.getStatus().isSuccess()) {
				return null;
			}
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(contentsResult.getContents().getInputStream()));
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

			file.discardContents(googleApiClient, contentsResult.getContents()).await();
			return contents;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				Toast.makeText(ImportAndroidActivity.this, 
						"Error while reading from the file", Toast.LENGTH_SHORT).show();
				return;
			}
			Log.d("ImportAndroidActivity", "File contents: " + result);
		}
	}
}
