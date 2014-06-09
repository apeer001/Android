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

package com.inoles.collegesports;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class RostersTextView extends TextView {
    private static final int PAD = 5;

    private String mFirstText;
    private String mLastText;

    private Paint mFirstPaint;
    private Paint mLastPaint;

    public RostersTextView(Context context) {
        super(context, null);
    }

    public RostersTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            mFirstText = "John";
            mLastText = "Doe";
        }

        mFirstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLastPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mFirstPaint.setTextSize(getTextSize());
        mLastPaint.setTextSize(getTextSize());

        mFirstPaint.setColor(getCurrentTextColor());
        mLastPaint.setColor(getCurrentTextColor());

        mFirstPaint.setFlags(getPaintFlags());
        mLastPaint.setFlags(getPaintFlags());
    }

    public void setText(String first, String last) {
        mFirstText = first;
        mLastText = last;

        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int height = getBaseline();

        if (!TextUtils.isEmpty(mFirstText)) {
            canvas.drawText(mFirstText, 0, height, mFirstPaint);
        }

        if (!TextUtils.isEmpty(mLastText)) {
            float width = mFirstPaint.measureText(mFirstText) + PAD;
            canvas.drawText(mLastText, width, height, mLastPaint);
        }
    }
}
