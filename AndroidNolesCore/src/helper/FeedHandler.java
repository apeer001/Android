package com.itnoles.shared.helper;

import org.xml.sax.*; // Attributes and SAXException
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import java.text.*; //SimpleDateFormat and ParseException
import java.util.*; // ArrayList and List

import com.itnoles.shared.News;

public class FeedHandler extends DefaultHandler {
	private static final String TAG = "RssHandler";
	
	// names of the XML tags
	private static final String PUB_DATE = "pubDate";
	private static final String LINK = "link";
	private static final String TITLE = "title";
	private static final String ITEM = "item";
	private static final String ENTRY = "entry";
	private static final String PUBLISHED = "published";
	
	// Common Atom Format
	protected static final SimpleDateFormat ISO8601_DATE_FORMATS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final List<News> messages = new ArrayList<News>();
	private News currentMessage;
	private StringBuilder builder;
	private String mHrefAttribute; // href attribute from link element in Atom format

	public List<News> getMessages()
	{
		return messages;
	}
	
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		builder = new StringBuilder();
		if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY))
			currentMessage = new News();
		else if (localName.equalsIgnoreCase("enclosure")) {
			if (attributes != null)
				currentMessage.setImageURL(attributes.getValue("url"));
		} else if (localName.equalsIgnoreCase("link")) {
			// Get href attribute from link element for Atom format
			if (attributes != null)
				mHrefAttribute = attributes.getValue("href");
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (currentMessage != null)
		{
			if (localName.equalsIgnoreCase(TITLE))
				currentMessage.setTitle(builder.toString().trim());
			else if (localName.equalsIgnoreCase(LINK)) {
				if (mHrefAttribute != null)
					currentMessage.setLink(mHrefAttribute);
				else
					currentMessage.setLink(builder.toString().trim());
			}
			else if (localName.equalsIgnoreCase(PUB_DATE))
				currentMessage.setPubdate(builder.toString().trim());
			else if (localName.equalsIgnoreCase(PUBLISHED))
				setDate(ISO8601_DATE_FORMATS);
			else if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY))
				messages.add(currentMessage);
			
			// Reset the String Builder to Zero
			builder.setLength(0);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}
	
	private void setDate(SimpleDateFormat sdf)
	{
		try {
			currentMessage.setPubdate(sdf.parse(builder.toString().trim()).toString());
		} catch (ParseException e) {
			Log.e(TAG, "bad date format", e);
		}
	}
}