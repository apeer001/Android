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
package com.itnoles.shared.activity;

import com.itnoles.shared.IntentUtils;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Toast;

/**
 * This is a secondary activity, to show what the user has selected
 * when the screen is not large enough to show it all in one activity.
 */
public class WebDetailsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// If the screen is now in landscape mode, we can show the
			// dialog in-line with the list so we don't need this activity.
			finish();
			return;
		}
		
		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.
			WebDetailsFragment details = new WebDetailsFragment();
			details.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
		}
	}
	
	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class WebDetailsFragment extends WebViewFragment
	{
		/**
		 * Create a new instance of WebDetailsFragment, initialized to
		 * show the text at 'url'.
		 * @param urlString text for url
		 * @return new WebDetailFragment
		 */
		public static WebDetailsFragment newInstance(String urlString)
		{
			WebDetailsFragment f = new WebDetailsFragment();
			
			// Supply index input as an argument.
			Bundle args = new Bundle();
			args.putString("url", urlString);
			f.setArguments(args);
			return f;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);
	
			WebView webView = getWebView();
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.setWebViewClient(new WebViewClient() {
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
				}
			});
			webView.loadUrl(getArguments().getString("url"));
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
		{
			MenuItem share = menu.add(Menu.NONE, R.string.share, Menu.NONE, R.string.share).setIcon(R.drawable.ic_menu_share);
			share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId()) {
				case R.string.share:
					new IntentUtils(getActivity()).selectAction(getArguments().getString("url"));
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
	}
}