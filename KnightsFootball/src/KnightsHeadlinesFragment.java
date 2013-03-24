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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.knightfootball;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.itnoles.shared.fragment.HeadlinesFragment;

public class KnightsHeadlinesFragment extends HeadlinesFragment {
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.headline_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String title = (String) item.getTitle();
        if (!mTitle.equals(title)) {
            mTitle = title;
        }

        switch (item.getItemId()) {
            case R.id.athletics:
                reloadLoaderWithNewInformation("http://www.ucfathletics.com/headline-rss.xml");
                break;
            case R.id.sports:
                reloadLoaderWithNewInformation("http://ucf.rivals.com/rss2feed.asp?SID=908");
                break;
            case R.id.paper:
                reloadLoaderWithNewInformation("http://www.centralfloridafuture.com/se/central-florida-future-rss-1.991045");
                break;
            case R.id.today:
                reloadLoaderWithNewInformation("http://today.ucf.edu/feed/");
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}