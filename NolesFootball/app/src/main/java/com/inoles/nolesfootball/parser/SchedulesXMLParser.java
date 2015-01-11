package com.inoles.nolesfootball.parser;

import com.inoles.nolesfootball.model.Event;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SchedulesXMLParser extends BaseXMLParser<Event> {

    public SchedulesXMLParser() {
        super("http://www.seminoles.com/XML/services/v2/schedules.v2.dbml?DB_OEM_ID=32900&spid=157113");
    }

    @Override
    List<Event> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        List<Event> result = new ArrayList<>();
        Event currentEvents = null;
        int eventType;
        while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("schedule_score".equals(name)) {
                        currentEvents = new Event();
                    } else if (currentEvents != null) {
                        switch (name) {
                            case "opponent_name":
                                currentEvents.mOpponentName = parser.nextText();
                                break;
                            case "game_date_time_gmt":
                                currentEvents.mEventDate =
                                        sdf.parse(parser.nextText(), new ParsePosition(0));
                                break;
                            case "home_away":
                                currentEvents.home_away = parser.nextText();
                                break;
                            case "home_score":
                                currentEvents.mHomeScore = parser.nextText();
                                break;
                            case "opponent_score":
                                currentEvents.mOpponentScore = parser.nextText();
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("schedule_score".equals(name)) {
                        result.add(currentEvents);
                    }
                    break;
            }
        }
        return result;
    }
}
