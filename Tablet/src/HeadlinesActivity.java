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

import android.app.*; // Activity, Fragment and ListFragment
import android.content.*; // Intent and SharedPreferences
import android.content.res.Configuration;
import android.os.*; // AsyncTask and Bundle
import android.view.*; // LayoutInflater, Menu, MenuItem, MenuInflater, View and ViewGroup
import android.view.ContextMenu.ContextMenuInfo;
import android.util.*; // Log and Xml
import android.webkit.*; // WebView and WebViewClient
import android.widget.*; // AdapterView, ArrayAdapter, TextView and Toast

import com.itnoles.shared.*; // News and Utilities
import com.itnoles.shared.helper.FeedHandler;

public class HeadlinesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headlines_fragment_layer);
	}
	
	public static class TitlesFragment extends ListFragment {
		private static final int PREFERENCE = 0;
		private static final String LOG_TAG = "HeadlinesActivity$TitlesFragment";
		
		private SharedPreferences mPrefs;
		private FeedTask mFeedTask;
		private NewsAdapter mAdapter;
		
		boolean mDualPane;
		int mCurCheckPosition = 0;
		int mShownCheckPosition = -1;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			mPrefs = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
			
			getNewContents();
		
			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			View detailsFrame = getActivity().findViewById(R.id.details);
			mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
		
			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
				mShownCheckPosition = savedInstanceState.getInt("shownChoice", -1);
			}
		
			if (mDualPane) {
				// In dual-pane mode, the list view highlights the selected item.
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				// Make sure our UI is in the correct state.
				showDetails(mCurCheckPosition);
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
		
		// When the users clicked the menu button in their device, it called this method first
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.headlines_menu, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				case 0:
					final Intent pref = new Intent(getActivity(), SettingsActivity.class);
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
		
		/**
		 * It will get a new content when users updated preference or click reload
		 */
		private void getNewContents()
		{
			String defaultTitle = getResources().getStringArray(R.array.listNames)[0];
			View header = Utilities.setHeaderonListView(mPrefs.getString("newstitle", defaultTitle), getActivity());
			getListView().addHeaderView(header, null, false);
			
			String defaultUrl = getResources().getStringArray(R.array.listValues)[0];
			mFeedTask = (FeedTask) new FeedTask().execute(mPrefs.getString("newsurl", defaultUrl));
			
			mAdapter = new NewsAdapter(getActivity());
			setListAdapter(mAdapter);
		}
		
		/**
		 * Helper function to show the details of a selected item, either by
		 * displaying a fragment in-place in the current UI, or starting a
		 * whole new activity in which it is displayed.
		 */
		void showDetails(int index) {
			mCurCheckPosition = index;
			
			News newsList = (News)mAdapter.getItem(index);
			String link = newsList.getLink();
			
			Log.i(LOG_TAG, new Boolean(mDualPane).toString());
			if (mDualPane) {
				// We can display everything in-place with fragments, so update
				// the list to highlight the selected item and show the data.
				getListView().setItemChecked(index, true);

				if (mShownCheckPosition != mCurCheckPosition) {
					// If we are not currently showing a fragment for the new
					// position, we need to create and install a new one.
					DetailsFragment df = DetailsFragment.newInstance(link);

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
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("url", link);
				startActivity(intent);
			}
		}
		
		private class FeedTask extends AsyncTask<String, News, Void> {
			@Override
			protected Void doInBackground(String... params) {
				// Parse RSS or Atom Feed
				FeedHandler handler = new FeedHandler();
				try {
					// Parse the xml-data from InputStream.
					Xml.parse(Utilities.getInputStream(params[0]), Xml.Encoding.UTF_8, handler);
				} catch (Exception e) {
					Log.e(LOG_TAG, "bad feed parsing", e);
				}
				// Parsing has finished.
				// Our handler now provides the parsed data to us.
				for (News news : handler.getMessages())
					publishProgress(news);
				return null;
			}

			@Override
			public void onProgressUpdate(News... values) {
				mAdapter.add(values[0]);
			}

			@Override
			protected void onPostExecute(Void result) {
				if (isCancelled()) return;
				if (mFeedTask != null && mFeedTask.getStatus() != AsyncTask.Status.FINISHED) {
					mFeedTask.cancel(true);
					mFeedTask = null;
				}
			}
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
	
	public static class DetailsFragment extends Fragment {
		private WebView webview;
		/**
		 * Create a new instance of DetailsFragment, initialized to
		 * show the text at 'url'.
		 */
		public static DetailsFragment newInstance(String urlString) {
			DetailsFragment f = new DetailsFragment();
			
			// Supply index input as an argument.
			Bundle args = new Bundle();
			args.putString("url", urlString);
			f.setArguments(args);
			return f;
		}
	
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
			
			webview = new WebView(getActivity());
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setBuiltInZoomControls(true);
			webview.setWebViewClient(new WebViewClient() {
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
				}
			});
			webview.loadUrl(getArguments().getString("url"));
			return webview;
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			
			webview.destroy();
			webview = null;
		}
	}
	
	public static class DetailsActivity extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// If the screen is now in landscape mode, we can show the
				// dialog in-line with the list so we don't need this activity.
				finish();
				return;
			}

			if (savedInstanceState == null) {
				// During initial setup, plug in the details fragment.
				DetailsFragment details = new DetailsFragment();
				details.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
			}
		}
	}
}