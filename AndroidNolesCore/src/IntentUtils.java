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
import android.content.Intent;
import android.net.Uri;

/**
 * handling Intent with a specific method of action.
 * @author Jonathan Steele
 */
public class IntentUtils
{
	/**
	 * The member variable to hold Activity reference.
	 */
	private Activity mActivity;

	/**
	 * Constructor.
	 * @param context reference of Activity
	 */
	public IntentUtils(Activity context)
	{
		this.mActivity = context;
	}

	/**
	 * Select an action with url.
	 * @param link url string
	 */
	public void selectAction(String link)
	{
		final Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, link);
		mActivity.startActivity(Intent.createChooser(shareIntent,
			"Select an action"));
	}

	/**
	 * Open Android Browser with url.
	 * @param url url string
	 */
	public void openBrowser(String url)
	{
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW,
			Uri.parse(url));
		mActivity.startActivity(viewIntent);
	}

	/**
	 * Select an action with email address.
	 * @param email email address
	 * @param subject string for subject
	 */
	public void sendEmail(String[] email, String subject)
	{
		final Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, email);
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		mActivity.startActivity(Intent.createChooser(i,
			"Select email application."));
	}
}

