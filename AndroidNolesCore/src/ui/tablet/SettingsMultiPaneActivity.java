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

package com.itnoles.shared.ui.tablet;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.itnoles.shared.R;
import com.itnoles.shared.SportsApplication;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.SharedPreferenceSaver;

import java.util.List;

public class SettingsMultiPaneActivity extends PreferenceActivity
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
	{
	    @Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.general_settings);
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
		{
		    final String key = preference.getKey();
		    if ("news".equals(key)) {
		        final SportsApplication apps = (SportsApplication) getActivity().getApplicationContext();
		        final Editor edit = apps.getSharedPreferenceEditor();
		        final ListPreference newsPref = (ListPreference) preference;
		        edit.putString(SportsConstants.SP_KEY_NEWS_TITLE, newsPref.getEntry().toString());
		        edit.putString(SportsConstants.SP_KEY_NEWS_URL, newsPref.getValue());
		        final SharedPreferenceSaver saver = PlatformSpecificImplementationFactory.getSharedPreferenceSaver(getActivity());
		        saver.savePreferences(edit, false);
		    }

		    return super.onPreferenceTreeClick(preferenceScreen, preference);
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
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
		{
			final String key = preference.getKey();
			if ("author_email".equals(key)) {
				final Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] {preference.getSummary().toString()});
				i.putExtra(Intent.EXTRA_SUBJECT, "App Feedback for " + getString(R.string.app_name));
		        startActivity(Intent.createChooser(i, "Select email application."));
			}
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}
}