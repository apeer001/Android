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

package com.itnoles.shared;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.IStrictMode;

public class SportsApplication extends Application
{
    private SharedPreferences mPrefs;

    @Override
    public void onCreate()
    {
        super.onCreate();
        if (SportsConstants.DEVELOPER_MODE) {
            final IStrictMode strictMode = PlatformSpecificImplementationFactory.getStrictMode();
            if (strictMode != null) {
                strictMode.enableStrictMode();
            }
        }
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public String getNewsTitle()
    {
        final String defaultTitle = getResources().getStringArray(R.array.listNames)[0];
        return mPrefs.getString(SportsConstants.SP_KEY_NEWS_TITLE, defaultTitle);
    }

    public String getNewsURL()
    {
        final String defaultUrl = getResources().getStringArray(R.array.listValues)[0];
        return mPrefs.getString(SportsConstants.SP_KEY_NEWS_URL, defaultUrl);
    }

    public SharedPreferences.Editor getSharedPreferenceEditor()
    {
        return mPrefs.edit();
    }
}