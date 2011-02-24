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

import com.itnoles.shared.JSONAsyncTaskCompleteListener;
import com.itnoles.shared.JSONBackgroundTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleFragment extends ListFragment implements JSONAsyncTaskCompleteListener {
	private static final String LOG_TAG = "ScheduleFragment";
	boolean mDualPane;
	int mCurCheckPosition = 0;
	int mShownCheckPosition = -1;
	private JSONBackgroundTask task;
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		// Give some text to display if there is no data.
		setEmptyText(getString(R.string.listview_empty));
			
		task = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(getResources().getString(R.string.schedule_url));
			
		// Check to see if we have a frame in which to embed the details
		// fragment directly in the containing UI.
		View detailsFrame = getActivity().findViewById(R.id.details);
		mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
		
		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			mShownCheckPosition = savedInstanceState.getInt("shownChoice", -1);
		}
			
		if (mDualPane)
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
		
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);
		outState.putInt("shownChoice", mShownCheckPosition);
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
			setListAdapter(new SimpleAdapter(getActivity(), entries, android.R.layout.simple_list_item_1, new String[] {"school"}, new int[] {android.R.id.text1}));
		} catch (JSONException e) {
			Log.e(LOG_TAG, "bad json parsing", e);
		}
	}
		
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCurCheckPosition = position;
			
		HashMap fullObjects = (HashMap)getListAdapter().getItem(position);
		String school = fullObjects.get("school").toString();
		String date = fullObjects.get("date").toString();
		String time = fullObjects.get("time").toString();
		String tv = fullObjects.get("tv").toString();
			
		if (mDualPane) {
			// We can display everything in-place with fragments, so update
			// the list to highlight the selected item and show the data.
			getListView().setItemChecked(position, true);
				
			if (mShownCheckPosition != mCurCheckPosition) {
				// If we are not currently showing a fragment for the new
				// position, we need to create and install a new one.
				ScheduleDetailsActivity.ScheduleDetailsFragment df = ScheduleDetailsActivity.ScheduleDetailsFragment.newInstance(school, date, time, tv);
				
				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.details, df);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
 				mShownCheckPosition = position;
			}
		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			final Intent intent = new Intent();
			intent.setClass(getActivity(), ScheduleDetailsActivity.class);
			intent.putExtra("school", school);
			startActivity(intent);
		}
	}
}