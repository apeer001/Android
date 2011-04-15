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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Is the AsyncTask class for json parsing.
 * @author Jonathan Steele
 */
public class JSONBackgroundTask
       extends AsyncTask<String, Void, List<Map<String, String>>>
{
	/**
	 * The string for Android's Log Tag.
	 */
	private static final String LOG_TAG = "JSONBackgroundTask";

	/**
	 * the member to hold AsyncTaskCompleteListener reference.
	 */
	private AsyncTaskCompleteListener<List<Map<String, String>>> mCallback;

	/** Constructor.
	 * @param callback reference for AsyncTaskCompleteListener
	 */
	public JSONBackgroundTask(
		AsyncTaskCompleteListener<List<Map<String, String>>> callback)
	{
		this.mCallback = callback;
	}

	/**
	 * perform a computation on a background thread.
	 * @param params reference for String Array
	 * @return List<Map<String,String>>
	 */
	@Override
	protected List<Map<String, String>> doInBackground(String... params)
	{
		final AndroidHttpClient client = AndroidHttpClient.newInstance(
			Constants.USER_AGENT);
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			final HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(LOG_TAG, "Error " + statusCode
					+ " while retrieving content from " + params[0]);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(entity.getContent(),
						Constants.BUF_SIZE);
					return putJSONArrayIntoMap(inputStream);
				}
				finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		}
		catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "bad json parsing", e);
		}
		finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	/**
	 * Runs on the UI thread after doInBackground.
	 * @param result reference for List<Map<String,String>>
	 */
	@Override
	protected void onPostExecute(List<Map<String, String>> result)
	{
		mCallback.onTaskComplete(result);
	}

	/**
	 * it take string from inputstream and putting it to Map.
	 * @param stream reference for InputStream
	 * @throws JSONException when JSON string is unreadable or not valid
	 * @throws IOException when something is gone wrong on inputstream
	 * @return new List with Map
	 */
	public List<Map<String, String>> putJSONArrayIntoMap(InputStream stream)
	    throws JSONException, IOException
	{
		final StringBuilder builder = new StringBuilder();
		// Now read the buffered stream.
		int byteRead;
		while ((byteRead = stream.read()) != -1) {
			builder.append((char) byteRead);
		}
		final JSONArray jsonArray = new JSONArray(builder.toString());
		final List<Map<String, String>> entries = new ArrayList<Map<String,
		String>>();
		for (int i = 0; i < jsonArray.length(); i++) {
			final Map<String, String> map = new HashMap<String, String>();
			final JSONObject rec = jsonArray.getJSONObject(i);
			final Iterator iter = rec.keys();
			while (iter.hasNext()) {
				final String key = (String) iter.next();
				final String value = rec.getString(key);
				map.put(key, value);
			}
			entries.add(map);
		}
		return entries;
	}
}
