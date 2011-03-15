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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LinkActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layer);
		
		// Create the list fragment and add it as our sole content.
		if (getSupportFragmentManager().findFragmentById(R.id.titles) == null) {
			LinkFragment list = new LinkFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.titles, list).commit();
		}
	}
	
	public static class LinkFragment extends ListFragment {
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.linkNames)));
		}
		
		@Override
		public void onListItemClick(ListView l, View v, final int position, long id) {
			super.onListItemClick(l, v, position, id);
			String url = getResources().getStringArray(R.array.linkValues)[position];
			// Take string from url and parse it to the default browsers
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(viewIntent);
		}
	}
}