/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itnoles.shared.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itnoles.shared.R;

import java.util.Map;

public class ScheduleDetailsFragment extends Fragment
{
	public static ScheduleDetailsFragment newInstance(Map item)
	{
		final ScheduleDetailsFragment f = new ScheduleDetailsFragment();
		f.setArguments(putMapIntoBundle(item));
		return f;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	    if (container == null) {
	 	    // Currently in a layout without a container, so no
	 	    // reason to create our view.
	 	    return null;
	    }

	    final View convertView = inflater.inflate(R.layout.schedule_item, container, false);
	    final TextView school = (TextView) convertView.findViewById(R.id.school);
	    school.setText(getArguments().getString("school"));

	    final TextView date = (TextView) convertView.findViewById(R.id.date);
	    date.setText(getArguments().getString("date"));

	    final TextView time = (TextView) convertView.findViewById(R.id.time);
	    time.setText(getArguments().getString("time"));

	    final TextView tv = (TextView) convertView.findViewById(R.id.tv);
	    tv.setText(getArguments().getString("tv"));
	    return convertView;
    }

    protected static Bundle putMapIntoBundle(Map item)
    {
	    final Bundle args = new Bundle();
		args.putString("school", item.get("school").toString());
		args.putString("date", item.get("date").toString());
		args.putString("time", item.get("time").toString());
		args.putString("tv", item.get("tv").toString());
		return args;
    }
}