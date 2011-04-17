//  Copyright 2011 Jonathan Steele
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.itnoles.shared;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * It is one of the utilities class that is handling SharedPreferences.
 * @author Jonathan Steele
 */
public class PrefsUtils
{
	/**
	 * The member variable to hold SharedPreferences reference.
	 */
	private SharedPreferences mPrefs;

	/**
	 * The member variable to hold Activity reference.
	 */
	private Activity mActivity;

	/**
	 * Constructor.
	 * @param context the reference for Activity
	 */
	public PrefsUtils(Activity context)
	{
		this.mActivity = context;
		mPrefs = context.getSharedPreferences("settings",
			Context.MODE_PRIVATE);
	}

	/**
	 * get News URL from Prefs.
	 * @return string
	 */
	public String getNewsURLFromPrefs()
	{
		final String defaultUrl = mActivity.getResources().getStringArray(
			R.array.listValues)[0];
		return mPrefs.getString("newsurl", defaultUrl);
	}

	/**
	 * get News Title from Prefs.
	 * @return string
	 */
	public String getNewsTitleFromPrefs()
	{
		final String defaultTitle = mActivity.getResources().getStringArray(
			R.array.listNames)[0];
		return mPrefs.getString("newstitle", defaultTitle);
	}

	/**
	 * get Editor from SharedPreferences.
	 * @return editor
	 */
	public SharedPreferences.Editor getEditor()
	{
		return mPrefs.edit();
	}
}
