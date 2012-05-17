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

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * A class that specifies which of the shared preferences you want to backup
 * to the Google Backup Service.
 */
public class SportsBackupAgent extends BackupAgentHelper {
    @Override
    public void onCreate() {
        final String packagePrefs = getPackageName() + "_preferences";
        final SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, packagePrefs);
        addHelper("newsurl_preference", helper);
    }
}