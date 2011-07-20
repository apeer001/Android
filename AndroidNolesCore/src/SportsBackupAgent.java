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

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * A class that specifies which of the shared preferences you want to backup
 * to the Google Backup Service.
 */
public class SportsBackupAgent extends BackupAgentHelper
{
    @Override
    public void onCreate()
    {
        final String packagePrefs = getPackageName() + "_preferences";
        final SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, packagePrefs);
        addHelper(SportsConstants.SP_KEY_NEWS_TITLE, helper);
        addHelper(SportsConstants.SP_KEY_NEWS_URL, helper);
    }
}