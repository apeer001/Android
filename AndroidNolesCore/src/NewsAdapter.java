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
package com.itnoles.shared;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<News> {
	private final LayoutInflater mLayoutInflater;
	private final ImageDownloader imageDownloader;
		
	// Constructor
	public NewsAdapter(Activity activity) {
		super(activity, 0);
		mLayoutInflater = LayoutInflater.from(activity);
		imageDownloader = new ImageDownloader();
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.headlines_item, parent, false);
			
		final News news = getItem(position);
		ImageView thumbnail = (ImageView) convertView.findViewById(R.id.image);
		if (thumbnail != null) {
			final String imageurl = news.getImageURL();
			if (imageurl != null && imageurl.length() > 0)
				imageDownloader.download(imageurl, thumbnail);
			else
				thumbnail.setVisibility(View.GONE);
		}
			
		TextView title = (TextView) convertView.findViewById(R.id.title);
		if (title != null)
			title.setText(news.getTitle());
			
		TextView subTitle = (TextView) convertView.findViewById(R.id.date);
		if (subTitle != null)
			subTitle.setText("Published on " + news.getPubDate());
			
		TextView desc = (TextView) convertView.findViewById(R.id.description);
		if (desc != null)
			desc.setText(news.getDesc());
		return convertView;
	}
}