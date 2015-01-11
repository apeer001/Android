package com.inoles.nolesfootball;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inoles.nolesfootball.model.DrawerPrimaryAction;

import java.util.List;

public class SideDrawerAdapter extends RecyclerView.Adapter<SideDrawerAdapter.ViewHolder> {
    private final int mSelectedItem;
    private final List<DrawerPrimaryAction> mPrimary;
    private final List<String> mSecondary;
    private final OnItemClickListener mListener;

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnItemClickListener {
        public void onClick(int position);
    }

    /**
     * Custom view holder for our navigation drawers views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final Resources mResources;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mResources = v.getResources();
        }
    }

    public SideDrawerAdapter(int selected, List<DrawerPrimaryAction> primary, List<String> secondary,
                             OnItemClickListener listener) {
        mSelectedItem = selected;
        mPrimary = primary;
        mSecondary = secondary;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 2) {
            View secondarySep = inflater.inflate(
                    R.layout.drawer_secondary_actions_separator, parent, false);
            return new ViewHolder(secondarySep);
        }

        TextView view = (TextView) inflater.inflate(R.layout.drawer_action_regular, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                getPrimaryActionView(position, viewHolder, false);
                break;
            case 1:
                getPrimaryActionView(position, viewHolder, true);
                break;
            case 3:
                getSecondaryActionView(position, viewHolder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPrimary.size() + mSecondary.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        int primarySize = mPrimary.size();
        if (position < primarySize) {
            if (mSelectedItem == position) {
                return 1;
            }
            return 0;
        }

        int separator = primarySize - position;
        if (separator == 0) {
            return 2;
        }
        return 3;
    }

    private void getPrimaryActionView(final int position, ViewHolder holder, boolean active) {
        TextView textView = (TextView) holder.mView;
        DrawerPrimaryAction action = mPrimary.get(position);
        textView.setText(action.actionText);
        if (action.iconResId > 0) {
            Drawable icon = holder.mResources.getDrawable(action.iconResId);
            icon.setAlpha(66);
            textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }

        if (active) {
            textView.setTextColor(holder.mResources.getColor(R.color.theme_primary));
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(position);
            }
        });
    }

    private void getSecondaryActionView(final int position, ViewHolder holder) {
        TextView textView = (TextView) holder.mView;
        textView.setText(mSecondary.get(position - (mPrimary.size() + 1)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(position);
            }
        });
    }
}
