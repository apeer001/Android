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

package com.itnoles.flavored.model;

public class News {
    public String title;
    public String link;
    public String desc;
    public String pubDate;
    //public String imageURL;

    public void setValue(String key, String value) {
        if ("title".equals(key)) {
            title = value;
        } else if ("pubDate".equals(key)) {
            pubDate = value;
        } else if ("link".equals(key)) {
            link = value;
        } else if ("description".equals(key)) {
            desc = value.replaceAll("<.*>", "");
        } /*else if ("enclosure".equals(key)) {
            imageURL = value;
        }*/
    }
}