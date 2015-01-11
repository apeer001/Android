package com.inoles.nolesfootball.parser;

import com.inoles.nolesfootball.model.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeadlinesXMLParser extends BaseXMLParser<News> {

    public HeadlinesXMLParser() {
        super("http://www.seminoles.com/rss.dbml?db_oem_id=32900&RSS_SPORT_ID=157113&media=news");
    }

    @Override
    List<News> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<News> result = new ArrayList<>();
        News currentNews = null;
        int eventType;
        while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("item".equals(name)) {
                        currentNews = new News();
                    } else if (currentNews != null) {
                        switch (name) {
                            case "title":
                                currentNews.Title = parser.nextText();
                                break;
                            case "link":
                                currentNews.Link = parser.nextText();
                                break;
                            case "description":
                                currentNews.Descriptions = parser.nextText();
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(name)) {
                        result.add(currentNews);
                    }
                    break;
            }
        }
        return result;
    }
}
