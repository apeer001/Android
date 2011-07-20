/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
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
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
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