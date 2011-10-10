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

package com.itnoles.simplecalendar;

import android.content.Context;
//import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

//import com.itnoles.shared.R;
//import com.itnoles.shared.provider.ScheduleContract.Schedule;
//import com.itnoles.shared.util.Lists;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleFragment extends Fragment
{
    private static final String LOG_TAG = "ScheduleFragment";
    private static final String DATE_TEMPLATE = "MMMM yyyy";

    private final DateFormat mDateFormat = new DateFormat();
    private MonthAdapter mAdapter;
    private static Calendar mCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.simple_calendar_view, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        /*final View detailFrame = getActivity().findViewById(R.id.details);
        if (detailFrame != null) {
            detailFrame.setVisibility(View.GONE);
        }*/
        
        mCalendar = Calendar.getInstance();
        final int month = mCalendar.get(Calendar.MONTH) + 1;
        final int year = mCalendar.get(Calendar.YEAR);
        Log.d(LOG_TAG, "Calendar Instance: Month: " + month + ", Year: " + year);

        final GridView gridView = (GridView) getView().findViewById(R.id.calendar);

        final TextView currentMonth = (TextView) getView().findViewById(R.id.currentMonth);
        currentMonth.setText(mDateFormat.format(DATE_TEMPLATE, mCalendar.getTime()));

        mAdapter = new MonthAdapter(getActivity(), month, year);
        gridView.setAdapter(mAdapter);
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final String[] projection = {"_id", Schedule.DATE, Schedule.TIME, Schedule.SCHOOL};
        return new CursorLoader(getActivity(), Schedule.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }*/

    private static class MonthAdapter extends BaseAdapter
    {
        private static final int DAY_OFFSET = 6;
        private static final int MONTH_OFFSET = 11;
        private static final String[] DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        private GregorianCalendar mGregCalendar;
        private LayoutInflater mInflater;
        private List<String> mItems;
        private int mMonth, mYear, mDaysShown, mDaysLastMonth, mDaysNextMonth;

        public MonthAdapter(Context context, int month, int year)
        {
            mInflater = LayoutInflater.from(context);
	        mMonth = month;
	        mYear = year;
	        mGregCalendar = new GregorianCalendar(year, month - 1, 1);
	        populateMonth();
	    }

	    private void populateMonth()
	    {
	        mItems = new ArrayList<String>();
		    for (String day : DAYS) {
		        mItems.add(day);
		        mDaysShown++;
		    }

		    final int firstDay = mGregCalendar.get(Calendar.DAY_OF_WEEK) - 1;
		    final int currentMonth = mMonth - 1;
		    int prevDay;
		    if (mMonth == 0) {
			    prevDay = DAYS_IN_MONTH[mMonth];
		    }
		    else {
			    prevDay = DAYS_IN_MONTH[currentMonth];
		    }
		    for (int i = 0; i < firstDay; i++) {
			    mItems.add(String.valueOf(prevDay - firstDay + i));
			    mDaysLastMonth++;
			    mDaysShown++;
		    }

		    int daysInMonth = DAYS_IN_MONTH[currentMonth];
		    if (currentMonth == Calendar.FEBRUARY && mGregCalendar.isLeapYear(mYear)) {
			    daysInMonth++;
		    }

		    for (int i = 1; i <= daysInMonth; i++) {
			    mItems.add(String.valueOf(i));
			    mDaysShown++;
		    }

		    mDaysNextMonth = 1;
		    while (mDaysShown % 7 != 0) {
			    mItems.add(String.valueOf(mDaysNextMonth));
			    mDaysShown++;
			    mDaysNextMonth++;
		    }
		}

        private boolean isToday(int day, int month, int year)
        {
            return ((mCalendar.get(Calendar.MONTH) + 1) == month
                 && mCalendar.get(Calendar.YEAR) == year
                 && mCalendar.get(Calendar.DAY_OF_MONTH) == day);
	   }

	    private int[] getDate(int position)
	    {
		    final int[] date = new int[3];
		    if (position <= DAY_OFFSET) {
			    return null; // day names
		    }
		    else if (position <= mDaysLastMonth + DAY_OFFSET) {
			    // previous month
			    date[0] = Integer.parseInt(mItems.get(position));
			    if (mMonth == 0) {
				    date[1] = MONTH_OFFSET;
				    date[2] = mYear - 1;
			    }
			    else {
				    date[1] = mMonth - 1;
				    date[2] = mYear;
			    }
		    }
		    else if (position <= mDaysShown - mDaysNextMonth) {
			    // current month
			    date[0] = position - (mDaysLastMonth + DAY_OFFSET);
			    date[1] = mMonth;
			    date[2] = mYear;
		    }
		    else {
			    // next month
			    date[0] = Integer.parseInt(mItems.get(position));
			    if (mMonth == MONTH_OFFSET) {
				    date[1] = 0;
				    date[2] = mYear + 1;
			    }
			    else {
				    date[1] = mMonth + 1;
				    date[2] = mYear;
			    }
		    }
		    return date;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent)
	    {
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.simple_calendar_cell, null);
	        }

	        final TextView dayText = (TextView) convertView.findViewById(R.id.day);
	        dayText.setText(mItems.get(position));
	        
	        final TextView subDayText = (TextView) convertView.findViewById(R.id.subday);
	        Log.i(LOG_TAG, Integer.toString(subDayText.getHeight()));

		    final int[] date = getDate(position);
		    if (date != null) {
		        final int day = date[0];
		        final int month = date[1];
		        final int year = date[2];

		 	    if (month != mMonth) {
				    // previous or next month
				    dayText.setTextColor(Color.LTGRAY);
			    }
			    else {
			        // current month
			        if (isToday(day, month, year)) {
			            final int today_color = Color.parseColor("#EEEEEE");
			            dayText.setBackgroundColor(today_color);
			            subDayText.setBackgroundColor(today_color);
			        }
		        }
		    }
		    else {
		        subDayText.setVisibility(View.GONE);
		    }

		    return convertView;
	    }

	    @Override
	    public int getCount()
	    {
		    return mItems.size();
	    }

	    @Override
	    public Object getItem(int position)
	    {
		    return mItems.get(position);
	    }

	    @Override
	    public long getItemId(int position)
	    {
		    return position;
	    }
    }
}