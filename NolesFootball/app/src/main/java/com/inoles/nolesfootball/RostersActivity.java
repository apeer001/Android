package com.inoles.nolesfootball;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inoles.nolesfootball.model.Rosters;
import com.inoles.nolesfootball.parser.RostersXMLParser;
import com.inoles.nolesfootball.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;

public class RostersActivity extends BaseActivity implements RostersFragment.Listener {
    private static final String LOG_TAG = RostersActivity.class.getName();

    private final List<Rosters> mPlayerList = new ArrayList<>();
    private final List<Rosters> mStaffList = new ArrayList<>();

    private RostersListAdapter mAdapter;

    @Override
    int getLayoutResource() {
        return R.layout.activity_rosters;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager pager = (ViewPager) findViewById(R.id.rosters_pager);
        RostersPagerAdapter pagerAdapter = new RostersPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.rosters_sliding_tabs);
        slidingTabLayout.setViewPager(pager);

        mAdapter = new RostersListAdapter(this);

        RostersXMLParser parser = new RostersXMLParser();
        AppObservable.bindActivity(this, parser.pullDataFromNetwork())
                .lift(new BindsAdapter())
                .subscribe();
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
        int position = fragment.getArguments().getInt("position");
        switch (position) {
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
        }
        fragment.setListAdapter(mAdapter);
    }

    final class BindsAdapter implements Observable.Operator<List<Rosters>, List<Rosters>> {
        @Override
        public Subscriber<? super List<Rosters>> call(Subscriber<? super List<Rosters>> subscriber) {
            return new Subscriber<List<Rosters>>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    Log.e(LOG_TAG, Log.getStackTraceString(e));
                }

                @Override
                public void onNext(List<Rosters> rostersList) {
                    for (Rosters rosters : rostersList) {
                        if (rosters.mIsCoach == 1) {
                            mStaffList.add(rosters);
                        } else {
                            mPlayerList.add(rosters);
                        }
                    }
                }
            };
        }
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
            return 3;
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
        public View getView(int position, View view, ViewGroup viewGroup) {
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
