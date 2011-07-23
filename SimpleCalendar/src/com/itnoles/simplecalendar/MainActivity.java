package com.itnoles.simplecalendar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            final SimpleCalendarFragment fragment = new SimpleCalendarFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}