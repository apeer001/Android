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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

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
	 * the member variable to hold AsyncTaskCompleteListener reference.
	 */
	private AsyncTaskCompleteListener<List<Map<String, String>>> mCallback;

	/**
	 * the member variable to hold List reference
	 */
	private List<Map<String, String>> mEntries = new ArrayList<Map<String,
		String>>();

	/**
	 * Constructor.
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
		final String jsonString = NetUtils.inputStreamAsString(params[0]);
		if (jsonString == null) {
			return null;
		}
		try {
			final JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				final Map<String, String> map = new HashMap<String, String>();
				final JSONObject rec = jsonArray.getJSONObject(i);
				final Iterator iter = rec.keys();
				while (iter.hasNext()) {
					final String key = (String) iter.next();
					final String value = rec.getString(key);
					map.put(key, value);
				}
				mEntries.add(map);
			}
		}
		catch (JSONException e) {
			Log.w(LOG_TAG, "bad json parsing", e);
		}
		return mEntries;
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
	 * clear all items in the list.
	 */
	public void clear()
	{
		mEntries.clear();
	}
}
