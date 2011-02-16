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

import android.app.*; // Activity and ListFragment
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*; // Bundle and AsyncTask
import android.view.*; // LayoutInflater, View and ViewGroup
import android.widget.*; //SimpleAdapter and TextView
import android.util.Log;

import java.util.*; //ArrayList, HashMap and List
import org.json.*; //JSONArray and JSONObject

import com.itnoles.shared.*; // JSONBackgroundTask and JSONAsyncTaskCompleteListener

public class ScheduleActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_fragment_layout);
	}
	
	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */
	public static class DetailsActivity extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// If the screen is now in landscape mode, we can show the
				// dialog in-line with the list so we don't need this activity.
				finish();
				return;
			}
			
			if (savedInstanceState == null) {
				// During initial setup, plug in the details fragment.
				DetailsFragment details = new DetailsFragment();
				details.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
			}
		}
	}
	
	public static class JSONFragment extends ListFragment implements JSONAsyncTaskCompleteListener {
		private static final String LOG_TAG = "ScheduleActivity$JSONFragment";
		boolean mDualPane;
		int mCurCheckPosition = 0;
		int mShownCheckPosition = -1;
		private JSONBackgroundTask task;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
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
			showDetails(position);
		}
		
		/**
		 * Helper function to show the details of a selected item, either by
		 * displaying a fragment in-place in the current UI, or starting a
		 * whole new activity in which it is displayed.
		 */
		void showDetails(int index) {
			mCurCheckPosition = index;
			
			HashMap fullObjects = (HashMap)getListAdapter().getItem(index);
			String school = fullObjects.get("school").toString();
			String date = fullObjects.get("date").toString();
			String time = fullObjects.get("time").toString();
			String tv = fullObjects.get("tv").toString();
			
			if (mDualPane) {
				// We can display everything in-place with fragments, so update
				// the list to highlight the selected item and show the data.
				getListView().setItemChecked(index, true);
				
				if (mShownCheckPosition != mCurCheckPosition) {
					// If we are not currently showing a fragment for the new
					// position, we need to create and install a new one.
					DetailsFragment df = DetailsFragment.newInstance(school, date, time, tv);
					
					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace(R.id.details, df);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
 					mShownCheckPosition = index;
				}
			} else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				final Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("school", school);
				startActivity(intent);
			}
		}
	}
	
	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class DetailsFragment extends Fragment {
		/**
		 * Create a new instance of DetailsFragment, initialized to
		 * show the text at a few of variable.
		 */
		public static DetailsFragment newInstance(String school, String date, String time, String tv) {
			DetailsFragment f = new DetailsFragment();
			
			// Supply index input as an argument.
			Bundle args = new Bundle();
			args.putString("school", school);
			args.putString("date", date);
			args.putString("time", time);
			args.putString("tv", tv);
			f.setArguments(args);
			return f;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (container == null) {
				// We have different layouts, and in one of them this
				// fragment's containing frame doesn't exist.  The fragment
				// may still be created from its saved state, but there is
				// no reason to try to create its view hierarchy because it
				// won't be displayed.  Note this is not needed -- we could
				// just run the code below, where we would create and return
				// the view hierarchy; it would just never be used.
				return null;
			}
			
			View convertView = inflater.inflate(R.layout.schedule_item, container, false);
			TextView school = (TextView) convertView.findViewById(R.id.school);
			school.setText(getArguments().getString("school"));
			
			TextView date = (TextView) convertView.findViewById(R.id.date);
			date.setText(getArguments().getString("date"));
			
			TextView time = (TextView) convertView.findViewById(R.id.time);
			time.setText(getArguments().getString("time"));
			
			TextView tv = (TextView) convertView.findViewById(R.id.tv);
			tv.setText(getArguments().getString("tv"));
			return convertView;
		}
	}
}