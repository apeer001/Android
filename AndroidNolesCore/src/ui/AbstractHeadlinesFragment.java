package com.itnoles.shared.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itnoles.shared.R;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.News;
import com.itnoles.shared.util.UrlIntentListener;

public abstract class AbstractHeadlinesFragment extends ListFragment
{
    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (!SportsConstants.SUPPORTS_HONEYCOMB) {
            return inflater.inflate(R.layout.headlines_view, null);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        setListAdapter(new NewsAdapter(getActivity()));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.details);
        // If users click in non-dual pane tabs,
        // it cause this one to be gone too.
        if (detailsFrame != null && detailsFrame.getVisibility() == View.GONE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getListAdapter() != null) {
            ((NewsAdapter) getListAdapter()).clear();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, android.view.MenuInflater inflater)
    {
        // Place an action bar item for settings.
        inflater.inflate(R.menu.newsmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final int getItemId = item.getItemId();
        if (getItemId == R.id.settings) {
            showSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        if (mDualPane) {
            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                final String tag = v.getTag().toString();
                final WebDetailsFragment df = WebDetailsFragment.newInstance(tag);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.details, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        }
        else {
            l.setOnItemClickListener(new UrlIntentListener());
        }
    }

    protected abstract void showSettings();

    protected static class NewsAdapter extends ArrayAdapter<News>
    {
        private LayoutInflater mLayoutInflater;

        public NewsAdapter(Context context)
        {
            super(context, 0);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // A ViewHolder keeps references to children views to avoid
            // unneccessary calls to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.headlines_item, null);

                // Creates a ViewHolder and store references to the three
                // children views we want to bind data to.
                holder = new ViewHolder();
                holder.mTitle = (TextView) convertView.findViewById(R.id.title);
                holder.mDate = (TextView) convertView.findViewById(R.id.date);
                holder.mDesc = (TextView) convertView.findViewById(R.id.description);
                convertView.setTag(R.id.headlines_viewholder, holder);
	        }
	        else {
	            holder = (ViewHolder) convertView.getTag(R.id.headlines_viewholder);
	        }

            final News news = getItem(position);
            holder.mTitle.setText(news.getTitle());
            holder.mDate.setText(news.getPubDate());
            final String text = news.getDesc();
            if (text.contains("<") && text.contains(">")) {
                holder.mDesc.setText(Html.fromHtml(text));
            }
            else {
                holder.mDesc.setText(text);
            }
            convertView.setTag(news.getLink());

            return convertView;
        }

        class ViewHolder
        {
            TextView mTitle;
            TextView mDate;
            TextView mDesc;
        }
    }
}