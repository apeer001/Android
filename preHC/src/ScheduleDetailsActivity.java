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
import android.support.v4.app.FragmentActivity;

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
		setContentView(R.layout.schedule_details);
		
		Bundle getExtra = getIntent().getExtras();
		
		ScheduleDetailsFragment viewer = (ScheduleDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.scheduleDetails);
		viewer.updateText(getExtra.getString("school"), getExtra.getString("date"), getExtra.getString("time"), getExtra.getString("tv"));
	}
}