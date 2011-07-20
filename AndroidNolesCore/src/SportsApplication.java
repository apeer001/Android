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