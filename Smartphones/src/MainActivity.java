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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity
{
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		TabHost tabHost = getTabHost(); // The activity TabHost
		tabHost.addTab(tabHost.newTabSpec("Headlines").setIndicator("headline").setContent(new Intent().setClass(this, HeadlinesActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("Schedule").setIndicator("schedule").setContent(new Intent().setClass(this, ScheduleActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("Link").setIndicator("link").setContent(new Intent().setClass(this, LinkActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("Staff").setIndicator("staff").setContent(new Intent().setClass(this, StaffActivity.class)));
	}
}