// Orginial from https://github.com/jrdnull/Android-Calendar-GridView-Adapter/blob/master/MonthAdapter.java
package com.itnoles.simplecalendar;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class MonthAdapter extends BaseAdapter
{
    private static final int mTitleHeight = 30;
    private static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private static final String[] mDays = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static final int[] mDaysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    private GregorianCalendar mCalendar;
    private Calendar mCalendarToday;
    private LayoutInflater mInflater;
    private DisplayMetrics mDisplayMetrics;
    private List<String> mItems;
    private Map<String, String> mData;
    private GridView mGrid;
    private int mMonth, mYear, mDaysShown, mDaysLastMonth, mDaysNextMonth, mDayHeight;

	public MonthAdapter(Context context, int month, int year, DisplayMetrics metrics, Map<String, String> data, GridView grid)
	{
		mInflater = LayoutInflater.from(context);
		mMonth = month;
		mYear = year;
		mCalendar = new GregorianCalendar(year, month - 1, 1);
		mCalendarToday = Calendar.getInstance();
		mDisplayMetrics = metrics;
		mData = data;
		mGrid = grid;
		populateMonth();
	}
	
	private int getNumberOfDaysOfMonth(int i)
	{
	    return mDaysInMonth[i];
     }

	private void populateMonth()
	{
		mItems = new ArrayList<String>();		
		for (String day : mDays) {
			mItems.add(day);
			mDaysShown++;
		}

		final int firstDay = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
		final int currentMonth = mMonth - 1;
		int prevDay;
		if (mMonth == 0)
			prevDay = getNumberOfDaysOfMonth(mMonth);
		else
			prevDay = getNumberOfDaysOfMonth(currentMonth);
		for (int i = 0; i < firstDay; i++) {
			mItems.add(String.valueOf(prevDay - firstDay + i));
			mDaysLastMonth++;
			mDaysShown++;
		} 

		int daysInMonth = getNumberOfDaysOfMonth(currentMonth);
		if (currentMonth == Calendar.FEBRUARY && mCalendar.isLeapYear(mYear)) {
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

		switch(mDisplayMetrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
		    mDayHeight = 37;
		    break;
		case DisplayMetrics.DENSITY_HIGH:
		    mDayHeight = 75;
		    break;
		case DisplayMetrics.DENSITY_XHIGH:
		    mDayHeight = 100;
		    break;
		default:
		case DisplayMetrics.DENSITY_MEDIUM:
		    mDayHeight = 50; //3.2
	    }
	}

	private boolean isToday(int day, int month, int year)
	{
		if ((mCalendarToday.get(Calendar.MONTH) + 1) == month
				&& mCalendarToday.get(Calendar.YEAR) == year
				&& mCalendarToday.get(Calendar.DAY_OF_MONTH) == day) {
			return true;
		}
		return false;
	}
	
	private int[] getDate(int position)
	{
		int date[] = new int[3];
		if (position <= 6) {
			return null; // day names
		} else if (position <= mDaysLastMonth + 6) {
			// previous month
			date[0] = Integer.parseInt(mItems.get(position));
			if (mMonth == 0) {
				date[1] = 11;	
				date[2] = mYear - 1;
			} else {
				date[1] = mMonth - 1;
				date[2] = mYear;
			}
		} else if (position <= mDaysShown - mDaysNextMonth) {
			// current month
			date[0] = position - (mDaysLastMonth + 6);
			date[1] = mMonth;
			date[2] = mYear;
		} else {
			// next month
			date[0] = Integer.parseInt(mItems.get(position));
			if (mMonth == 11) {
				date[1] = 0;
				date[2] = mYear + 1;
			} else {
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
	     final TextView subDay = (TextView) convertView.findViewById(R.id.subDay);
		dayText.setText(mItems.get(position));

		final int[] date = getDate(position);
		if (date != null) {
			dayText.setHeight(mDayHeight);

		    final int day = date[0];
		    final int month = date[1];
		    final int year = date[2];
		    
		    final String tag = month + "." + day + "." + year;
			if (month != mMonth) {
				// previous or next month
				dayText.setTextColor(Color.LTGRAY);
				subDay.setVisibility(View.GONE);
			} else {
				// current month
				final String date_map = mData.get("date");
				if (date_map.equals(tag)) {
				    if (IS_HONEYCOMB) {
				        subDay.setText(date_map);
				    }
				    subDay.setVisibility(View.VISIBLE);
				}
				else {
				    subDay.setVisibility(View.GONE);
				}

				if (isToday(day, month, year)) {
					dayText.setTextColor(Color.WHITE);
					dayText.setBackgroundColor(Color.BLUE);
					mGrid.setSelection(position);
				}
			}
			convertView.setTag(tag);
		} else {
		     subDay.setVisibility(View.GONE);
			dayText.setHeight(mTitleHeight);
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