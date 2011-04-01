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

import com.itnoles.shared.AsyncTaskCompleteListener;
import com.itnoles.shared.FeedBackgroundTask;
import com.itnoles.shared.IntentUtils;
import com.itnoles.shared.JSONBackgroundTask;
import com.itnoles.shared.News;
import com.itnoles.shared.NewsAdapter;
import com.itnoles.shared.PrefsUtils;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
	
	public static class HeadlinesFragment extends ListFragment implements AsyncTaskCompleteListener<List<News>>
	{
		private static final int PREFERENCE = 0;
		private PrefsUtils mPrefs;
		private FeedBackgroundTask task;
		private boolean mDualPane;
		private int mCurCheckPosition = 0;
		private int mShownCheckPosition = -1;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);
			
			mPrefs = new PrefsUtils(getActivity());
			
			// Get a new Data
			getNewContents();
			
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
			
			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
				mShownCheckPosition = savedInstanceState.getInt("shownChoice", -1);
			}
		}
		
		@Override
		public void onPause()
		{
			super.onPause();
			
			if (mDualPane && !getActivity().isChangingConfigurations())
				// replace details fragment with empty one
				getFragmentManager().beginTransaction().replace(R.id.details, new Fragment()).commit();
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
		{
			// Place an action bar item for settings.
			inflater.inflate(R.menu.newsmenu, menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId()) {
				case R.id.refresh:
					getNewContents();
				return true;
				
				case R.id.settings:
					final Intent pref = new Intent(getActivity(), SettingsActivity.class);
					startActivityForResult(pref, PREFERENCE);
				return true;
				
				case R.id.daynight:
					UiModeManager manager = (UiModeManager)getActivity().getSystemService(Context.UI_MODE_SERVICE);
					if (manager.getNightMode() == UiModeManager.MODE_NIGHT_NO)
						manager.setNightMode(UiModeManager.MODE_NIGHT_YES);
					else
						manager.setNightMode(UiModeManager.MODE_NIGHT_NO);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
		
		private void getNewContents()
		{
			getActivity().getActionBar().setSubtitle(mPrefs.getNewsTitleFromPrefs());
			
			task = (FeedBackgroundTask) new FeedBackgroundTask(this).execute(mPrefs.getNewsURLFromPrefs());
		}
		
		public void onTaskComplete(List<News> data)
		{
			// If AsyncTask is cancelled, return early
			if (task.isCancelled())
				return;

			if (task != null && task.getStatus() != FeedBackgroundTask.Status.FINISHED) {
				task.cancel(true);
				task = null;
			}
			
			// If data is not null, add it to NewsAdapter
			if (data != null) {
				for (News news : data)
					((NewsAdapter)getListAdapter()).add(news);
			}
		}
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putInt("curChoice", mCurCheckPosition);
			outState.putInt("shownChoice", mShownCheckPosition);
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			showDetails(position);
		}
		
		void showDetails(int index)
		{
			mCurCheckPosition = index;
			
			String link = ((News)getListAdapter().getItem(index)).getLink();
			
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
					mShownCheckPosition = index;
				}
			} else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				Intent intent = new Intent(getActivity(), WebDetailsActivity.class);
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
				getNewContents();
		}
	}
	
	public static class ScheduleFragment extends ListFragment implements AsyncTaskCompleteListener<List<Map<String, String>>>
	{
		private JSONBackgroundTask task;
		private boolean mDualPane;
		private int mCurCheckPosition = 0;
		private int mShownCheckPosition = -1;
		
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
			
			task = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(getResources().getString(R.string.schedule_url));
		}
		
		@Override
		public void onPause()
		{
			super.onPause();
			
			if (mDualPane && !getActivity().isChangingConfigurations())
				// replace details fragment with empty one
				getFragmentManager().beginTransaction().replace(R.id.details, new Fragment()).commit();
		}
		
		// Display Data to ListView
		public void onTaskComplete(List<Map<String, String>> json)
		{
			// If AsyncTask is cancelled, return early
			if (task.isCancelled())
				return;

			if (task != null && task.getStatus() != JSONBackgroundTask.Status.FINISHED) {
				task.cancel(true);
				task = null;
			}
			
			setListAdapter(new SimpleAdapter(getActivity(), json, android.R.layout.simple_list_item_1, new String[] {"school"}, new int[] {android.R.id.text1}));
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
				// We can display everything in-place with fragments, so update
				// the list to highlight the selected item and show the data.
				getListView().setItemChecked(position, true);
				
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
				Intent intent = new Intent(getActivity(), ScheduleDetailsActivity.class);
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
			
			setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.linkNames, android.R.layout.simple_list_item_1));
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			String url = getResources().getStringArray(R.array.linkValues)[position];
			// Take string from url and parse it to the default browsers
			new IntentUtils(getActivity()).openBrowser(url);
		}
	}
	
	public static class StaffFragment extends ListFragment implements AsyncTaskCompleteListener<List<Map<String, String>>>
	{
		private JSONBackgroundTask task;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			
			View detailFrame = getActivity().findViewById(R.id.details);
			if (detailFrame != null)
				detailFrame.setVisibility(View.GONE);
				
			task = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(getResources().getString(R.string.staff_url));
		}
		
		// Display Data to ListView
		public void onTaskComplete(List<Map<String, String>> json)
		{
			// If AsyncTask is cancelled, return early
			if (task.isCancelled())
				return;

			if (task != null && task.getStatus() != JSONBackgroundTask.Status.FINISHED) {
				task.cancel(true);
				task = null;
			}
			
			setListAdapter(new SimpleAdapter(getActivity(), json, android.R.layout.simple_list_item_2,
			new String[] {"name", "positions"}, new int[] {android.R.id.text1, android.R.id.text2}));
		}
	}
}