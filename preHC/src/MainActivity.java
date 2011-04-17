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

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * The Main Activity for this application.
 * @author Jonathan Steele
 */
public class MainActivity extends FragmentActivity
{
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		getLayoutInflater().inflate(R.layout.tab_content,
			tabHost.getTabContentView(), true);

		final Resources res = getResources();
		tabHost.addTab(tabHost.newTabSpec("Headlines").setIndicator("Headlines",
			res.getDrawable(R.drawable.suitcase)).setContent(
			R.id.headlinesLayout));

		tabHost.addTab(tabHost.newTabSpec("Schedule").setIndicator("Schedule",
			res.getDrawable(R.drawable.calendar)).setContent(
			R.id.scheduleLayout));

		tabHost.addTab(tabHost.newTabSpec("Link").setIndicator("Link",
			res.getDrawable(R.drawable.bookmark)).setContent(R.id.linkLayout));

		tabHost.addTab(tabHost.newTabSpec("Staff").setIndicator("Staff",
			res.getDrawable(R.drawable.star)).setContent(R.id.staffLayout));
	}

	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */
	public static class WebDetailsActivity extends FragmentActivity
	{
		@Override
		// Called when the activity is first created.
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.headlines_details);

			final WebDetailsFragment viewer =
		                          (WebDetailsFragment)
		                          getSupportFragmentManager().findFragmentById(
		                          	  R.id.headlinesDetails);
		    viewer.updateUrl(getIntent().getExtras().getString("url"));
		}
	}

	/**
	 * A fragment that {@link android.support.v4.app.ListFragment}
	 * to load a specific item for headlines.
	 */
	public static class HeadlinesFragment extends ListFragment
	       implements AsyncTaskCompleteListener<List<News>>
	{
		/**
		 * The Constant for preference that use in startActivityforResult
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

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
		{
			return inflater.inflate(R.layout.headlines_view, container);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			// We have a menu item to show in the menu bar.
			setHasOptionsMenu(true);

			mPrefs = new PrefsUtils(getActivity());

			// Get a new Data
			getNewContents();

			// Create an empty adapter we will use to display the loaded data.
			final NewsAdapter mAdapter = new NewsAdapter(getActivity());
			setListAdapter(mAdapter);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
		{
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
				// Launch settings
				startActivityForResult(new Intent(getActivity(),
					SettingsActivity.class), PREFERENCE);
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
			((TextView) getView().findViewById(R.id.list_header_title)).
			setText(mPrefs.getNewsTitleFromPrefs());

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

			// if data is null, return early
			if (feed == null) {
				return;
			}

			// If data is not null, add it to NewsAdapter.
			for (News news : feed) {
				((NewsAdapter) getListAdapter()).add(news);
			}
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			final String link = ((News) getListAdapter().getItem(position)).
			getLink();

			// Check what fragment is shown if it is null then load new activity
			final WebDetailsFragment viewer =
			    (WebDetailsFragment) getActivity().getSupportFragmentManager().
			    findFragmentById(R.id.headlinesDetails);
			if (viewer == null || !viewer.isInLayout()) {
				final Intent intent = new Intent(getActivity(),
					WebDetailsActivity.class);
				intent.putExtra("url", link);
				startActivity(intent);
			}
			else {
				viewer.updateUrl(link);
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
			getNewContents();
		}
	}

	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */
	public static class ScheduleDetailsActivity extends FragmentActivity
	{
	 	@Override
	 	protected void onCreate(Bundle savedInstanceState)
	 	{
	 	 	super.onCreate(savedInstanceState);
	 	 	setContentView(R.layout.schedule_details);

	 	 	final Bundle getExtra = getIntent().getExtras();
	 	 	final ScheduleDetailsFragment viewer =
		                     (ScheduleDetailsFragment)
		            getSupportFragmentManager().findFragmentById(
		            	R.id.scheduleDetails);
		    viewer.updateText(getExtra.getString("school"),
		     	 getExtra.getString("date"),
		     	 getExtra.getString("time"),
		     	 getExtra.getString("tv")
		    );
		}
	}

	/**
	 * A fragment that {@link android.support.v4.app.ListFragment}
	 * to load a specific item for schedule.
	 */
	public static class ScheduleFragment extends ListFragment
	       implements AsyncTaskCompleteListener<List<Map<String, String>>>
	{
		/**
		 * The member variable to hold JSONBackground reference.
		 */
		private JSONBackgroundTask mTask;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			mTask = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(
				getResources().getString(R.string.schedule_url));
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

			final ScheduleDetailsFragment viewer = (ScheduleDetailsFragment)
			    getActivity().getSupportFragmentManager().findFragmentById(
			    	R.id.scheduleDetails);
			if (viewer == null || !viewer.isInLayout()) {
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
			else {
				viewer.updateText(school, date, time, tv);
			}
		}
	}

	/**
	 * A fragment that {@link android.support.v4.app.ListFragment}
	 * to load a specific item for link.
	 */
	public static class LinkFragment extends ListFragment
	{
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			setListAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.linkNames, android.R.layout.simple_list_item_1));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			super.onListItemClick(l, v, position, id);
			final String url = getResources().getStringArray(
				R.array.linkValues)[position];
			// Take string from url and parse it to the default browsers
			IntentUtils.openBrowser(getActivity(), url);
		}
	}

	/**
	 * A fragment that {@link android.support.v4.app.ListFragment}
	 * to load a specific item for staff.
	 */
	public static class StaffFragment extends ListFragment
	       implements AsyncTaskCompleteListener<List<Map<String, String>>>
	{
		/**
		 * The member variable to hold JSONBackground reference.
		 */
		private JSONBackgroundTask mTask;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			mTask = (JSONBackgroundTask) new JSONBackgroundTask(this).execute(
				getResources().getString(R.string.staff_url));
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
				android.R.layout.simple_list_item_2,
				new String[] {"name", "positions"},
				new int[] {android.R.id.text1, android.R.id.text2}));
		}
	}
}
