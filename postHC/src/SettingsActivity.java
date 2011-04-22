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
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.List;

/**
 * Activity that {@link android.preference.PreferenceActivity}
 * load specific preferences.
 * @author Jonathan Steele
 */
public class SettingsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	/**
	 * Populate the activity with the top-level headers.
	 * @param target reference for List<Header>
	 */
	@Override
	public void onBuildHeaders(List<Header> target)
	{
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class GeneralFragment extends PreferenceFragment
	       implements OnSharedPreferenceChangeListener
	{
		/**
		 * The member variable to hold SharedPreferences reference.
		 */
		private SharedPreferences mPrefs;

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.general);

			mPrefs = Utils.getSharedPreferences(getActivity());
		}

		@Override
		public void onResume()
		{
			super.onResume();
			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().
			registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause()
		{
			super.onPause();
			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().
			unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key)
		{
			final Preference pref = findPreference(key);
			final SharedPreferences.Editor editor = mPrefs.edit();
			if ("news".equals(key)) {
				final ListPreference newsPref = (ListPreference) pref;
				final int index = newsPref.findIndexOfValue(
					newsPref.getValue());
				if (index != -1) {
					editor.putString("newstitle",
						newsPref.getEntries()[index].toString());
					editor.putString("newsurl",
						newsPref.getEntryValues()[index].toString());
					// Don't forget to apply your edits!!!
					editor.apply();
				}
				getActivity().setResult(RESULT_OK);
			}
		}
	}

	/**
	 * This fragment shows the preferences for the second header.
	 */
	public static class AboutFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.about_settings);
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference)
		{
			final String key = preference.getKey();
			if ("author_email".equals(key)) {
				Utils.sendEmail(getActivity(),
					new String[] {preference.getSummary().toString()},
					"App Feedback for " + getResources().getString(
						R.string.app_name));
			}
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}
}
