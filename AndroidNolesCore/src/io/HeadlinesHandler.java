/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.io;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.Lists;
import com.itnoles.shared.util.News;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class HeadlinesHandler extends DefaultHandler
{
    private List<News> mListNews;
    private News mNews;
    private StringBuilder mBuilder;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        super.characters(ch, start, length);
        mBuilder.append(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        mListNews = Lists.newArrayList();
        mBuilder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);
        if ("item".equals(localName) || SportsConstants.ENTRY.equals(localName)) {
            mNews = new News();
        }
        if (mNews != null) {
            if (SportsConstants.LINK.equals(localName) && attributes.getLength() > 0) {
                final String url = attributes.getValue("href");
                mNews.setLink(url);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        super.endElement(uri, localName, name);
        if (mNews != null) {
            if ("item".equals(localName) || SportsConstants.ENTRY.equals(localName)) {
                mListNews.add(mNews);
            }
            else {
                final String text = mBuilder.toString().trim();
                mNews.setValue(localName, text);
            }
        }
        mBuilder.setLength(0);
    }

    public List<News> getFeeds()
    {
        return mListNews;
    }
}