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

public class ScheduleTextView extends TextView {
    private String mNameText;
    private String mScoreText;

    private Paint mNamePaint;
    private Paint mScorePaint;

    public ScheduleTextView(Context context) {
        super(context, null);
    }

    public ScheduleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            mNameText = "Ball State";
            mScoreText = "30";
        }

        mNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mNamePaint.setTextSize(getTextSize());
        mScorePaint.setTextSize(getTextSize());

        mNamePaint.setColor(getCurrentTextColor());
        mScorePaint.setColor(getCurrentTextColor());

        mNamePaint.setFlags(getPaintFlags());
        mScorePaint.setFlags(getPaintFlags());

        mScorePaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setText(String name, String score) {
        mNameText = name;
        mScoreText = score;

        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int height = getBaseline();

        if (!TextUtils.isEmpty(mNameText)) {
            canvas.drawText(mNameText, 0, height, mNamePaint);
        }

        if (!TextUtils.isEmpty(mScoreText)) {
            canvas.drawText(mScoreText, getMeasuredWidth(), height, mScorePaint);
        }
    }
}
