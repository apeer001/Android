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
package com.itnoles.nolesfootballxl;

import com.itnoles.shared.activity.HeadlinesFragment;
import com.itnoles.shared.activity.ScheduleFragment;
import com.itnoles.shared.activity.StaffFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity
{
	/** Called when the activity is first created. */
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
	private class TabListener implements ActionBar.TabListener {
		private Fragment mFragment;
		
		public TabListener(Fragment fragment) {
			mFragment = fragment;
		}
		
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.add(R.id.titles, mFragment, null);
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(mFragment);
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	}
	
	public static class LinkFragment extends ListFragment {
		private static final String[] LINK = new String[]{
			"Tickets", "Upcoming Events and Promo", "Seminoles Booster", "Radio Affiliate", "Traditions", "Team Ranking", "Seminoles Podcast"
		};
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			getActivity().findViewById(R.id.details).setVisibility(View.GONE);
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, LINK));
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			final String url;
			switch (position) {
				case 0:
					url = "http://www.seminoles.com/tickets/fsu-tickets.html";
					break;
				case 1:
					url = "http://www.seminoles.com/genrel/nole-zone.html";
					break;
				case 2:
					url = "http://seminole-boosters.fsu.edu/Community/Page.aspx?pid=520&srcid=223";
					break;
				case 3:
					url = "http://www.seminoles.com/multimedia/broadcast.html";
					break;
				case 4:
					url = "http://www.seminoles.com/trads/fsu-trads.html";
					break;
				case 5:
					url = "http://www.seminoles.com/genrel/rankings.html";
					break;
				default:
					url = "http://www.seminoles.com/podcasts/fsu-podcasts.html";
					break;
			}
			
			// Take string from url and parse it to the default browsers
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(viewIntent);
		}
	}
}