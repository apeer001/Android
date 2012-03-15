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

package com.itnoles.shared.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.itnoles.shared.R;

public abstract class AbstractMainActivity extends SherlockFragmentActivity {
    private static final int POST_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_loading_indicator);

        final View progressFrame = findViewById(R.id.loading_indicator);
        progressFrame.postDelayed(new Runnable() {
            public void run() {
                progressFrame.setVisibility(View.GONE);
                final Intent intent = new Intent(AbstractMainActivity.this, getTabbedActivity());
                startActivity(intent);
            }
        }, POST_DELAY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected abstract Class getTabbedActivity();
}