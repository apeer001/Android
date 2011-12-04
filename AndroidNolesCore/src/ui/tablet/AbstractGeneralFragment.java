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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.itnoles.shared.R;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.SharedPreferenceSaver;

/**
 * An abstract preference fragment class for General Settings.
 */
public abstract class AbstractGeneralFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	protected ListPreference mNewsPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.general_settings);

		mNewsPref = (ListPreference) findPreference("news");
	}

	@Override
	public void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if ("news".equals(key)) {
		    final SharedPreferences.Editor edit = sharedPreferences.edit();
		    edit.putString(SportsConstants.SP_KEY_NEWS_TITLE, mNewsPref.getEntry().toString());
		    edit.putString(SportsConstants.SP_KEY_NEWS_URL, mNewsPref.getValue());
		    edit.putBoolean(SportsConstants.SP_KEY_NEWS_REFRESH, true);
		    final SharedPreferenceSaver saver = PlatformSpecificImplementationFactory.getSharedPreferenceSaver(getActivity());
		    saver.savePreferences(edit, false);
		}
	}
}