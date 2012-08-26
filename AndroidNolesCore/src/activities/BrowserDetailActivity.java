
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

package com.itnoles.shared.activities;

import android.support.v4.app.Fragment;

import com.itnoles.shared.fragment.BrowserDetailFragment;

public class BrowserDetailActivity extends BaseSinglePaneActivity {
    @Override
    protected Fragment onCreatePane() {
        final BrowserDetailFragment details = new BrowserDetailFragment();
        details.setArguments(getIntent().getExtras());
        return details;
    }
}