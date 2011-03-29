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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSONBackgroundTask
 * it is the class has a delegate for AsyncTask class which is for json parsing
 * @author Jonathan Steele
 */

public class JSONBackgroundTask extends AsyncTask<String, Void, List<Map<String, String>>> {
	private static final String LOG_TAG = "JSONBackgroundTask";
	private AsyncTaskCompleteListener<List<Map<String, String>>> callback;
	
	// Constructor
	public JSONBackgroundTask(AsyncTaskCompleteListener<List<Map<String, String>>> callback) {
		this.callback = callback;
	}
	
	// perform a computation on a background thread
	@Override
	protected List<Map<String, String>> doInBackground(String... params)
	{
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(LOG_TAG, "Error " + statusCode + " while retrieving content from " + params[0]);
				return null;
			}
			
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(entity.getContent(), 1024);
					return putJSONArrayintoMap(inputStream);
				} finally {
					if (inputStream != null)
						inputStream.close();
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "bad json parsing", e);
		} finally {
			if (client != null)
				client.close();
		}
		return null;
	}
	
	// Runs on the UI thread after doInBackground
	@Override
	protected void onPostExecute(List<Map<String, String>> result)
	{
		callback.onTaskComplete(result);
	}
	
	public List<Map<String, String>> putJSONArrayintoMap(InputStream inputStream) throws JSONException, Exception
	{
		StringBuilder builder = new StringBuilder();
		// Now read the buffered stream.
		int byteRead;
		while ((byteRead = inputStream.read()) != -1)
		{
			builder.append((char)byteRead);
		}
		JSONArray jsonArray = new JSONArray(builder.toString());
		List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		for (int i = 0; i < jsonArray.length(); i++) {
			Map<String,String> map = new HashMap<String,String>();
			JSONObject rec = jsonArray.getJSONObject(i);
			Iterator iter = rec.keys();
			while (iter.hasNext())
			{
				String key = (String)iter.next();
				String value = rec.getString(key);
				map.put(key,value);
			}
			entries.add(map);
		}
		return entries;
	}
}