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

import android.content.Intent;
import android.content.pm.*; //PackageInfo and PackageManager
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.net.Uri;
import android.preference.*; // Preference, PreferenceActivity and PreferenceScreen
import android.util.Log;

public class AboutSettings extends PreferenceActivity
{
	private static final String NONFOUND = "N/A";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Load the XML preferences file
		addPreferencesFromResource(R.xml.about_settings);
		
		setStringSummary("app_version", getVersion());
		setStringSummary("author_name", "Jonathan Steele");
		setStringSummary("author_email", "xfsunoles@gmail.com");
		setStringSummary("author_website", NONFOUND);
		
		findPreference("author_email").setEnabled(true);
		findPreference("author_website").setEnabled(true);
	}
	
	private void setStringSummary(String preference, String value)
	{
		try {
			findPreference(preference).setSummary(value);
		} catch (RuntimeException e) {
			findPreference(preference).setSummary(NONFOUND);
		}
	}
	
	protected String getVersion()
	{
		String version = "N/A";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
			version = pInfo.versionName;
		} catch (NameNotFoundException e1) {
			Log.e(this.getClass().getSimpleName(), "Name not found", e1);
		}
		return version;
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		String key = preference.getKey();
		String summary = preference.getSummary().toString();
		if (key.equals("author_email")) {
			final Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] {summary});
			i.putExtra(Intent.EXTRA_SUBJECT, "App Feedback for " + getResources().getString(R.string.app_name));
			startActivity(Intent.createChooser(i, "Select email application."));
		} else if (key.equals("author_website")) {
			if (NONFOUND.equals(summary) == false) {
				// Take string from url and parse it to the default browsers
				final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(summary));
				startActivity(viewIntent);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}