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

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;

public class SharedPreferencesHelper {
    // App Preferences
    public static final String SP_KEY_NEWS_TITLE = "SP_KEY_NEWS_TITLE";
    public static final String SP_KEY_NEWS_URL = "SP_KEY_NEWS_URL";
    private static final String SP_KEY_NEWS_REFRESH = "SP_KEY_NEWS_REFRESH";

    private final SharedPreferences mPrefs;

    public SharedPreferencesHelper(SharedPreferences prefs) {
        this.mPrefs = prefs;
    }

    public void onNewsPrefChanged(Context context, ListPreference newsPref) {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(SP_KEY_NEWS_TITLE, newsPref.getEntry().toString());
        edit.putString(SP_KEY_NEWS_URL, newsPref.getValue());
        edit.putBoolean(SP_KEY_NEWS_REFRESH, true);
        applyorcommit(edit);

        final BackupManager backupManager = new BackupManager(context);
        backupManager.dataChanged();
    }

    public void setNewsRefreshToFalse() {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean(SP_KEY_NEWS_REFRESH, false);
        applyorcommit(edit);
    }

    public boolean getNewsFresh() {
        return mPrefs.getBoolean(SP_KEY_NEWS_REFRESH, false);
    }

    public String getNewsTitle() {
        return mPrefs.getString(SP_KEY_NEWS_TITLE, "Top Athletics Stories");
    }

    public String getNewsURL(String url) {
        return mPrefs.getString(SP_KEY_NEWS_URL, url);
    }

    private void applyorcommit(SharedPreferences.Editor edit) {
        if (SportsConstants.SUPPORTS_GINGERBREAD) {
            edit.apply();
        } else {
            edit.commit();
        }
    }
}