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
package com.itnoles.nolesfootball;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.*; // Bundle and StrictMode
import android.widget.TabHost;

import com.itnoles.shared.Constants;
import com.itnoles.shared.activity.*; // HeadlinesActivity, ScheduleActivity, StadiumActivity amd StaffActivity

public class NolesFootball extends TabActivity
{
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		if (Constants.ISORLATER_GINGERBREAD && Constants.ISTESTKEY)
			StrictMode.enableDefaults();
		super.onCreate(savedInstanceState);
		
		Resources res = getResources(); // get Resources Class
		final TabHost tabHost = getTabHost(); // The activity TabHost
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, HeadlinesActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		tabHost.addTab(tabHost.newTabSpec("headlines").setIndicator("Headlines", res.getDrawable(R.drawable.ic_menu_rss)).setContent(intent));

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ScheduleActivity.class);
		tabHost.addTab(tabHost.newTabSpec("schedule").setIndicator("Schedule", res.getDrawable(R.drawable.calendar)).setContent(intent));

		intent = new Intent().setClass(this, LinkActivity.class);
		tabHost.addTab(tabHost.newTabSpec("link").setIndicator("Link", res.getDrawable(R.drawable.bookmark)).setContent(intent));

		intent = new Intent().setClass(this, StaffActivity.class);
		tabHost.addTab(tabHost.newTabSpec("staff").setIndicator("Staff", res.getDrawable(R.drawable.star)).setContent(intent));

		intent = new Intent().setClass(this, StadiumActivity.class);
		tabHost.addTab(tabHost.newTabSpec("stadium").setIndicator("Stadium", res.getDrawable(R.drawable.map)).setContent(intent));
	}
}