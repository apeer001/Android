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
import android.os.*; // AsyncTask and Bundle
import android.view.*; // LayoutInflater, Menu, MenuItem and View
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*; // AdapterView, ArrayAdapter and TextView

import com.itnoles.shared.*; // FeedAsyncTaskCompleteListener, FeedBackgroundTask, FeedHandler and News

public class HeadlinesActivity extends ListActivity implements FeedAsyncTaskCompleteListener {
	private static final int PREFERENCE = 0;
	private SharedPreferences mPrefs;
	private FeedBackgroundTask mFeedTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maincontent);

		mPrefs = getSharedPreferences("settings", MODE_PRIVATE);
		
		getNewContents();
	}
	
	private void getNewContents()
	{
		String defaultTitle = getResources().getStringArray(R.array.listNames)[0];
		
		View header = Utilities.setHeaderonListView(mPrefs.getString("newstitle", defaultTitle), this);
		getListView().addHeaderView(header, null, false);
		
		String defaultUrl = getResources().getStringArray(R.array.listValues)[0];
		mFeedTask = (FeedBackgroundTask) new FeedBackgroundTask(this).execute(mPrefs.getString("newsurl", defaultUrl));
		
		NewsAdapter mAdapter = new NewsAdapter(this);
		setListAdapter(mAdapter);
		// register context menu for listview
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		// unregister context menu for listview
		unregisterForContextMenu(getListView());
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
		menu.add(Menu.NONE, 0, Menu.NONE, "Settings").setIcon(R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, 1, Menu.NONE, "Refresh Data").setIcon(R.drawable.ic_menu_refresh);
		return true;
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
	
	@Override
	public void onProgressUpdate(News... values)
	{
		((NewsAdapter)getListAdapter()).add(values[0]);
	}
	
	@Override
	public void onTaskComplete(Void result)
	{
		if (mFeedTask.isCancelled()) return;
		if (mFeedTask != null && mFeedTask.getStatus() != AsyncTask.Status.FINISHED) {
			mFeedTask.cancel(true);
			mFeedTask = null;
		}
	}
	
	private static class NewsAdapter extends ArrayAdapter<News> {
		private final LayoutInflater mLayoutInflater;
		
		// Constructor
		public NewsAdapter(ListActivity activity) {
			super(activity, 0);
			mLayoutInflater = LayoutInflater.from(activity);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = mLayoutInflater.inflate(R.layout.headlines_item, parent, false);

			final News news = getItem(position);
			ImageView thumbnail = (ImageView) convertView.findViewById(R.id.icon);
			if (thumbnail != null) {
				if (news.getImageURL() != null && news.getImageURL().length() > 0) {
					ImageDownloader imageDownloader = new ImageDownloader();
					imageDownloader.download(news.getImageURL(), thumbnail);
				} else
					thumbnail.setVisibility(View.GONE);
			}

			TextView title = (TextView) convertView.findViewById(R.id.text1);
			if (title != null)
				title.setText(news.getTitle());

			TextView subTitle = (TextView) convertView.findViewById(R.id.text2);
			if (subTitle != null)
				subTitle.setText(news.getPubdate());
			
			return convertView;
		}
	}
}