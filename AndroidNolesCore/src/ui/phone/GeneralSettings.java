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

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.itnoles.shared.R;
import com.itnoles.shared.SportsApplication;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.SharedPreferenceSaver;

public class GeneralSettings extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Load the XML preferences file
		addPreferencesFromResource(R.xml.general_settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		final String key = preference.getKey();
		if ("news".equals(key)) {
		    final SportsApplication apps = (SportsApplication) getApplicationContext();
		    final Editor edit = apps.getSharedPreferenceEditor();
		    final ListPreference newsPref = (ListPreference) preference;
		    edit.putString(SportsConstants.SP_KEY_NEWS_TITLE, newsPref.getEntry().toString());
		    edit.putString(SportsConstants.SP_KEY_NEWS_URL, newsPref.getValue());
		    final SharedPreferenceSaver saver = PlatformSpecificImplementationFactory.getSharedPreferenceSaver(this);
		    saver.savePreferences(edit, false);
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}