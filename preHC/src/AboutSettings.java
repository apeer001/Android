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

import com.itnoles.shared.IntentUtils;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * Activity that {@link android.preference.PreferenceActivity}
 * load specific preferences for author's info.
 * @author Jonathan Steele
 */
public class AboutSettings extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Load the XML preferences file
		addPreferencesFromResource(R.xml.about_settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
		Preference preference)
	{
		final String key = preference.getKey();
		if ("author_email".equals(key)) {
			new IntentUtils(this).sendEmail(
				new String[] {preference.getSummary().toString()},
				"App Feedback for " + getResources().getString(
					R.string.app_name));
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
