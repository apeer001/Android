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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * This is the secondary fragment, displaying the details of a particular
 * webview item.
 * @author Jonathan Steele
 */
public class WebDetailsFragment extends Fragment
{
	/**
	 * The member variable to hold WebView reference.
	 */
	private WebView mWebView;

	/**
	 * The member variable to hold newurl string.
	 */
	private String mNewUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl)
			{
				Toast.makeText(getActivity(), "Oh no! " + description,
					Toast.LENGTH_SHORT).show();
			}
		});
		return mWebView;
	}

	/**
	 * Tell WebView to load a new url.
	 * @param newUrl string for url address
	 */
	public void updateUrl(String newUrl)
	{
		if (mWebView != null) {
			mWebView.loadUrl(newUrl);
			this.mNewUrl = newUrl;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// We have a menu item to show in the menu bar.
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.add(Menu.NONE, R.string.share, Menu.NONE, R.string.share).setIcon(
			R.drawable.ic_menu_share);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.string.share:
			new IntentUtils(getActivity()).selectAction(mNewUrl);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		mWebView.destroy();
	}
}
