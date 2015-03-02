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

package com.inoles.nolesfootball;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inoles.nolesfootball.model.Rosters;
import com.inoles.nolesfootball.widget.SlidingTabLayout;

public class RostersActivity extends BaseActivity implements RostersFragment.Listener {
    private RostersListAdapter mAdapter;

    @Override
    int getLayoutResource() {
        return R.layout.activity_rosters;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up ViewPager and adapter
        final ViewPager pager = (ViewPager) findViewById(R.id.rosters_pager);
        RostersPagerAdapter pagerAdapter = new RostersPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.rosters_sliding_tabs);
        slidingTabLayout.setViewPager(pager);

        mAdapter = new RostersListAdapter(this);

        // TODO: Trying to run this in parallel
        /*RostersXMLParser parser = new RostersXMLParser();
        parser.pullDataFromNetwork()*/
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity.
     */
    @Override
    int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_ROSTERS;
    }

    @Override
    public void onFragmentViewCreated(ListFragment fragment) {
        /*int position = fragment.getArguments().getInt("position");
        switch(position) {
            case 0:
                Collections.sort(mPlayerList, Rosters.NAME);
                mAdapter.add(mPlayerList);
                break;
            case 1:
                Collections.sort(mPlayerList, Rosters.NUMBER);
                mAdapter.add(mPlayerList);
                break;
            case 2:
                Collections.sort(mStaffList, Rosters.NAME);
                mAdapter.add(mStaffList);
                break;
        }*/
        fragment.setListAdapter(mAdapter);
    }

    static class RostersPagerAdapter extends FragmentPagerAdapter {
        private static final String[] TITLES = {"Name", "Number", "Staff"};

        RostersPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RostersFragment fragment = new RostersFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    static class RostersListAdapter extends AbstractBaseAdapter<Rosters> {
        RostersListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflater.inflate(R.layout.rosters_item, viewGroup, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Rosters rosters = getItem(position);

            viewHolder.mLastName.setText(rosters.mLastName);
            viewHolder.mFirstName.setText(rosters.mFirstName);
            viewHolder.mPosition.setText(rosters.mPosition);

            return view;
        }
    }

    static class ViewHolder {
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mPosition;

        ViewHolder(View v) {
            mFirstName = (TextView) v.findViewById(R.id.first_name);
            mLastName = (TextView) v.findViewById(R.id.last_name);
            mPosition = (TextView) v.findViewById(R.id.position);
        }
    }
}
