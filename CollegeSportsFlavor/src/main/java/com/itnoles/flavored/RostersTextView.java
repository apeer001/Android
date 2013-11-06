/*
 * Copyright (c) 2013 Jonathan Steele
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.flavored;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class RostersTextView extends TextView {
    private static final int pad = 5;

    private String mFirstText;
    private String mLastText;

    private Paint mFirstPaint;
    private Paint mLastPaint;

    public RostersTextView(Context context) {
        super(context);
        init();
    }

    public RostersTextView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.textAppearanceMedium);
        init();
    }

    private void init() {
        mFirstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFirstPaint.setTextSize(getTextSize());
        mFirstPaint.setColor(getCurrentTextColor());

        mLastPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLastPaint.setTextSize(getTextSize());
        mLastPaint.setColor(getCurrentTextColor());
    }

    public void setText(String first, String last) {
        mFirstText = first;
        mLastText = last;

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // width & height mode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // find out Width based on widthMode
        int reqWidth = (widthMode == MeasureSpec.EXACTLY) ? MeasureSpec.getSize(widthMeasureSpec)
        : (int) (mFirstPaint.measureText(mFirstText) + mLastPaint.measureText(mLastText) + 3 * pad);

        // find out Height based on heightMode
        int reqHeight = (heightMode == MeasureSpec.EXACTLY) ? MeasureSpec.getSize(heightMeasureSpec)
        : (int) (mLastPaint.descent() - mLastPaint.ascent());

        // set the calculated width and height of your drawing area
        setMeasuredDimension(reqWidth, reqHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = this.getMeasuredHeight() -3;

        if (!TextUtils.isEmpty(mFirstText)) {
            canvas.drawText(mFirstText, 0, height, mFirstPaint);
        }

        if (!TextUtils.isEmpty(mLastText)) {
            float width = mFirstPaint.measureText(mFirstText) + pad;
            canvas.drawText(mLastText, width, height, mLastPaint);
        }
    }
}