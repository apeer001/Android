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
 * item.
 */
public class ScheduleDetailsFragment extends Fragment
{
	private TextView school;
	private TextView date;
	private TextView time;
	private TextView tv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View convertView = inflater.inflate(R.layout.schedule_item, container, false);
		school = (TextView) convertView.findViewById(R.id.school);
		date = (TextView) convertView.findViewById(R.id.date);
		time = (TextView) convertView.findViewById(R.id.time);
		tv = (TextView) convertView.findViewById(R.id.tv);
		return convertView;
	}
	
	public void updateText(String update_school, String update_date, String update_time, String update_tv)
	{
		school.setText(update_school);
		date.setText(update_date);
		time.setText(update_time);
		tv.setText(update_tv);
	}
}