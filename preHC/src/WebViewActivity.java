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
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends FragmentActivity
{
	@Override
	// Called when the activity is first created.
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// During initial setup, plug in the details fragment.
		DetailsFragment details = new DetailsFragment();
		details.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
	}
		
	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class DetailsFragment extends Fragment
	{
		private WebView webView;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (container == null) {
				// We have different layouts, and in one of them this
				// fragment's containing frame doesn't exist. The fragment
				// may still be created from its saved state, but there is
				// no reason to try to create its view hierarchy because it
				// won't be displayed. Note this is not needed -- we could
				// just run the code below, where we would create and return
				// the view hierarchy; it would just never be used.
				return null;
			}

			webView = new WebView(getActivity());
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.setWebViewClient(new WebViewClient() {
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
				}
			});
			webView.loadUrl(getArguments().getString("url"));
			return webView;
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
			menu.add("Share").setIcon(R.drawable.ic_menu_share);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			if (item.getTitle().equals("Share"))
				new IntentUtils(getActivity()).selectAction(getArguments().getString("url"));
			return super.onOptionsItemSelected(item);
		}
		
		@Override
		public void onDestroyView()
		{
			super.onDestroyView();
			webView.destroy();
		}
	}
}