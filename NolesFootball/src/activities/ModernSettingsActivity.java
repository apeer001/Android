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

package com.itnoles.nolesfootball.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.itnoles.nolesfootball.R;
import com.itnoles.shared.fragments.AbstractGeneralFragment;

import java.util.List;

public class ModernSettingsActivity extends PreferenceActivity {
	/**
	 * Populate the activity with the top-level headers.
	 * @param target reference for List<Header>
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class GeneralFragment extends AbstractGeneralFragment {
	    @Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNewsPref.setEntries(R.array.listNames);
			mNewsPref.setEntryValues(R.array.listValues);
		}
	}
}