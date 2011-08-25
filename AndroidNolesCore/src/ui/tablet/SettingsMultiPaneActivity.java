/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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