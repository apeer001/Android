//  Copyright 2010 Jonathan Steele
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.itnoles.shared;

import org.apache.http.*; // HttpEntity, HttpResponse, HttpStatus
import org.apache.http.client.methods.HttpGet;

import android.os.AsyncTask;
import android.net.http.AndroidHttpClient;
import android.util.*; // Log and Xml

import java.io.*; // BufferedInputStream and InputStream

public class FeedBackgroundTask extends AsyncTask<String, News, Void> {
	private FeedAsyncTaskCompleteListener callback;
	private static final String LOG_TAG = "FeedBackgroundTask";
	
	// Constructor
	public FeedBackgroundTask(FeedAsyncTaskCompleteListener callback) {
		this.callback = callback;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		// Parse RSS or Atom Feed
		InputStream inputStream = null;
		FeedHandler handler = new FeedHandler();
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(getRequest);
			final HttpEntity entity = response.getEntity();
			inputStream = new BufferedInputStream(entity.getContent());
			// Parse the xml-data from InputStream.
			Xml.parse(inputStream, Xml.Encoding.UTF_8, handler);
		} catch (Exception e) {
			getRequest.abort();
			Log.e(LOG_TAG, "bad feed parsing", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.w(LOG_TAG, "can't close input stream", e);
			}
			
			if (client != null)
				client.close();
		}
		// Parsing has finished.
		// Our handler now provides the parsed data to us.
		for (News news : handler.getMessages())
			publishProgress(news);
		return null;
	}
	
	@Override
	public void onProgressUpdate(News... values) {
		callback.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Void result) {
		callback.onTaskComplete(result);
	}
}