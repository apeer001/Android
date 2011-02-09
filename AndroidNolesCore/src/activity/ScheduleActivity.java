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
package com.itnoles.shared.activity;

import android.app.ListActivity;
import android.os.*; // Bundle and AsyncTask
import android.widget.SimpleAdapter;
import android.util.Log;

import java.util.*; //ArrayList, HashMap and List
import org.json.*; //JSONArray and JSONObject

import com.itnoles.shared.JSONBackgroundTask;
import com.itnoles.shared.helper.JSONAsyncTaskCompleteListener;

public class ScheduleActivity extends ListActivity implements JSONAsyncTaskCompleteListener
{
	private static final String LOG_TAG = "ScheduleActivity";
	private JSONBackgroundTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maincontent);
		
		task = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(getResources().getString(R.string.schedule_url));
	}
	
	// Display Data to ListView
	public void onTaskComplete(JSONArray json)
	{
		// If json is null, return early
		if (json == null)
			return;
		
		// If AsyncTask is cancelled, return early
		if (task.isCancelled())
			return;
		
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
			task.cancel(true);
			task = null;
		}
		
		List<HashMap<String, String>> entries = new ArrayList<HashMap<String, String>>();
		try {
			for (int i = 0; i < json.length(); i++) {
				JSONObject rec = json.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("date", rec.getString("date"));
				map.put("time", rec.getString("time"));
				map.put("school", rec.getString("school"));
				map.put("tv", rec.getString("tv"));
				entries.add(map);
			}
			setListAdapter(new SimpleAdapter(this, entries, R.layout.schedule_item, new String[] {"date", "time", "school", "tv"}, new int[] {R.id.list_header_title, R.id.time, R.id.school, R.id.tv}));
		} catch (JSONException e) {
			Log.e(LOG_TAG, "bad json parsing", e);
		}
	}
}