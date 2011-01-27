//  Copyright 2010 Jonathan Steele
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

import android.app.ListActivity;
import android.content.*; // Intent and SharedPreferences
import android.os.Bundle;
import android.view.*; // Menu, MenuItem and View
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*; // AdapterView and TextView

import java.util.List;

import com.itnoles.shared.*; //BetterBackgroundTask, News and Utilities
import com.itnoles.shared.adapter.NewsAdapter;
import com.itnoles.shared.helper.*; // BetterAsyncTaskCompleteListener and FeedParser

public class HeadlinesActivity extends ListActivity implements BetterAsyncTaskCompleteListener<String, Void, List<News>> {
	private static final int PREFERENCE = 0;
	private SharedPreferences mPrefs = null;
	private BetterBackgroundTask<String, Void, List<News>> task = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maincontent);

		mPrefs = getSharedPreferences("settings", MODE_PRIVATE);
		
		getNewContents();
		
		// register context menu for listview
		registerForContextMenu(getListView());
	}
	
	private NewsAdapter getNewsAdapter()
	{
		return ((NewsAdapter)getListAdapter());
	}
	
	private void getNewContents()
	{
		String defaultTitle = getResources().getStringArray(R.array.listNames)[0];
		final TextView headerText = (TextView) findViewById(R.id.list_header_title);
		headerText.setText(mPrefs.getString("newstitle", defaultTitle));
		
		// Check to see count is more than zero then clear the list
		if (getNewsAdapter() != null && getNewsAdapter().getCount() > 0)
			getNewsAdapter().clear();
		
		// Visible Progress Bar
		getParent().setProgressBarIndeterminateVisibility(true);
		
		task = (BetterBackgroundTask<String, Void, List<News>>)getLastNonConfigurationInstance();
		if (task == null) {
			task = new BetterBackgroundTask<String, Void, List<News>>(this);
			String defaultUrl = getResources().getStringArray(R.array.listValues)[0];
			task.execute(mPrefs.getString("newsurl", defaultUrl));
		} else
			task.attach(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		getNewsAdapter().clear();
		
		// unregister context menu for listview
		unregisterForContextMenu(getListView());
	}
	
	@Override
	public Object onRetainNonConfigurationInstance()
	{
		task.detach();
		return task;
	}
	
	// Display Data to ListView
	public void onTaskComplete(List<News> news)
	{
		// Hide the progress bar
		getParent().setProgressBarIndeterminateVisibility(false);
		NewsAdapter adapter = new NewsAdapter(this, news);
		setListAdapter(adapter);
	}
	
	// Do This stuff in Background
	public List<News> readData(String ...params)
	{
		// Parse RSS or Atom Feed
		return FeedParser.parse(params[0]);
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
		News newsList = getItemFromNews(info);
		switch(item.getItemId()) {
			case 0:
				// Launch Activity to view page load in webview
				final Intent displayWebView = new Intent(this, WebViewActivity.class);
				displayWebView.putExtra("url", newsList.getLink());
				startActivity(displayWebView);
				return true;
			case 1:
				final Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, newsList.getLink());
				startActivity(Intent.createChooser(shareIntent, "Select an action"));
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	// When the users clicked the menu button in their device, it called this method first
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Settings").setIcon(R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, 1, Menu.NONE, "Refresh Data").setIcon(R.drawable.ic_menu_refresh);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				final Intent pref = new Intent(this, SettingsActivity.class);
				startActivityForResult(pref, PREFERENCE);
			return true;
			case 1:
				getNewContents();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Called when an activity called by using startActivityForResult finishes.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == PREFERENCE && resultCode == RESULT_OK)
			getNewContents();
	}
}