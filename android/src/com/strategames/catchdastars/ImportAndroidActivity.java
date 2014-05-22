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

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.strategames.catchdastars.RetrieveDriveFileContentsAsyncTask.OnFileReceivedListener;

/**
 * An activity to illustrate how to pick a file with the
 * opener intent.
 */
public class ImportAndroidActivity extends Activity implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
OnFileReceivedListener {

	public static final String BUNDLE_KEY_JSON = "bundle_key_json";
	
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
		Log.d("ImportAndroidActivity", "onActivityResult: requestCode="+requestCode+", resultCode="+resultCode);
		switch (requestCode) {
		case REQUEST_CODE_OPENER:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(
						OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				RetrieveDriveFileContentsAsyncTask task = new RetrieveDriveFileContentsAsyncTask(this, this.googleApiClient, this);
				task.execute(driveId);
			}
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

	@Override
	public void onFileReceived(String json) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(BUNDLE_KEY_JSON, json);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
