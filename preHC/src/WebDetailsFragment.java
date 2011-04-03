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
 * item
 */
public class WebDetailsFragment extends Fragment
{
	private WebView webView;
	private String newUrl;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		webView = new WebView(getActivity());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});
		return webView;
	}
		
	public void updateUrl(String newUrl) {
		if (webView != null) {
			webView.loadUrl(newUrl);
			this.newUrl = newUrl;
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
		menu.add(Menu.NONE, R.string.share, Menu.NONE, R.string.share).setIcon(R.drawable.ic_menu_share);
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.string.share:
				new IntentUtils(getActivity()).selectAction(newUrl);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		webView.destroy();
	}
}