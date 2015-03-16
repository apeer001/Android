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
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.inoles.nolesfootball.model.Event;
import com.inoles.nolesfootball.parser.SchedulesXMLParser;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ScheduleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new ScheduleFragment())
                    .commit();
        }
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity.
     */
    @Override
    int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SCHEDULES;
    }

    public static class ScheduleAdapter extends AbstractBaseAdapter<Event> {
        private static final String FSU = "Florida State";

        private final SimpleDateFormat mDateFormat =
                new SimpleDateFormat("MMM dd 'at' hh:mma", Locale.US);

        public ScheduleAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.schedule_item, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Event event = getItem(position);
            if ("A".equals(event.home_away)) {
                holder.mAwayTeam.setText(FSU, event.mHomeScore);
                holder.mHomeTeam.setText(event.mOpponentName, event.mOpponentScore);
            } else {
                holder.mAwayTeam.setText(event.mOpponentName, event.mOpponentScore);
                holder.mHomeTeam.setText(FSU, event.mHomeScore);
            }

            holder.mDate.setText(mDateFormat.format(event.mEventDate));

            return view;
        }
    }

    static class ViewHolder {
        public final TextView mDate;
        public final ScheduleTextView mAwayTeam;
        public final ScheduleTextView mHomeTeam;

        ViewHolder(View v) {
            mDate = (TextView) v.findViewById(R.id.date);
            mAwayTeam = (ScheduleTextView) v.findViewById(R.id.away_team);
            mHomeTeam = (ScheduleTextView) v.findViewById(R.id.home_team);
        }
    }

    public static class ScheduleFragment extends Fragment {
        /**
         * The fragment's ListView/GridView.
         */
        private AbsListView mListView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_schedule, container, false);
            mListView = (AbsListView) view.findViewById(android.R.id.list);
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setEmptyText();

            // Set the adapter
            ScheduleAdapter adapter = new ScheduleAdapter(getActivity());
            mListView.setAdapter(adapter);

            SchedulesXMLParser parser = new SchedulesXMLParser();
            parser.pullDataFromNetwork()
                    .compose(RxUtils.<Event>applyFragmentSchedulers(this))
                    .lift(new BindsAdapter<>(adapter))
                    .subscribe();
        }

        /**
         * The default content for this Fragment has a TextView that is shown when
         * the list is empty. If you would like to change the text, call this method
         * to supply the text it should use.
         */
        public void setEmptyText() {
            View emptyView = mListView.getEmptyView();

            if (emptyView instanceof TextView) {
                ((TextView) emptyView).setText("No content");
            }
        }
    }
}
