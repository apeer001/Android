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
package com.itnoles.shared;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Xml;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * It is the AsyncTask class for Atom and Feed Parsing.
 * @author Jonathan Steele
 */
public class FeedBackgroundTask extends AsyncTask<String, Void, List<News>>
{
	/**
	 * the field for Android's Log Tag.
	 */
	private static final String LOG_TAG = "FeedBackgroundTask";

	/**
	 * the member variable to hold AsyncTaskCompleteListener reference.
	 */
	private AsyncTaskCompleteListener<List<News>> mCallback;

	/**
	 * The XML Tag field for Pub Date.
	 */
	private static final String PUB_DATE = "pubDate";

	/**
	 * The XML Tag field for Link.
	 */
	private static final String LINK = "link";

	/**
	 * The XML Tag field for Description.
	 */
	private static final String DESCRIPTION = "description";

	/**
	 * The XML Tag field for Title.
	 */
	private static final String TITLE = "title";

	/**
	 * The XML Tag field for item.
	 */
	private static final String ITEM = "item";

	/**
	 * The XML Tag field for entry.
	 */
	private static final String ENTRY = "entry";

	/**
	 * The XML Tag field for published.
	 */
	private static final String PUBLISHED = "published";

	/**
	 * The XML Tag field for content.
	 */
	private static final String CONTENT = "content";

	/**
	 * Constructor.
	 * @param callback reference for AsyncTaskCompleteListener
	 */
	public FeedBackgroundTask(AsyncTaskCompleteListener<List<News>> callback)
	{
		this.mCallback = callback;
	}

	/**
	 * perform a computation on a background thread.
	 * @param params reference for string array
	 * @return List<News>
	 */
	@Override
	protected List<News> doInBackground(String... params)
	{
		final AndroidHttpClient client = AndroidHttpClient.newInstance(
			Constants.USER_AGENT);
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			final HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(LOG_TAG, "Error " + statusCode
					+ " while retrieving content from " + params[0]);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(entity.getContent(),
						Constants.BUF_SIZE);
					return parse(inputStream);
				}
				finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		}
		catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "bad feed parsing", e);
		}
		finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	/**
	 * Runs on the UI thread after doInBackground.
	 * @param result reference for List<News>
	 */
	@Override
	protected void onPostExecute(List<News> result)
	{
		mCallback.onTaskComplete(result);
	}

	/**
	 * parse XMLPullReader from InputStream.
	 * @param stream reference for InputStream
	 * @throws Exception something gone wrong
	 * @return new List with News objects
	 */
	private List<News> parse(InputStream stream) throws Exception
	{
		List<News> news = null;
		final XmlPullParser parser = Xml.newPullParser();
		// auto-detect the encoding from the stream
		parser.setInput(stream, null);
		int eventType = parser.getEventType();
		News currentNews = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				news = new ArrayList<News>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase(ITEM)
					|| name.equalsIgnoreCase(ENTRY))
				{
					currentNews = new News();
				}
				else if (currentNews != null) {
					if (name.equalsIgnoreCase(LINK)) {
						if (parser.getAttributeCount() > 0) {
							currentNews.setLink(parser.getAttributeValue(
								null, "href"));
						}
						else {
							currentNews.setLink(parser.nextText());
						}
					}
					else if (name.equalsIgnoreCase(PUB_DATE)
						|| name.equalsIgnoreCase(PUBLISHED))
					{
						currentNews.setPubDate(parser.nextText());
					}
					else if (name.equalsIgnoreCase(TITLE)) {
						currentNews.setTitle(parser.nextText());
					}
					else if (name.equalsIgnoreCase(DESCRIPTION)
						|| name.equalsIgnoreCase(CONTENT))
					{
						currentNews.setDesc(parser.nextText().replaceAll(
							"\\<.*?\\>", ""));
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase(ITEM)
					|| name.equalsIgnoreCase(ENTRY) && currentNews != null)
				{
					news.add(currentNews);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		return news;
	}
}
