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

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.inoles.nolesfootball.model.News;
import com.inoles.nolesfootball.parser.HeadlinesXMLParser;

import rx.android.app.AppObservable;

public class HeadlinesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setTitle(R.string.navdrawer_headlines);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new HeadlinesFragment())
                    .commit();
        }
    }

    /**
     * Returns the navigation drawer_item item that corresponds to this Activity.
     */
    @Override
    int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HEADLINES;
    }

    public static class NewsAdapter extends AbstractBaseAdapter<News> {
        public NewsAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = mInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            News news = getItem(position);

            holder.mTitle.setText(news.title);
            holder.mDesc.setText(news.descriptions);

            return view;
        }
    }

    public static class ViewHolder {
        public final TextView mTitle;
        public final TextView mDesc;

        public ViewHolder(View v) {
            mTitle = (TextView) v.findViewById(android.R.id.text1);
            mDesc = (TextView) v.findViewById(android.R.id.text2);
        }
    }

    public static class HeadlinesFragment extends ListFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            NewsAdapter adapter = new NewsAdapter(getActivity());
            setListAdapter(adapter);

            HeadlinesXMLParser parser = new HeadlinesXMLParser();
            AppObservable.bindFragment(this, parser.pullDataFromNetwork())
                    .lift(new BindsAdapter<>(adapter))
                    .subscribe();
        }

        @Override
        public void onListItemClick(ListView parent, View view, int position, long id) {
            News news = (News) parent.getAdapter().getItem(position);
            Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
            intent.putExtra("url", news.link);
            startActivity(intent);
        }
    }
}
