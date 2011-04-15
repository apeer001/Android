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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.Map;
import java.util.List;

/**
 * The main activity for this application.
 * @author Jonathan Steele
 */
public class MainActivity extends Activity
{
	/**
	 * the field for theme id.
	 */
	private int mThemeId = -1;

	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null
			&& savedInstanceState.getInt("theme", -1) != -1)
		{
            mThemeId = savedInstanceState.getInt("theme");
            setTheme(mThemeId);
        }

		setContentView(R.layout.fragment_layer);

		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		final HeadlinesFragment headlines = new HeadlinesFragment();
		bar.addTab(bar.newTab().setText("Headlines").setTabListener(
			new TabListener(headlines)));

		final ScheduleFragment schedule = new ScheduleFragment();
		bar.addTab(bar.newTab().setText("Schedule").setTabListener(
			new TabListener(schedule)));

		final LinkFragment link = new LinkFragment();
		bar.addTab(bar.newTab().setText("Link").setTabListener(
			new TabListener(link)));

		final StaffFragment staff = new StaffFragment();
		bar.addTab(bar.newTab().setText("Staff").setTabListener(
			new TabListener(staff)));

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tabpos"));
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(Menu.NONE, R.string.daynight, Menu.NONE, R.string.daynight);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId()) {
    	case R.string.daynight:
    		if (mThemeId == android.R.style.Theme_Holo || mThemeId == -1) {
    			mThemeId = android.R.style.Theme_Holo_Light;
    		}
    		else {
                mThemeId = android.R.style.Theme_Holo;
            }
            recreate();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        final int currentTabPos = getActionBar().getSelectedNavigationIndex();
        outState.putInt("tabpos", currentTabPos);
        outState.putInt("theme", mThemeId);
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
    	/**
    	 * the member variable to hold the reference for Fragment.
    	 */
    	private Fragment mFragment;

    	/**
    	 * Constructor
    	 * @param fragment reference for Fragment
    	 */
    	public TabListener(Fragment fragment)
    	{
    		mFragment = fragment;
    	}

    	/**
    	 * Called when a tab enters the selected state.
    	 * When the activity is being recreated, ft.add will make the content
    	 * looks terrible.
    	 * @param tab reference of Tab
    	 * @param ft reference of FragmentTransaction
    	 */
    	public void onTabSelected(Tab tab, FragmentTransaction ft)
    	{
    		ft.replace(R.id.titles, mFragment, null);
    	}

    	/**
    	 * Called when a tab exits the selected state.
    	 * this wouldn't work when you are recreated Activity because
    	 * it didn't unselected the tab.
    	 * @param tab reference of Tab
    	 * @param ft reference of FragmentTransaction
    	 */
    	public void onTabUnselected(Tab tab, FragmentTransaction ft)
    	{
    	}

    	/**
    	 * Called when a tab that is already selected is chosen again by the
    	 * user.
    	 * @param tab reference of Tab
    	 * @param ft reference of FragmentTransaction
    	 */
    	public void onTabReselected(Tab tab, FragmentTransaction ft)
    	{
    		// do nothing
    	}
    }

	/**
	 * A fragment that {@link android.app.ListFragment}
	 * to load a specific item for headlines.
	 */
	public static class HeadlinesFragment extends ListFragment
	       implements AsyncTaskCompleteListener<List<News>>
	{
		/**
		 * the field for preference in startActivityForResult
		 */
		private static final int PREFERENCE = 0;

		/**
		 * The member variable to hold PrefsUtils reference.
		 */
		private PrefsUtils mPrefs;

		/**
		 * The member variable to hold FeedBackgroundTask reference.
		 */
		private FeedBackgroundTask mTask;

		/**
		 * A flag that Fragment supports dual pane and is under landscape.
		 */
		private boolean mDualPane;

		/**
		  * a number that is already showing
		  */
		private int mShownCheckPosition = -1;

		@Override
		public void onActivityCreated(Bundle savedState)
		{
			super.onActivityCreated(savedState);

			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);

			mPrefs = new PrefsUtils(getActivity());

			// Get a new Data
			getNewContents();

			// Create an empty adapter we will use to display the loaded data.
			final NewsAdapter mAdapter = new NewsAdapter(getActivity());
			setListAdapter(mAdapter);

			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			final View detailsFrame = getActivity().findViewById(R.id.details);
			// If users click in non-dual pane tabs,
			// it cause this one to be gone too.
			if (detailsFrame != null
				&& detailsFrame.getVisibility() == View.GONE)
			{
				detailsFrame.setVisibility(View.VISIBLE);
			}
			mDualPane = detailsFrame != null
			     && detailsFrame.getVisibility() == View.VISIBLE;
		}

		@Override
		public void onPause()
		{
			super.onPause();

			if (mDualPane && !getActivity().isChangingConfigurations()) {
				// replace details fragment with empty one
				getFragmentManager().beginTransaction().replace(R.id.details,
					new Fragment()).commit();
			}
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();

			// Clear all item in NewsAdapter
			((NewsAdapter) getListAdapter()).clear();
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
				final Intent pref = new Intent(getActivity(),
					SettingsActivity.class);
				startActivityForResult(pref, PREFERENCE);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		/**
		 * Get a new data to display.
		 */
		private void getNewContents()
		{
			getActivity().getActionBar().setSubtitle(
				mPrefs.getNewsTitleFromPrefs());

			mTask = (FeedBackgroundTask) new FeedBackgroundTask(this).execute(
				mPrefs.getNewsURLFromPrefs());
		}

		/**
		  * Display Data to ListView when AsyncTask is finishing loading.
		  * @param feed Data from FeedBackgroundTask
		  */
		public void onTaskComplete(List<News> feed)
		{
			// If AsyncTask is cancelled, return early
			if (mTask.isCancelled()) {
				return;
			}

			if (mTask != null
				&& mTask.getStatus() != FeedBackgroundTask.Status.FINISHED)
			{
				mTask.cancel(true);
				mTask = null;
			}

			// If feed is null, return early
			if (feed == null) {
				return;
			}

			for (News news : feed) {
				((NewsAdapter) getListAdapter()).add(news);
			}
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			final String link = ((News) getListAdapter().getItem(position)).
			getLink();

			if (mDualPane) {
				if (mShownCheckPosition != position) {
					// If we are not currently showing a fragment for the new
					// position, we need to create and install a new one.
					final WebDetailsActivity.DetailsFragment df =
					      WebDetailsActivity.DetailsFragment.newInstance(link);

					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					final FragmentTransaction ft = getFragmentManager().
					    beginTransaction();
					ft.replace(R.id.details, df);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
					mShownCheckPosition = position;
				}
			}
			else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				final Intent intent = new Intent(getActivity(),
					WebDetailsActivity.class);
				intent.putExtra("url", link);
				startActivity(intent);
			}
		}

		/**
		 * This method is called when the sending activity has finished, with
		 * the result it supplied.
		 * @param requestCode integer that use in startActivityForResult
		 * @param resultCode integer that returned by child activity
		 * @param data returned data by child activity
		 */
		@Override
		public void onActivityResult(int requestCode, int resultCode,
			Intent data)
		{
			if (requestCode == PREFERENCE && resultCode == RESULT_OK) {
				getNewContents();
			}
		}
	}

	/**
	 * A fragment that {@link android.app.ListFragment}
	 * to load a specific item for schedule.
	 */
	public static class ScheduleFragment extends ListFragment
	       implements AsyncTaskCompleteListener<List<Map<String, String>>>
	{
		/**
		 * The member variable to hold JSONBackgroundTask reference.
		 */
		private JSONBackgroundTask mTask;

		/**
		 * A flag that Fragment supports dual pane and is under landscape.
		 */
		private boolean mDualPane;

		/**
		  * a number that is already showing
		  */
		private int mShownCheckPosition = -1;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			final View detailsFrame = getActivity().findViewById(R.id.details);
			// If users click in non-dual pane tabs,
			// it cause this one to be gone too.
			if (detailsFrame != null
				&& detailsFrame.getVisibility() == View.GONE)
			{
				detailsFrame.setVisibility(View.VISIBLE);
			}

			mDualPane = detailsFrame != null
			    && detailsFrame.getVisibility() == View.VISIBLE;

			mTask = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(
				getString(R.string.schedule_url));
		}

		@Override
		public void onPause()
		{
			super.onPause();

			if (mDualPane && !getActivity().isChangingConfigurations()) {
				// replace details fragment with empty one
				getFragmentManager().beginTransaction().replace(R.id.details,
					new Fragment()).commit();
			}
		}

		/**
		  * Display Data to ListView when AsyncTask is finishing loading.
		  * @param json Data from JSONBackgroundTask
		  */
		public void onTaskComplete(List<Map<String, String>> json)
		{
			// If AsyncTask is cancelled, return early
			if (mTask.isCancelled()) {
				return;
			}

			if (mTask != null
				&& mTask.getStatus() != JSONBackgroundTask.Status.FINISHED)
			{
				mTask.cancel(true);
				mTask = null;
			}

			// If json is null, return early
			if (json == null) {
				return;
			}

			setListAdapter(new SimpleAdapter(getActivity(), json,
				android.R.layout.simple_list_item_1, new String[] {"school"},
				new int[] {android.R.id.text1}));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			final Map fullObjects = (Map) getListAdapter().getItem(position);
			final String school = fullObjects.get("school").toString();
			final String date = fullObjects.get("date").toString();
			final String time = fullObjects.get("time").toString();
			final String tv = fullObjects.get("tv").toString();

			if (mDualPane) {
				// We can display everything in-place with fragments, so update
				// the list to highlight the selected item and show the data.
				getListView().setItemChecked(position, true);

				if (mShownCheckPosition != position) {
					// If we are not currently showing a fragment for the new
					// position, we need to create and install a new one.
					final ScheduleDetailsActivity.DetailsFragment df =
					    ScheduleDetailsActivity.DetailsFragment.newInstance(
						    school, date, time, tv);

					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					final FragmentTransaction ft = getFragmentManager().
					    beginTransaction();
					ft.replace(R.id.details, df);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
					mShownCheckPosition = position;
				}
			}
			else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				final Intent intent = new Intent(getActivity(),
					ScheduleDetailsActivity.class);
				intent.putExtra("school", school);
				intent.putExtra("date", date);
				intent.putExtra("time", time);
				intent.putExtra("tv", tv);
				startActivity(intent);
			}
		}
	}

	/**
	 * A fragment that {@link android.app.ListFragment}
	 * to load a specific item for link.
	 */
	public static class LinkFragment extends ListFragment
	{
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			final View detailFrame = getActivity().findViewById(R.id.details);
			if (detailFrame != null) {
				detailFrame.setVisibility(View.GONE);
			}

			setListAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.linkNames, android.R.layout.simple_list_item_1));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			final String url = getResources().getStringArray(
				R.array.linkValues)[position];
			// Take string from url and parse it to the default browsers
			new IntentUtils(getActivity()).openBrowser(url);
		}
	}

	/**
	 * A fragment that {@link android.app.ListFragment}
	 * to load a specific item for staff.
	 */
	public static class StaffFragment extends ListFragment
	       implements AsyncTaskCompleteListener<List<Map<String, String>>>
	{
		/**
		 * The member variable to hold JSONBackgroundTask reference.
		 */
		private JSONBackgroundTask mTask;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			final View detailFrame = getActivity().findViewById(R.id.details);
			if (detailFrame != null) {
				detailFrame.setVisibility(View.GONE);
			}

			mTask = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(
				getString(R.string.staff_url));
		}

		/**
		  * Display Data to ListView when AsyncTask is finishing loading.
		  * @param json Data from JSONBackgroundTask
		  */
		public void onTaskComplete(List<Map<String, String>> json)
		{
			// If AsyncTask is cancelled, return early
			if (mTask.isCancelled()) {
				return;
			}

			if (mTask != null
				&& mTask.getStatus() != JSONBackgroundTask.Status.FINISHED)
			{
				mTask.cancel(true);
				mTask = null;
			}

			// if json is null, return early
			if (json == null) {
				return;
			}

			setListAdapter(new SimpleAdapter(getActivity(), json,
				android.R.layout.simple_list_item_2,
				new String[] {"name", "positions"},
				new int[] {android.R.id.text1, android.R.id.text2}));
		}
	}
}
