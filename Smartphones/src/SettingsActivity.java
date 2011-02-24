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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	private SharedPreferences sharedPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Load the XML preferences file
		addPreferencesFromResource(R.xml.preferences);
		
		sharedPref = getSharedPreferences("settings", MODE_PRIVATE);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	private void commitChange(SharedPreferences.Editor editor)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			editor.apply();
		else
			editor.commit();
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = findPreference(key);
		SharedPreferences.Editor editor = sharedPref.edit();
		if (key.equals("news"))
		{
			ListPreference newsPref = (ListPreference)pref;
			int index = newsPref.findIndexOfValue(newsPref.getValue());
			if (index != -1)
			{
				editor.putString("newstitle", newsPref.getEntries()[index].toString());
				editor.putString("newsurl", newsPref.getEntryValues()[index].toString());
				// Don't forget to commit or apply your edits!!!
				commitChange(editor);
			}
			setResult(RESULT_OK);
		}
	}
}