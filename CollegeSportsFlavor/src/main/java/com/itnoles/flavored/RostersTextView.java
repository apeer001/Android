/*
 * Copyright (C) 2013 Jonathan Steele
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
/**
 * Orginal Source Code from http://prasanta-paul.blogspot.com/2010/05/android-custom-textview.html
 */
package com.itnoles.flavored;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class RostersTextView extends TextView {
    private static final int pad = 5;

    private final Paint mPaint = new Paint();
    private final Paint mPaintB = new Paint();

    private String firstText;
    private String lastText;

    public RostersTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        firstText = "";
        lastText = "";

        //setPadding(3, 0, 0, 0); // Left, Top, Right, Bottom

        // set Size
        mPaint.setTextSize(18);
        mPaintB.setTextSize(18);

        // set Color
        mPaint.setColor(Color.BLACK);
        mPaintB.setColor(Color.BLACK);
    }

    public RostersTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RostersTextView(Context context) {
        super(context);
    }

    public void setText(String first, String last) {
        firstText = first;
        lastText = last;
        // request for re-draw- calls draw()
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int reqWidth;
        int reqHeight;

        // find out Width based on widthMode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            // set user specified Width
            reqWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            // find out the total pixel size required for first and last text
            reqWidth = (int) (mPaint.measureText(firstText) + mPaintB.measureText(lastText) + 3 * pad);
        }

        // find out Height based on heightMode
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            // set user specified Height
            reqHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            // get the default height of the Font
            reqHeight = (int) mPaintB.getTextSize();
        }

        // set the calculated width and height of your drawing area
        setMeasuredDimension(reqWidth, reqHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = getLeft();
        int y = getTop();

        canvas.drawText(firstText, x, y, mPaint);

        // shift to next word position = (width of the first text) + padding
        x += mPaint.measureText(firstText) + pad;

        canvas.drawText(lastText, x, y, mPaintB);
    }
}