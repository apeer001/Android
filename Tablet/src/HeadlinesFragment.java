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

import com.itnoles.shared.FeedAsyncTaskCompleteListener;
import com.itnoles.shared.FeedBackgroundTask;
import com.itnoles.shared.ImageDownloader;
import com.itnoles.shared.News;
import com.itnoles.shared.Utilities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;

public class HeadlinesFragment extends ListFragment implements FeedAsyncTaskCompleteListener {
	private static final int PREFERENCE = 0;
	
	private SharedPreferences mPreference;
	private FeedBackgroundTask mFeedTask;
	boolean mDualPane;
	int mCurCheckPosition = 0;
	int mShownCheckPosition = -1;
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		// Give some text to display if there is no data.
		setEmptyText(getString(R.string.listview_empty));
			
		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);
			
		mPreference = getActivity().getSharedPreferences("settings", Activity.MODE_PRIVATE);
			
		// Get a new Data
		getNewContents();
			
		// Create an empty adapter we will use to display the loaded data.
		NewsAdapter mAdapter = new NewsAdapter(getActivity());
		setListAdapter(mAdapter);
			
		// Check to see if we have a frame in which to embed the details
		// fragment directly in the containing UI.
		View detailsFrame = getActivity().findViewById(R.id.details);
		mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
			
		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			mShownCheckPosition = savedInstanceState.getInt("shownChoice", -1);
		}
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Place an action bar item for reload or settings.
		inflater.inflate(R.menu.newsmenu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.settings:
				final Intent pref = new Intent(getActivity(), SettingsActivity.class);
				startActivityForResult(pref, PREFERENCE);
			return true;
			case R.id.refresh:
				getNewContents();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);
		outState.putInt("shownChoice", mShownCheckPosition);
	}
		
	private void getNewContents()
	{
		String defaultTitle = getResources().getStringArray(R.array.listNames)[0];
		View header = Utilities.setHeaderonListView(mPreference.getString("newstitle", defaultTitle), getActivity());
		getListView().addHeaderView(header, null, false);

		String defaultUrl = getResources().getStringArray(R.array.listValues)[0];
		mFeedTask = (FeedBackgroundTask) new FeedBackgroundTask(this).execute(mPreference.getString("newsurl", defaultUrl));
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
		
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCurCheckPosition = position;
		
		News newsList = (News)getListAdapter().getItem(position);
		String link = newsList.getLink();
		
		if (mDualPane) {
			if (mShownCheckPosition != mCurCheckPosition) {
				// If we are not currently showing a fragment for the new
				// position, we need to create and install a new one.
				WebDetailsActivity.WebDetailsFragment df = WebDetailsActivity.WebDetailsFragment.newInstance(link);
				
				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.details, df);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
				mShownCheckPosition = position;
			}
		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			Intent intent = new Intent();
			intent.setClass(getActivity(), WebDetailsActivity.class);
			intent.putExtra("url", link);
			startActivity(intent);
		}
	}
	
	/**
	 * This method is called when the sending activity has finished, with the
	 * result it supplied.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREFERENCE && resultCode == Activity.RESULT_OK)
			getNewContents();
	}
	
	private static class NewsAdapter extends ArrayAdapter<News> {
		private final LayoutInflater mLayoutInflater;
		
		// Constructor
		public NewsAdapter(Activity activity) {
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
					ImageDownloader imageDownload = new ImageDownloader();
					imageDownload.download(news.getImageURL(), thumbnail);
				} else
					thumbnail.setVisibility(View.GONE);
			}
				
			TextView title = (TextView) convertView.findViewById(R.id.text1);
			if (title != null)
				title.setText(news.getTitle());
				
			TextView subTitle = (TextView) convertView.findViewById(R.id.text2);
			if (subTitle != null)
				subTitle.setText(news.getPubDate());
				
			return convertView;
		}
	}
}