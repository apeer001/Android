/*
 * Copyright (C) 2015 Jonathan Steele
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.inoles.nolesfootball.parser;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

import com.inoles.nolesfootball.XMLUtils;
import com.inoles.nolesfootball.model.News;

import org.xml.sax.ContentHandler;

import rx.Subscriber;

public class HeadlinesXMLParser extends BaseXMLParser<News> {
    public HeadlinesXMLParser() {
        super("http://www.seminoles.com/rss.dbml?db_oem_id=32900&RSS_SPORT_ID=157113&media=news");
    }

    @Override
    ContentHandler getContentHandler(final Subscriber<? super News> subscriber) {
        final News currentNews = new News();
        RootElement root = new RootElement("rss");
        Element channel = root.getChild("channel");
        Element item = channel.getChild("item");
        item.setEndElementListener(new EndElementListener() {
            public void end() {subscriber.onNext(currentNews.copy());
            }
        });
        item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentNews.title = s;
            }
        });
        item.getChild("link").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentNews.link = s;
            }
        });
        item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentNews.descriptions =  XMLUtils.unescape(s);
            }
        });
        return root.getContentHandler();
    }
}
