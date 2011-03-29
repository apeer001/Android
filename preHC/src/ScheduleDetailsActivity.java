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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is a secondary activity, to show what the user has selected
 * when the screen is not large enough to show it all in one activity.
 */
public class ScheduleDetailsActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// During initial setup, plug in the schedule details fragment.
		DetailsFragment details = new DetailsFragment();
		details.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
	}
	
	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class DetailsFragment extends Fragment
	{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
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
