package com.itnoles.simplecalendar;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class CalendarListFragment extends ListFragment
{
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No events");
    }

    public void display(List<Map<String, String>> map)
    {
        final SimpleAdapter adapter = new SimpleAdapter(getActivity(), map,
            android.R.layout.simple_list_item_2, new String[] {"school", "time"},
            new int[] {android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
    }
}