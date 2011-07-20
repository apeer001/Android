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