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
package com.itnoles.tnawrestling;

import com.itnoles.shared.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.view.View;

public class NewsActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Utilities.NetworkCheck(this);
		new FeedLoadingTask(this, getListView(), "http://feeds2.feedburner.com/tnawrestling/news", null).execute();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		News news = (News) getListView().getAdapter().getItem(position);
		Intent displayWebView = new Intent(this, WebViewActivity.class);
		displayWebView.putExtra("url", news.getLink());
		startActivity(displayWebView);
	}
}