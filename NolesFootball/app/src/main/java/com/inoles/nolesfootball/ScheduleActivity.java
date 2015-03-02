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

import rx.android.app.AppObservable;

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
     * Returns the navigation drawer_item item that corresponds to this Activity.
     */
    @Override
    int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SCHEDULES;
    }

    public static class ScheduleAdapter extends AbstractBaseAdapter<Event> {
        private static final String FSU = "Florida State";

        private final SimpleDateFormat mDateFormat =
                new SimpleDateFormat("MMM dd 'at' hh:mma", Locale.US);

        ScheduleAdapter(Context context) {
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

    public static class ViewHolder {
        public final TextView mDate;
        public final ScheduleTextView mAwayTeam;
        public final ScheduleTextView mHomeTeam;

        public ViewHolder(View v) {
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
            AppObservable.bindFragment(this, parser.pullDataFromNetwork())
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
