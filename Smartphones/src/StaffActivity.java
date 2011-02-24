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

import com.itnoles.shared.JSONBackgroundTask;
import com.itnoles.shared.JSONAsyncTaskCompleteListener;
import com.itnoles.shared.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffActivity extends ListActivity implements JSONAsyncTaskCompleteListener
{
	private static final String LOG_TAG = "StaffActivity";
	private JSONBackgroundTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maincontent);
		
		View header = Utilities.setHeaderonListView("Current Football Staff", this);
		getListView().addHeaderView(header);
		
		task = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(getResources().getString(R.string.staff_url));
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
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			for (int i = 0; i < json.length(); i++) {
				JSONObject rec = json.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", rec.getString("name"));
				map.put("position", rec.getString("positions"));
				list.add(map);
			}
			setListAdapter(new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
			new String[] {"name", "position"}, new int[] {android.R.id.text1, android.R.id.text2}));
		} catch (JSONException e) {
			Log.e(LOG_TAG, "bad json parsing", e);
		}
	}
}