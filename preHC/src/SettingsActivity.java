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

import com.itnoles.shared.Utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Activity that {@link android.preference.PreferenceActivity}
 * load specific preferences.
 * @author Jonathan Steele
 */
public class SettingsActivity extends PreferenceActivity
       implements OnSharedPreferenceChangeListener
{
	/**
	 * The member variable to hold SharedPreferences reference.
	 */
	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Load the XML preferences file
		addPreferencesFromResource(R.xml.preferences);

		mPrefs = Utils.getSharedPreferences(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().
		registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// Un-register the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().
		unregisterOnSharedPreferenceChangeListener(this);
	}

	/**
	 * It check version for use specific sharedpreference.editor method.
	 * @param editor reference for sharedpreference.editor
	 */
	private void commitChange(SharedPreferences.Editor editor)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			editor.apply();
		}
		else {
			editor.commit();
		}
	}

	/**
	 * Called when a shared preference is changed, added, or removed.
	 * @param sharedPreferences reference for SharedPreferences
	 * @param key string for each preference's key.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
		String key)
	{
		final Preference pref = findPreference(key);
		final SharedPreferences.Editor editor = mPrefs.edit();
		if ("news".equals(key)) {
			final ListPreference newsPref = (ListPreference) pref;
			final int index = newsPref.findIndexOfValue(newsPref.getValue());
			if (index != -1) {
				editor.putString("newstitle", newsPref.getEntries()[index].
					toString());
				editor.putString("newsurl", newsPref.getEntryValues()[index].
					toString());
				// Don't forget to commit or apply your edits!!!
				commitChange(editor);
			}
			setResult(RESULT_OK);
		}
	}
}
