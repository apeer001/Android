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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.widget.SimpleAdapter;
import com.itnoles.shared.Utils;

import java.util.List;
import java.util.Map;

public class StaffActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layer);
		
		// Create the list fragment and add it as our sole content.
		if (getSupportFragmentManager().findFragmentById(R.id.titles) == null) {
			StaffFragment staff = new StaffFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.titles, staff).commit();
		}
	}
		
	public static class StaffFragment extends JSONLoadFragment {
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			Bundle args = Utils.setBundleURL(getResources().getString(R.string.staff_url));
			getActivity().getSupportLoaderManager().initLoader(2, args, this).forceLoad();
		}
		
		public void onLoadFinished(Loader<List<Map<String, String>>> loader, List<Map<String, String>> data) {
			setListAdapter(new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2,
			new String[] {"name", "positions"}, new int[] {android.R.id.text1, android.R.id.text2}));
		}
	}
}