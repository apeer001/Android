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

import com.inoles.nolesfootball.model.Rosters;

import org.xml.sax.ContentHandler;

import rx.Subscriber;

public class RostersXMLParser extends BaseXMLParser<Rosters> {

    public RostersXMLParser() {
        super("http://www.seminoles.com/XML/services/v2/rosters.v2.dbml?DB_OEM_ID=32900&spid=157113");
    }

    @Override
    ContentHandler getContentHandler(final Subscriber<? super Rosters> subscriber) {
        final Rosters currentRosters = new Rosters();
        RootElement root = new RootElement("rosters");
        Element rosterEntry = root.getChild("rosterEntry");
        rosterEntry.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                subscriber.onNext(currentRosters.copy());
            }
        });
        rosterEntry.getChild("first_name").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentRosters.mFirstName = s;
            }
        });
        rosterEntry.getChild("last_name").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentRosters.mLastName = s;
            }
        });
        rosterEntry.getChild("position_name").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentRosters.mPosition = s;
            }
        });
        rosterEntry.getChild("shirt_number").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentRosters.mShirtNumber = s;
            }
        });
        return root.getContentHandler();
    }
}
