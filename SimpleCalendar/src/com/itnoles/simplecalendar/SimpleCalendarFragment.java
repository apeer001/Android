// Code Shared from http://iamvijayakumar.blogspot.com/2011/06/android-simple-calender-in-gridview.html
package com.itnoles.simplecalendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleCalendarFragment extends Fragment
{
    private static final String LOG_TAG = "CalendarFragment";
    private static final String dateTemplate = "MMMM yyyy";

    private final DateFormat dateFormatter = new DateFormat();

    private GridView mGridView;
    private Calendar mCalendar;
    private DisplayMetrics mMetrics;
    private int mMonth, mYear;
    private TextView mCurrentMonth;
    
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
        mCalendar = Calendar.getInstance();
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mYear = mCalendar.get(Calendar.YEAR);
        Log.d(LOG_TAG, "Calendar Instance: Month: " + mMonth + ", Year: " + mYear);
        
        mGridView = (GridView) getView().findViewById(R.id.calendar);

        final ImageView prevMonth = (ImageView) getView().findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (mMonth <= 1) {
                    mMonth = 12;
                    mYear--;
	           }
	           else {
	               mMonth--;
	           }
	           Log.d(LOG_TAG, "Setting Prev Month in MonthAdapter: " + "Month: " + mMonth + " Year: " + mYear);
	           setMonthCellToDate(mMonth, mYear);
            }
        });

        mCurrentMonth = (TextView) getView().findViewById(R.id.currentMonth);
        mCurrentMonth.setText(dateFormatter.format(dateTemplate, mCalendar.getTime()));

        final ImageView nextMonth = (ImageView) getView().findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (mMonth > 11) {
                    mMonth = 1;
                    mYear++;
                }
                else {
                    mMonth++;
		       }
		       Log.d(LOG_TAG, "Setting Next Month in : MonthAdapter" + "Month: " + mMonth + " Year: " + mYear);
		       setMonthCellToDate(mMonth, mYear);
            }
        });

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        getAdapter();
    }

    private void getAdapter()
    {
        mGridView.setAdapter(new MonthAdapter(getActivity(), mMonth, mYear, mMetrics, getMap(), mGridView));
    }

    private void setMonthCellToDate(int month, int year)
    {
        mCalendar.set(year, month - 1, mCalendar.get(Calendar.DAY_OF_MONTH));
        mCurrentMonth.setText(dateFormatter.format(dateTemplate, mCalendar.getTime()));
        getAdapter();
    }
}