package com.inoles.nolesfootball.parser;

import com.inoles.nolesfootball.model.Rosters;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RostersXMLParser extends BaseXMLParser<Rosters> {

    public RostersXMLParser() {
        super("http://www.seminoles.com/XML/services/v2/rosters.v2.dbml?DB_OEM_ID=32900&spid=157113");
    }

    @Override
    List<Rosters> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Rosters> result = new ArrayList<>();
        Rosters currentRosters = null;
        int eventType;
        while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("rosterEntry".equals(name)) {
                        currentRosters = new Rosters();
                    } else if (currentRosters != null) {
                        switch (name) {
                            case "first_name":
                                currentRosters.mFirstName = parser.nextText();
                                break;
                            case "last_name":
                                currentRosters.mLastName = parser.nextText();
                                break;
                            case "position_name":
                                currentRosters.mPosition = parser.nextText();
                                break;
                            case "is_coach":
                                currentRosters.mIsCoach = Integer.parseInt(parser.nextText());
                                break;
                            case "shirt_number":
                                currentRosters.mShirtNumber = parser.nextText();
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("rosterEntry".equals(name)) {
                        result.add(currentRosters);
                    }
                    break;
            }
        }
        return result;
    }
}
