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

import com.inoles.nolesfootball.model.Event;

import org.xml.sax.ContentHandler;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import rx.Subscriber;

public class SchedulesXMLParser extends BaseXMLParser<Event> {

    private final SimpleDateFormat simpleDateFormat;

    public SchedulesXMLParser() {
        super("http://www.seminoles.com/XML/services/v2/schedules.v2.dbml?DB_OEM_ID=32900&spid=157113");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    }

    @Override
    ContentHandler getContentHandler(final Subscriber<? super Event> subscriber) {
        final Event currentEvent = new Event();
        RootElement rootElement = new RootElement("schedules");
        Element scores = rootElement.getChild("schedule_score");
        scores.setEndElementListener(new EndElementListener() {
            @Override
            public void end() { subscriber.onNext(currentEvent.copy());
            }
        });
        scores.getChild("opponent_name").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentEvent.mOpponentName = s;
            }
        });
        scores.getChild("game_date_time_gmt").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentEvent.mEventDate = simpleDateFormat.parse(s, new ParsePosition(0));
            }
        });
        scores.getChild("home_away").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentEvent.home_away = s;
            }
        });
        scores.getChild("home_score").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentEvent.mHomeScore = s;
            }
        });
        scores.getChild("opponent_score").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String s) {
                currentEvent.mOpponentScore = s;
            }
        });
        return rootElement.getContentHandler();
    }
}

