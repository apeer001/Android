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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.itnoles.shared.News;
import com.itnoles.shared.NewsAdapter;
import com.itnoles.shared.PrefsUtils;
import com.itnoles.shared.Utils;

public class HeadlinesActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headlines);
	}
	
	/** Handle "settings" action.
     * @param v reference for View
     */
	public void onSettingsClick(View v) {
		// Launch settings
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	/** Handle "refresh" action.
     * @param v reference of View
     */
	public void onRefreshClick(View v) {
		// refresh current listview
		HeadlinesFragment hf = (HeadlinesFragment)getSupportFragmentManager().findFragmentById(R.id.headlines);
		hf.getNewContents(true);
	}
	
	public static class WebViewActivity extends FragmentActivity
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
	}
	
	public static class HeadlinesFragment extends FeedLoadFragment {
		private static final int PREFERENCE = 0;
		private PrefsUtils mPrefs;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			mPrefs = new PrefsUtils(getActivity());
			
			// Get a new Data
			getNewContents(false);
			
			// Create an empty adapter we will use to display the loaded data.
			NewsAdapter mAdapter = new NewsAdapter(getActivity());
			setListAdapter(mAdapter);
			
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			Bundle args = Utils.setBundleURL(mPrefs.getNewsURLFromPrefs());
			getActivity().getSupportLoaderManager().initLoader(0, args, this).forceLoad();
			
			// register context menu for listview
			registerForContextMenu(getListView());
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			
			// un-register context menu for listview
			unregisterForContextMenu(getListView());
		}
		
		private void getNewContents(boolean refresh)
		{
			((TextView) getActivity().findViewById(R.id.title_text)).setText(mPrefs.getNewsTitleFromPrefs());
			
			if (refresh) {
				Bundle args = Utils.setBundleURL(mPrefs.getNewsURLFromPrefs());
				getActivity().getSupportLoaderManager().restartLoader(0, args, this);
			}
		}
		
		private News getItemFromNews(AdapterView.AdapterContextMenuInfo info)
		{
			return (News)getListAdapter().getItem(info.position);
		}
		
		// Show the list in the context menu
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
		{
			if (v.getId() != android.R.id.list)
				return;
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(getItemFromNews(info).getTitle());
			menu.add(Menu.NONE, 0, Menu.NONE, "View on WebView");
			menu.add(Menu.NONE, 1, Menu.NONE, "Share by other apps");
		}

		// When the users selected the item id in the context menu, it called specific item action.
		@Override
		public boolean onContextItemSelected(MenuItem item)
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			String link = getItemFromNews(info).getLink();
			switch(item.getItemId()) {
				case 0:
					// Launch Activity to view page load in webview
					final Intent displayWebView = new Intent(getActivity(), WebViewActivity.class);
					displayWebView.putExtra("url", link);
					startActivity(displayWebView);
					return true;
				case 1:
					final Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(Intent.EXTRA_TEXT, link);
					startActivity(Intent.createChooser(shareIntent, "Select an action"));
				default:
					return super.onContextItemSelected(item);
			}
		}
		
		/**
		 * This method is called when the sending activity has finished, with the
		 * result it supplied.
		 */
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == PREFERENCE && resultCode == RESULT_OK)
				getNewContents(true);
		}
	}
		
	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */
	public static class DetailsFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (container == null) {
				// We have different layouts, and in one of them this
				// fragment's containing frame doesn't exist.  The fragment
				// may still be created from its saved state, but there is
				// no reason to try to create its view hierarchy because it
				// won't be displayed.  Note this is not needed -- we could
				// just run the code below, where we would create and return
				// the view hierarchy; it would just never be used.
				return null;
			}
			
			WebView webView = new WebView(getActivity());
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
		public void onDestroyView() {
			super.onDestroyView();
			((WebView)getView()).destroy();
		}
	}
}