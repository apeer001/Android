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

package com.itnoles.shared.ui.phone;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.itnoles.shared.R;

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
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		final String key = preference.getKey();
		if ("author_email".equals(key)) {
		    final Intent i = new Intent(Intent.ACTION_SEND);
		    i.setType("message/rfc822");
		    i.putExtra(Intent.EXTRA_EMAIL, new String[] {preference.getSummary().toString()});
		    i.putExtra(Intent.EXTRA_SUBJECT, "App Feedback for " + getResources().getString(R.string.app_name));
		    startActivity(Intent.createChooser(i, "Select email application."));
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}