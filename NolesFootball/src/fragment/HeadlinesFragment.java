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

package com.itnoles.nolesfootball.fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.itnoles.shared.R;
import com.itnoles.shared.fragment.AbstractHeadlinesFragment;

public class HeadlinesFragment extends AbstractHeadlinesFragment {
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.headline_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String title = (String) item.getTitle();
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                getLoaderManager().restartLoader(HEADLINES_LOADER, null, this);
                break;
            case R.id.athletics:
                putSourceIntoPreference(title, "http://www.seminoles.com/headline-rss.xml");
                break;
            case R.id.warchant:
                putSourceIntoPreference(title, "http://floridastate.rivals.com/rss2feed.asp?SID=1061");
                break;
            case R.id.digest:
                putSourceIntoPreference(title, "http://rss.scout.com/rss.aspx?sid=16");
                break;
            case R.id.tomahawk:
                putSourceIntoPreference(title, "http://feeds.feedburner.com/sportsblogs/tomahawknation.xml");
                break;
            case R.id.spirit:
                putSourceIntoPreference(title, "http://www.seminoles.com/blog/atom.xml");
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}