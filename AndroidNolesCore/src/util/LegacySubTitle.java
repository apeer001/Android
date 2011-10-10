/*
 * Copyright (C) 2011 Jonathan Steele
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.util;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.itnoles.shared.R;
import com.itnoles.shared.util.base.ISubTitle;

public class LegacySubTitle implements ISubTitle
{
    public void displaySubTitle(Fragment fragment, String subtitle)
    {
        final TextView vSubTitle = (TextView) fragment.getView().findViewById(R.id.list_header_title);
        vSubTitle.setText(subtitle);
    }
}