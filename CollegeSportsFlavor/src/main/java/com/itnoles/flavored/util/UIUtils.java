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

package com.itnoles.flavored.util;

import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * An assortment of UI helpers.
 */
public class UIUtils {
    private UIUtils() {}

    /**
     * Populate the given {@link TextView} with the requested text, formatting
     * through {@link Html#fromHtml(String)} when applicable.
     * Copy from http://code.google.com/p/iosched/source/browse/android/src/com/google/android/apps/iosched/util/UIUtils.java
     */
    public static void setTextMaybeHtml(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            view.setText("");
            return;
        }
        if (text.contains("<") && text.contains(">")) {
            view.setText(Html.fromHtml(text));
        } else {
            view.setText(text);
        }
    }

}