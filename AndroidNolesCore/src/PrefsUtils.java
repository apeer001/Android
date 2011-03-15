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

public class PrefsUtils
{
	private SharedPreferences mPrefs;
	private Activity activity;
	
	public PrefsUtils(Activity activity)
	{
		this.activity = activity;
		mPrefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
	}
	
	public String getNewsURLFromPrefs()
	{
		String defaultUrl = activity.getResources().getStringArray(R.array.listValues)[0];
		return mPrefs.getString("newsurl", defaultUrl);
	}
	
	public String getNewsTitleFromPrefs()
	{
		String defaultTitle = activity.getResources().getStringArray(R.array.listNames)[0];
		return mPrefs.getString("newstitle", defaultTitle);
	}
	
	public SharedPreferences.Editor getEditor()
	{
		return mPrefs.edit();
	}
}