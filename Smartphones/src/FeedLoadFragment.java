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

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Xml;
import com.itnoles.shared.News;
import com.itnoles.shared.NewsAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class FeedLoadFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
	public Loader<List<News>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		return new FeedLoader(getActivity(), args.getString("url"));
	}
	
	public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
		if (data == null)
			return;
		for (News news : data)
			((NewsAdapter)getListAdapter()).add(news);
	}
	
	public void onLoaderReset(Loader<List<News>> loader) {
		((NewsAdapter)getListAdapter()).clear();
	}
	
	private static class FeedLoader extends AsyncTaskLoader<List<News>> {
		private String mUri;
		private static final String LOG_TAG = "FeedLoader";
		
		// names of the XML tags
		private static final String PUB_DATE = "pubDate";
		private static final String ENCOL = "enclosure";
		private static final String LINK = "link";
		private static final String DESCRIPTION = "description";
		private static final String TITLE = "title";
		private static final String ITEM = "item";
		private static final String ENTRY = "entry";
		private static final String PUBLISHED = "published";
		private static final String CONTENT = "content";
		
		public FeedLoader(Activity activity, String uri)
        {
			super(activity);
			this.mUri = uri;
		}
		
		@Override
		public List<News> loadInBackground()
        {
			// Parse RSS or Atom Feed
			final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			final HttpGet getRequest = new HttpGet(mUri);
			try {
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					Log.w(LOG_TAG, "Error " + statusCode + " while retrieving content from " + mUri);
					return null;
				}
				
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = new BufferedInputStream(entity.getContent(), 1024);
						return parse(inputStream);
					} finally {
						if (inputStream != null)
							inputStream.close();
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				getRequest.abort();
				Log.w(LOG_TAG, "bad feed parsing", e);
			} finally {
				if (client != null)
					client.close();
			}
			return null;
		}
		
		private List<News> parse(InputStream inputStream) throws Exception
		{
			List<News> news = null;
			XmlPullParser parser = Xml.newPullParser();
			// auto-detect the encoding from the stream
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			News currentNews = null;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
                switch (eventType)
				{
					case XmlPullParser.START_DOCUMENT:
						news = new ArrayList<News>();
						break;
					case XmlPullParser.START_TAG:
						String name = parser.getName();
						if (name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ENTRY)) {
							currentNews = new News();
						} else if (currentNews != null) {
							if (name.equalsIgnoreCase(LINK)) {
								if (parser.getAttributeCount() > 0)
									currentNews.setLink(parser.getAttributeValue(null, "href"));
								else
									currentNews.setLink(parser.nextText());
							} else if (name.equalsIgnoreCase(PUB_DATE) || name.equalsIgnoreCase(PUBLISHED))
								currentNews.setPubDate(parser.nextText());
							else if (name.equalsIgnoreCase(TITLE))
								currentNews.setTitle(parser.nextText());
							else if (name.equalsIgnoreCase(DESCRIPTION) || name.equalsIgnoreCase(CONTENT))
								currentNews.setDesc(parser.nextText().replaceAll("<(.|\n)*?>",""));
							else if (name.equalsIgnoreCase(ENCOL)) {
								if (parser.getAttributeCount() > 0)
									currentNews.setImageURL(parser.getAttributeValue(null, "url"));
							}
						}
						break;
					case XmlPullParser.END_TAG:
						String parser_name = parser.getName();
						if (parser_name.equalsIgnoreCase(ITEM) || parser_name.equalsIgnoreCase(ENTRY) && currentNews != null) {
                            try {
                                news.add(currentNews);
                            } catch (Exception e) {
                                Log.w(LOG_TAG, "Something is gone wrong on add data to the List class");
                            }
                        }
						break;
				}
				eventType = parser.next();
			}
			return news;
		}
	}
}