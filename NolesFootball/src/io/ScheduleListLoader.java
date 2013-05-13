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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.nolesfootball.io;

import android.content.Context;
import android.util.JsonReader;

import com.itnoles.nolesfootball.io.model.Schedule;
//import com.itnoles.nolesfootball.util.HttpConnectionHelper;
import com.itnoles.nolesfootball.util.Lists;
import com.itnoles.nolesfootball.util.LogUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A custom Loader that loads all of the headlines.
 */
public class ScheduleListLoader extends FeedListLoader<Schedule> {
    private static final String TAG = LogUtils.makeLogTag(ScheduleListLoader.class);

    private String mHeader;
    private List<Schedule> mResults = Lists.newArrayList();

    public ScheduleListLoader(Context context) {
        super(context, null);
    }

    public String getHeader() {
        return mHeader;
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<Schedule> loadInBackground() {
        try {
            InputStreamReader is = new InputStreamReader(getContext().getAssets().open("noles_schedule.json"));
            JsonReader reader = new JsonReader(is);
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("header".equals(name)) {
                    mHeader = reader.nextString();
                } else if ("schedule".equals(name)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        mResults.add(readScheduleObject(reader));
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
        } catch (IOException ioe) {
            LogUtils.LOGW(TAG, "Problem reading I/O for ", ioe);
        }
        return mResults;
    }

    /*public void onPostExecute(InputStreamReader is) throws IOException {
        JsonReader reader = new JsonReader(is);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("header".equals(name)) {
                mHeader = reader.nextString();
            } else if ("schedule".equals(name)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    mResults.add(readScheduleObject(reader));
                }
                reader.endArray();
            }
        }
        reader.endObject();
    }*/

    private Schedule readScheduleObject(JsonReader reader) throws IOException {
        String date = null;
        String school = null;
        String tv = null;
        String time = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("date".equals(name)) {
                date = reader.nextString();
            } else if ("school".equals(name)) {
                school = reader.nextString();
            } else if ("tv".equals(name)) {
                tv = reader.nextString();
            } else if ("time".equals(name)) {
                time = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Schedule(date, school, tv, time);
    }
}