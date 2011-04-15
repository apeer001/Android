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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is the secondary fragment, displaying the details of a particular
 * schedule item.
 * @author Jonathan Steele
 */
public class ScheduleDetailsFragment extends Fragment
{
	/**
	 * The member variable to hold TextView reference for school.
	 */
	private TextView mSchool;

	/**
	 * The member variable to hold TextView reference for date.
	 */
	private TextView mDate;

	/**
	 * The member variable to hold TextView reference for time.
	 */
	private TextView mTime;

	/**
	 * The member variable to hold TextView reference for tv.
	 */
	private TextView mTv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		final View convertView = inflater.inflate(R.layout.schedule_item,
			container, false);
		mSchool = (TextView) convertView.findViewById(R.id.school);
		mDate = (TextView) convertView.findViewById(R.id.date);
		mTime = (TextView) convertView.findViewById(R.id.time);
		mTv = (TextView) convertView.findViewById(R.id.tv);
		return convertView;
	}

	/**
	 * Update Text in a specific TextView.
	 * @param school text for School
	 * @param date text for Date
	 * @param time text for Time
	 * @param tv text for TV
	 */
	public void updateText(String school, String date, String time, String tv)
	{
		mSchool.setText(school);
		mDate.setText(date);
		mTime.setText(time);
		mTv.setText(tv);
	}
}
