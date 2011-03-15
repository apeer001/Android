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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.itnoles.shared.Utils;

import java.util.List;
import java.util.Map;

public class ScheduleActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layer);
		
		// Create the list fragment and add it as our sole content.
		if (getSupportFragmentManager().findFragmentById(R.id.titles) == null) {
			ScheduleFragment schedule = new ScheduleFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.titles, schedule).commit();
		}
	}
	
	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */
	public static class ScheduleDetailsActivity extends FragmentActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// During initial setup, plug in the schedule details fragment.
			DetailsFragment details = new DetailsFragment();
			details.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
		}
	}
	
	public static class ScheduleFragment extends JSONLoadFragment {
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			Bundle args = Utils.setBundleURL(getResources().getString(R.string.schedule_url));
			getActivity().getSupportLoaderManager().initLoader(1, args, this).forceLoad();
		}
		
		public void onLoadFinished(Loader<List<Map<String, String>>> loader, List<Map<String, String>> data) {
			setListAdapter(new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_1, new String[] {"school"}, new int[] {android.R.id.text1}));
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Map fullObjects = (Map)getListAdapter().getItem(position);
			String school = fullObjects.get("school").toString();
			String date = fullObjects.get("date").toString();
			String time = fullObjects.get("time").toString();
			String tv = fullObjects.get("tv").toString();
		
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			final Intent intent = new Intent();
			intent.setClass(getActivity(), ScheduleDetailsActivity.class);
			intent.putExtra("school", school);
			intent.putExtra("date", date);
			intent.putExtra("time", time);
			intent.putExtra("tv", tv);
			startActivity(intent);
		}
	}
	
	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class DetailsFragment extends Fragment {
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