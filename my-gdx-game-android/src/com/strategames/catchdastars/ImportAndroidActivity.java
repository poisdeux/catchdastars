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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.widget.DataBufferAdapter;

/**
 * An activity to illustrate how to pick a file with the
 * opener intent.
 */
public class ImportAndroidActivity extends Activity implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener {

	private static final int REQUEST_CODE_OPENER = 1;
	private ListView mListView;
	private DataBufferAdapter<Metadata> mResultsAdapter;
	private String mNextPageToken;
	private boolean mHasMore;
	private GoogleApiClient googleApiClient;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_listfiles);

		mHasMore = true; // initial request assumes there are files results.

		mListView = (ListView) findViewById(R.id.listViewResults);
		mResultsAdapter = new ResultsAdapter(this);
		mListView.setAdapter(mResultsAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			/**
			 * Handles onScroll to retrieve next pages of results
			 * if there are more results items to display.
			 */
			@Override
			public void onScroll(AbsListView view, int first, int visible, int total) {
				if (mNextPageToken != null && first + visible + 5 < total) {
					retrieveNextPage();
				}
			}
		});
	}

	/**
	 * Clears the result buffer to avoid memory leaks as soon
	 * as the activity is no longer visible by the user.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		mResultsAdapter.clear();
	}

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
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();
		}
		this.googleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		IntentSender intentSender = Drive.DriveApi
				.newOpenFileActivityBuilder()
				.setMimeType(new String[] { "text/plain" })
				.build(this.googleApiClient);
		try {
			startIntentSenderForResult(
					intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
		} catch (SendIntentException e) {
			Log.w("ImportAndroid", "Unable to send intent", e);
		}
		retrieveNextPage();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_OPENER:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(
						OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

				Log.d("ImportAndroid", "Selected file's ID: " + driveId);
			}
			finish();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

	}
	
	/**
     * Retrieves results for the next page. For the first run,
     * it retrieves results for the first page.
     */
    private void retrieveNextPage() {
        // if there are no more results to retrieve,
        // return silently.
        if (!mHasMore) {
            return;
        }
        // retrieve the results for the next page.
        Query query = new Query.Builder()
            .setPageToken(mNextPageToken)
            .build();
        Drive.DriveApi.query(this.googleApiClient, query)
                .setResultCallback(metadataBufferCallback);
    }
    
    /**
     * Appends the retrieved results to the result buffer.
     */
    private final ResultCallback<MetadataBufferResult> metadataBufferCallback = new
            ResultCallback<MetadataBufferResult>() {
        @Override
        public void onResult(MetadataBufferResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.d("ImportAndroidActivity", "Problem while retrieving files");
                return;
            }
            mResultsAdapter.append(result.getMetadataBuffer());
            mNextPageToken = result.getMetadataBuffer().getNextPageToken();
            mHasMore = mNextPageToken != null;
        }
    };
}
