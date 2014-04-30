
package com.github.mikephil.charting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class LineChart extends BarLineChartBase {

    /** the radius of the circle-shaped value indicators */
    protected float mCircleSize = 4f;

    /** the width of the drawn data lines */
    protected float mLineWidth = 1f;

    /** the width of the highlighning rectangle */
    protected float mHighlightWidth = 0.6f;

    /** if true, the data will also be drawn filled */
    protected boolean mDrawFilled = false;

    /** if true, drawing circles is enabled */
    protected boolean mDrawCircles = true;

    /** paint for the lines of the chart */
    protected Paint mLinePaint;

    /** paint for the filled are (if enabled) below the chart line */
    protected Paint mFilledPaint;

    /** paint for the outer circle of the value indicators */
    protected Paint mCirclePaintOuter;

    /** paint for the inner circle of the value indicators */
    protected Paint mCirclePaintInner;

    /** paint used for highlighting values */
    protected Paint mHighlightPaint;

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mCircleSize = Utils.convertDpToPixel(mCircleSize);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mColorDarkBlue);
        // mLinePaint.setColor(mDrawColor);

        mFilledPaint = new Paint();
        mFilledPaint.setStyle(Paint.Style.FILL);
        mFilledPaint.setColor(mColorDarkBlue);
        mFilledPaint.setAlpha(130); // alpha ~55%

        mCirclePaintOuter = new Paint(Paint.ANTI_ALIAS_FLAG);
        // mCirclePaint.setStrokeWidth(5f);
        mCirclePaintOuter.setStyle(Paint.Style.FILL);
        mCirclePaintOuter.setColor(mColorDarkBlue);

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }

    @Override
    protected void prepareDataPaints(ColorTemplate ct) {

        if (ct == null)
            return;

        mDrawPaints = new Paint[ct.getColors().size()];

        for (int i = 0; i < ct.getColors().size(); i++) {
            mDrawPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDrawPaints[i].setStrokeWidth(mLineWidth);
            mDrawPaints[i].setStyle(Style.STROKE);
            mDrawPaints[i].setColor(ct.getColors().get(i));
        }
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && valuesToHighlight()) {

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                RectF highlight = new RectF(mIndicesToHightlight[i] - mHighlightWidth / 2,
                        mYChartMax, mIndicesToHightlight[i] + mHighlightWidth / 2, mYChartMin);
                transformRect(highlight);
                mDrawCanvas.drawRect(highlight, mHighlightPaint);
            }
        }
    }

    /**
     * draws the given y values to the screen
     */
    @Override
    protected void drawData() {

        Path p = new Path();
        p.moveTo(0, mYVals.get(0));

        for (int x = 1; x < mYVals.size(); x++) {

            p.lineTo(x, mYVals.get(x));
        }

        transformPath(p);

        mDrawCanvas.drawPath(p, mLinePaint);

        // if data is drawn filled
        if (mDrawFilled) {

            Path filled = new Path();
            filled.moveTo(0, mYVals.get(0));

            // create a new path
            for (int x = 1; x < mYVals.size(); x++) {

                filled.lineTo(x, mYVals.get(x));
            }

            // close up
            filled.lineTo(mXVals.size() - 1, mYChartMin);
            filled.lineTo(0f, mYChartMin);
            filled.close();

            transformPath(filled);

            mDrawCanvas.drawPath(filled, mFilledPaint);
        }
    }

    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues && mYVals.size() < mMaxVisibleCount * mScaleX) {

            float[] valuePoints = new float[mYVals.size() * 2];

            for (int i = 0; i < valuePoints.length; i += 2) {
                valuePoints[i] = i / 2;
                valuePoints[i + 1] = mYVals.get(i / 2);
            }

            transformPointArray(valuePoints);

            for (int i = 0; i < valuePoints.length; i += 2) {
                mDrawCanvas.drawText(
                        mFormatValue.format(mYVals.get(i / 2)),
                        valuePoints[i], valuePoints[i + 1] - 12, mValuePaint);
            }
        }
    }

    /**
     * draws the circle value indicators
     */
    @Override
    protected void drawAdditional() {

        // if drawing circles is enabled
        if (mDrawCircles) {

            float[] positions = new float[mYVals.size() * 2];

            for (int i = 0; i < positions.length; i += 2) {
                positions[i] = i / 2;
                positions[i + 1] = mYVals.get(i / 2);
            }

            transformPointArray(positions);

            for (int i = 0; i < positions.length; i += 2) {
                mDrawCanvas.drawCircle(positions[i], positions[i + 1], mCircleSize,
                        mCirclePaintOuter);
                mDrawCanvas.drawCircle(positions[i], positions[i + 1], mCircleSize / 2,
                        mCirclePaintInner);
            }
        }
    }

    /**
     * set this to true to enable the drawing of circle indicators
     * 
     * @param enabled
     */
    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    /**
     * returns true if drawing circles is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawCirclesEnabled() {
        return mDrawCircles;
    }

    /**
     * sets the size (radius) of the circle shpaed value indicators, default
     * size = 4f
     * 
     * @param size
     */
    public void setCircleSize(float size) {
        mCircleSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the circlesize
     * 
     * @param size
     */
    public float getCircleSize(float size) {
        return Utils.convertPixelsToDp(mCircleSize);
    }

    /**
     * set if the chartdata should be drawn as a line or filled default = line /
     * default = false, disabling this will give up to 20% performance boost on
     * large datasets
     * 
     * @param filled
     */
    public void setDrawFilled(boolean filled) {
        mDrawFilled = filled;
    }

    /**
     * returns true if filled drawing is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawFilledEnabled() {
        return mDrawFilled;
    }

    /**
     * set the line width of the chart (min = 0.5f, max = 10f); default 1f NOTE:
     * thinner line == better performance, thicker line == worse performance
     * 
     * @param width
     */
    public void setLineWidth(float width) {

        if (width < 0.5f)
            width = 0.5f;
        if (width > 10.0f)
            width = 10.0f;
        mLineWidth = width;

        mLinePaint.setStrokeWidth(mLineWidth);
    }

    /**
     * returns the width of the drawn chart line
     * 
     * @return
     */
    public float getLineWidth() {
        return mLineWidth;
    }

    /**
     * set the width of the highlightning rectangle 1.0f == 100% width of the
     * cell, 0f = 0%, default 0.6f
     * 
     * @param width
     */
    public void setHighlightRectWidth(float width) {
        mHighlightWidth = width;
    }

    /**
     * returns the width of the highlightning rectanlge, 1f == 100%, 0f = 0% of
     * the highlighted cell
     * 
     * @return
     */
    public float getHighlightRectWidth() {
        return mHighlightWidth;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_FILLED:
                mFilledPaint = p;
                break;
            case PAINT_LINE:
                mLinePaint = p;
                break;
            case PAINT_CIRCLES_INNER:
                mCirclePaintInner = p;
                break;
            case PAINT_CIRCLES_OUTER:
                mCirclePaintOuter = p;
                break;
            case PAINT_HIGHLIGHT_LINE:
                mHighlightPaint = p;
                break;
        }
    }
}
