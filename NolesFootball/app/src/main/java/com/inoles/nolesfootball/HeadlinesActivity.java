package com.inoles.nolesfootball;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.inoles.nolesfootball.model.News;
import com.inoles.nolesfootball.parser.HeadlinesXMLParser;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;

public class HeadlinesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBarToolbar.setTitle(R.string.navdrawer_headlines);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new HeadlinesFragment())
                    .commit();
        }
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity.
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = mInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            News news = getItem(position);

            holder.mTitle.setText(news.Title);
            holder.mDesc.setText(news.Descriptions);

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
        private static final String LOG_TAG = HeadlinesFragment.class.getName();

        private NewsAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mAdapter = new NewsAdapter(getActivity());
            setListAdapter(mAdapter);

            HeadlinesXMLParser parser = new HeadlinesXMLParser();
            AppObservable.bindFragment(this, parser.pullDataFromNetwork())
                    .lift(new BindsAdapter())
                    .subscribe();
        }

        @Override
        public void onListItemClick(ListView parent, View view, int position, long id) {
            News news = (News) parent.getAdapter().getItem(position);
            Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
            intent.putExtra("url", news.Link);
            startActivity(intent);
        }

        final class BindsAdapter implements Observable.Operator<List<News>, List<News>> {
            @Override
            public Subscriber<? super List<News>> call(Subscriber<? super List<News>> subscriber) {
                return new Subscriber<List<News>>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(List<News> newses) {
                        mAdapter.add(newses);
                    }
                };
            }
        }
    }
}
