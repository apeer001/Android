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
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * JSONBackgroundTask
 * it is the class has a delegate for AsyncTask class which is for json parsing
 * @author Jonathan Steele
 */

public class JSONBackgroundTask extends AsyncTask<String, Void, JSONArray> {
	private static final String LOG_TAG = "JSONHelper";
	private JSONAsyncTaskCompleteListener callback;
	
	// Constructor
	public JSONBackgroundTask(JSONAsyncTaskCompleteListener callback) {
		this.callback = callback;
	}
	
	// perform a computation on a background thread
	@Override
	protected JSONArray doInBackground(String... params)
	{
        JSONArray json = null;
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(getRequest);
			final HttpEntity entity = response.getEntity();
			json = new JSONArray(convertStreamToString(entity));
			entity.consumeContent();
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "bad json array", e);
		} finally {
			if (client != null)
				client.close();
		}
		return json;
	}
	
	// Runs on the UI thread after doInBackground
	@Override
	protected void onPostExecute(JSONArray result)
	{
		callback.onTaskComplete(result);
    }

    private static String convertStreamToString(HttpEntity entity) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        while((line = reader.readLine()) != null)
        {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }
}