//  Copyright 2011 Jonathan Steele
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
package com.itnoles.shared.activity;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;

public abstract class JSONLoadFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Map<String, String>>>
{
	public Loader<List<Map<String, String>>> onCreateLoader(int id, Bundle args)
	{
		// This is called when a new Loader needs to be created.
		return new JSONLoader(getActivity(), args.getString("url"));
	}
	
	public void onLoaderReset(Loader<List<Map<String, String>>> loader) {}
	
	private static class JSONLoader extends AsyncTaskLoader<List<Map<String, String>>>
	{
		private static final String LOG_TAG = "JSONLoader";
		private String mUri;
		
		public JSONLoader(Activity activity, String uri)
		{
			super(activity);
			this.mUri = uri;
		}
		
		@Override
		public List<Map<String, String>> loadInBackground()
		{
			final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			final HttpGet getRequest = new HttpGet(mUri);
			try {
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					Log.w(LOG_TAG, "Error " + statusCode + " while retrieving content from " + mUri);
					return null;
				}
				
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = new BufferedInputStream(entity.getContent(), 1024);
						return putJSONArrayIntoMap(convertStreamToString(inputStream));
					} finally {
						if (inputStream != null)
							inputStream.close();
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				getRequest.abort();
				Log.w(LOG_TAG, "bad json array", e);
			} finally {
				if (client != null)
					client.close();
			}
			return null;
		}
		
		private static String convertStreamToString(InputStream inputStream) throws Exception
		{
			StringBuilder builder = new StringBuilder();
			// Now read the buffered stream.
			int byteRead;
			while ((byteRead = inputStream.read()) != -1)
			{
				builder.append((char)byteRead);
			}
			return builder.toString();
		}
		
		private List<Map<String, String>> putJSONArrayIntoMap(String inputStream) throws JSONException
		{
			JSONArray jsonArray = new JSONArray(inputStream);
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
}