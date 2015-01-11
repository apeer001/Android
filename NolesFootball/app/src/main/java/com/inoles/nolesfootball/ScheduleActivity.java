package com.inoles.nolesfootball;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inoles.nolesfootball.model.Event;
import com.inoles.nolesfootball.parser.SchedulesXMLParser;

import java.text.SimpleDateFormat;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;

public class ScheduleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new ScheduleFragments())
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

       private final SimpleDateFormat mDateFormat;

       public ScheduleAdapter(Context context) {
           super(context);
           mDateFormat = new SimpleDateFormat("MMM dd 'at' hh:mma");
       }

       @Override
       public View getView(int position, View view, ViewGroup viewGroup) {
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

    public static class ScheduleFragments extends ListFragment {
        private static final String LOG_TAG = ScheduleFragments.class.getName();

        private ScheduleAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mAdapter = new ScheduleAdapter(getActivity());
            setListAdapter(mAdapter);

            SchedulesXMLParser parser = new SchedulesXMLParser();
            AppObservable.bindFragment(this, parser.pullDataFromNetwork())
                    .lift(new BindsAdapter())
                    .subscribe();
        }

        final class BindsAdapter implements Observable.Operator<List<Event>, List<Event>> {
            @Override
            public Subscriber<? super List<Event>> call(Subscriber<? super List<Event>> subscriber) {
                return new Subscriber<List<Event>>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(List<Event> events) {
                        mAdapter.add(events);
                    }
                };
            }
        }
    }
}
