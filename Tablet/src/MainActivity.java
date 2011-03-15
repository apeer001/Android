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

import com.itnoles.shared.News;
import com.itnoles.shared.NewsAdapter;
import com.itnoles.shared.PrefsUtils;
import com.itnoles.shared.Utils;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Map;
import java.util.List;

public class MainActivity extends Activity
{
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layer);
		
		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.addTab(bar.newTab().setText("Headlines").setTabListener(new TabListener(new HeadlinesFragment())));
		bar.addTab(bar.newTab().setText("Schedule").setTabListener(new TabListener(new ScheduleFragment())));
		bar.addTab(bar.newTab().setText("Link").setTabListener(new TabListener(new LinkFragment())));
		bar.addTab(bar.newTab().setText("Staff").setTabListener(new TabListener(new StaffFragment())));
	}
	
	/**
	 * A TabListener receives event callbacks from the action bar as tabs
	 * are deselected, selected, and reselected. A FragmentTransaction
	 * is provided to each of these callbacks; if any operations are added
	 * to it, it will be committed at the end of the full tab switch operation.
	 * This lets tab switches be atomic without the app needing to track
	 * the interactions between different tabs.
	 */
	private class TabListener implements ActionBar.TabListener
	{
		private Fragment mFragment;
		
		public TabListener(Fragment fragment)
		{
			mFragment = fragment;
		}
		
		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			ft.add(R.id.titles, mFragment);
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft)
		{
			ft.remove(mFragment);
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
		}
	}
	
	public static class HeadlinesFragment extends FeedLoadFragment
	{
		private static final int PREFERENCE = 0;
		private PrefsUtils mPrefs;
		boolean mDualPane;
		int mCurCheckPosition = 0;
		int mShownCheckPosition = -1;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);
			
			mPrefs = new PrefsUtils(getActivity());
			
			// Get a new Data
			getNewContents(false);
			
			// Create an empty adapter we will use to display the loaded data.
			NewsAdapter mAdapter = new NewsAdapter(getActivity());
			setListAdapter(mAdapter);

			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			View detailsFrame = getActivity().findViewById(R.id.details);
			// If users click in non-dual pane tabs, it cause this one to be gone too.
			if (detailsFrame != null && detailsFrame.getVisibility() == View.GONE)
				detailsFrame.setVisibility(View.VISIBLE);
			mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
			
			 // Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			Bundle args = Utils.setBundleURL(mPrefs.getNewsURLFromPrefs());
			getLoaderManager().initLoader(0, args, this).forceLoad();
		}
		
		@Override
		public void onPause()
		{
			super.onPause();
			
			getLoaderManager().destroyLoader(0);
			
			if (mDualPane)
				// replace details fragment with empty one
				getFragmentManager().beginTransaction().replace(R.id.details, new Fragment()).commit();
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
		{
			// Place an action bar item for reload or settings.
			inflater.inflate(R.menu.newsmenu, menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId()) {
				case R.id.settings:
					final Intent pref = new Intent(getActivity(), SettingsActivity.class);
					startActivityForResult(pref, PREFERENCE);
				return true;
				case R.id.refresh:
					getNewContents(true);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
		
		private void getNewContents(boolean refresh)
		{
			getActivity().getActionBar().setSubtitle(mPrefs.getNewsTitleFromPrefs());
			
			if (refresh) {
				Bundle args = Utils.setBundleURL(mPrefs.getNewsURLFromPrefs());
				getLoaderManager().restartLoader(0, args, this);
			}
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			mCurCheckPosition = position;
			
			String link = ((News)getListAdapter().getItem(position)).getLink();
			
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
		public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			if (requestCode == PREFERENCE && resultCode == RESULT_OK)
				getNewContents(true);
		}
	}
	
	public static class ScheduleFragment extends JSONLoadFragment
	{
		boolean mDualPane;
		int mCurCheckPosition = 0;
		int mShownCheckPosition = -1;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			View detailsFrame = getActivity().findViewById(R.id.details);
			// If users click in non-dual pane tabs, it cause this one to be gone too.
			if (detailsFrame != null && detailsFrame.getVisibility() == View.GONE)
				detailsFrame.setVisibility(View.VISIBLE);
			mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
			
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			Bundle args = Utils.setBundleURL(getResources().getString(R.string.schedule_url));
			getLoaderManager().initLoader(1, null, this).forceLoad();
		}
		
		@Override
		public void onPause()
		{
			super.onPause();
			
			getLoaderManager().destroyLoader(1);
			
			if (mDualPane)
				// replace details fragment with empty one
				getFragmentManager().beginTransaction().replace(R.id.details, new Fragment()).commit();
		}
		
		public void onLoadFinished(Loader<List<Map<String, String>>> loader, List<Map<String, String>> data) {
			setListAdapter(new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_1, new String[] {"school"}, new int[] {android.R.id.text1}));
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			mCurCheckPosition = position;
			
			Map fullObjects = (Map)getListAdapter().getItem(position);
			String school = fullObjects.get("school").toString();
			String date = fullObjects.get("date").toString();
			String time = fullObjects.get("time").toString();
			String tv = fullObjects.get("tv").toString();
			
			if (mDualPane) {
				if (mShownCheckPosition != mCurCheckPosition) {
					// If we are not currently showing a fragment for the new
					// position, we need to create and install a new one.
					ScheduleDetailsActivity.ScheduleDetailsFragment df = ScheduleDetailsActivity.ScheduleDetailsFragment.newInstance(school, date, time, tv);
					
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
				intent.setClass(getActivity(), ScheduleDetailsActivity.class);
				intent.putExtra("school", school);
				intent.putExtra("date", date);
				intent.putExtra("time", time);
				intent.putExtra("tv", tv);
				startActivity(intent);
			}
		}
	}
	
	public static class LinkFragment extends ListFragment
	{
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			View detailFrame = getActivity().findViewById(R.id.details);
			if (detailFrame != null)
				detailFrame.setVisibility(View.GONE);
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.linkNames)));
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			String url = getResources().getStringArray(R.array.linkValues)[position];
			// Take string from url and parse it to the default browsers
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(viewIntent);
		}
	}
	
	public static class StaffFragment extends JSONLoadFragment
	{
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			View detailFrame = getActivity().findViewById(R.id.details);
			if (detailFrame != null)
				detailFrame.setVisibility(View.GONE);
			
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			Bundle args = Utils.setBundleURL(getResources().getString(R.string.staff_url));
			getLoaderManager().initLoader(2, args, this).forceLoad();
		}
		
		@Override
		public void onPause()
		{
			super.onPause();
			
			getLoaderManager().destroyLoader(2);
		}

		public void onLoadFinished(Loader<List<Map<String, String>>> loader, List<Map<String, String>> data)
		{
			setListAdapter(new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2,
			new String[] {"name", "positions"}, new int[] {android.R.id.text1, android.R.id.text2}));
		}
	}
}