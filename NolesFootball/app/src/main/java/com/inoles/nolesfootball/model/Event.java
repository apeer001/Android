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

package com.inoles.nolesfootball.model;

import java.util.Date;

public class Event {
    public Date mEventDate;
    public String mOpponentName;
    public String mOpponentScore;
    public String home_away;
    public String mHomeScore;

    public Event copy() {
        Event copy = new Event();
        copy.mEventDate = mEventDate;
        copy.mOpponentName = mOpponentName;
        copy.mOpponentScore = mOpponentScore;
        copy.mHomeScore = mHomeScore;
        copy.home_away = home_away;
        copy.mHomeScore = mHomeScore;
        return copy;
    }
}
