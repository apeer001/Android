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

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is a secondary activity, to show what the user has selected
 * when the screen is not large enough to show it all in one activity.
 * @author Jonathan Steele
 */
public class ScheduleDetailsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation
			== Configuration.ORIENTATION_LANDSCAPE)
		{
			// If the screen is now in landscape mode, we can show the
			// dialog in-line with the list so we don't need this activity.
			finish();
			return;
		}

		if (savedInstanceState == null) {
			// During initial setup, plug in the schedule details fragment.
			final DetailsFragment details = new DetailsFragment();
			details.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(android.R.id.content,
				details).commit();
		}
	}

	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class DetailsFragment extends Fragment
	{
		/**
		 * Create a new instance of DetailsFragment, initialized to
		 * show the text at a few of variable.
		 * @param school text for school
		 * @param date text for date
		 * @param time text for time
		 * @param tv text for date
		 * @return new DetailsFragment
		 */
		public static DetailsFragment newInstance(String school, String date,
			String time, String tv)
		{
			final DetailsFragment f = new DetailsFragment();

		 	// Supply index input as an argument.
		 	final Bundle args = new Bundle();
		 	args.putString("school", school);
		 	args.putString("date", date);
		 	args.putString("time", time);
		 	args.putString("tv", tv);
		 	f.setArguments(args);
		 	return f;
		}

	 	@Override
	 	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	 		Bundle savedInstanceState)
	 	{
	 	 	if (container == null) {
	 	 	 	// Currently in a layout without a container, so no
	 	 	 	// reason to create our view.
	 	 	 	return null;
	 	 	}

	 	 	final View convertView = inflater.inflate(R.layout.schedule_item,
	 	 		container, false);
	 	 	final TextView school = (TextView) convertView.findViewById(
	 	 		R.id.school);
	 	 	school.setText(getArguments().getString("school"));

	 	 	final TextView date = (TextView) convertView.findViewById(
	 	 		R.id.date);
	 	 	date.setText(getArguments().getString("date"));

	 	 	final TextView time = (TextView) convertView.findViewById(
	 	 		R.id.time);
	 	 	time.setText(getArguments().getString("time"));

	 	 	final TextView tv = (TextView) convertView.findViewById(R.id.tv);
	 	 	tv.setText(getArguments().getString("tv"));
	 	 	return convertView;
	 	}
	}
}

