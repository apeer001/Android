// Code Shared from http://iamvijayakumar.blogspot.com/2011/06/android-simple-calender-in-gridview.html
package com.itnoles.simplecalendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleCalendarFragment extends Fragment implements View.OnClickListener
{
    private static final String LOG_TAG = "CalendarFragment";
    private static final String dateTemplate = "MMMM yyyy";

    private final DateFormat dateFormatter = new DateFormat();

    private GridView mGridView;
    private Calendar mCalendar;
    private DisplayMetrics mMetrics;
    private int month, year;
    private TextView currentMonth;
    private ImageView prevMonth, nextMonth;
    
    private Map<String, String> getMap()
    {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("date", "9.3.2011");
        map.put("time", "3:30 pm");
        map.put("school", "Louisiana-Monroe");
        return map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.simple_calendar_view, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mGridView = (GridView) getView().findViewById(R.id.calendar);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id)
	       {
	           final CalendarListFragment events = (CalendarListFragment) getFragmentManager().findFragmentById(R.id.events);
	           final String tag = v.getTag().toString();
	           if (TextUtils.isEmpty(tag) || events == null) {
	               return;
                }
                final List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                if ("9.3.2011".equals(tag)) {
                    entries.add(getMap());
                }
                Log.i(LOG_TAG, "Tag = " + tag);
                events.display(entries);
            }
        });

        mCalendar = Calendar.getInstance();
        month = mCalendar.get(Calendar.MONTH) + 1;
        year = mCalendar.get(Calendar.YEAR);
        final int week_month = mCalendar.get(Calendar.WEEK_OF_MONTH);
        Log.d(LOG_TAG, "Calendar Instance: Month: " + month + ", Year: " + year + ", Week Of Month: " + week_month);

        prevMonth = (ImageView) getView().findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) getView().findViewById(R.id.currentMonth);
        currentMonth.setText(dateFormatter.format(dateTemplate, mCalendar.getTime()));

        nextMonth = (ImageView) getView().findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        getAdapter(month, year);
    }

    private void getAdapter(int month, int year)
    {
        mGridView.setAdapter(new MonthAdapter(getActivity(), month, year, mMetrics));
    }

    private void setGridCellAdapterToDate(int month, int year)
    {
        mCalendar.set(year, month - 1, mCalendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(dateFormatter.format(dateTemplate, mCalendar.getTime()));
        getAdapter(month, year);
    }
        
    @Override
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                 month = 12;
                 year--;
	       }
	       else {
	           month--;
	       }
	       Log.d(LOG_TAG, "Setting Prev Month in MonthAdapter: " + "Month: " + month + " Year: " + year);
	       setGridCellAdapterToDate(month, year);
	   }
	   if (v == nextMonth) {
	       if (month > 11) {
	           month = 1;
	           year++;
		  }
		  else {
		      month++;
		  }
		  Log.d(LOG_TAG, "Setting Next Month in : MonthAdapter" + "Month: " + month + " Year: " + year);
		  setGridCellAdapterToDate(month, year);
	   }
    }
}