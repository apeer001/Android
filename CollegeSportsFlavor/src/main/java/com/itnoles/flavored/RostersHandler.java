/*
 * Copyright (C) 2013 Jonathan Steele
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

package com.itnoles.flavored;

import com.itnoles.flavored.model.Rosters;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class RostersHandler extends DefaultHandler {
    private final List<Rosters> mResults = new ArrayList<Rosters>();
    private String tempVal;
    private Rosters tempRosters;

    public List<Rosters> getResults() {
        return mResults;
    }

    /**
     * This will be called when the tags of the XML starts.
     **/
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // reset
        tempVal = "";

        if ("player".equals(localName) || "asst_coach_lev1".equals(localName) || "asst_coach_lev2".equals(localName)
            || "asst_coach_lev3".equals(localName) || "head_coach".equals(localName) || "other".equals(localName)) {
                tempRosters = new Rosters(!"player".equals(localName));
        }
    }

    /**
     * This will be called when the tags of the XML end.
     **/
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("asst_coach_lev1".equals(localName) || "asst_coach_lev2".equals(localName) || "asst_coach_lev3".equals(localName)
            || "head_coach".equals(localName) || "other".equals(localName) || "player".equals(localName)) {
            mResults.add(tempRosters);
        } else {
            tempRosters.setValue(localName, tempVal);
        }
    }

    /**
     * This is called to get the tags value
     **/
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
}