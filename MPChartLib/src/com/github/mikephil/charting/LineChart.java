
package com.github.mikephil.charting;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;

public class LineChart extends BarLineChartBase {

    /** the radius of the circle-shaped value indicators */
    protected float mCircleSize = 4f;

    /** the width of the drawn data lines */
    protected float mLineWidth = 1f;

    /** the width of the highlighning line */
    protected float mHighlightWidth = 3f;

    /** if true, the data will also be drawn filled */
    protected boolean mDrawFilled = false;

    /** if true, drawing circles is enabled */
    protected boolean mDrawCircles = true;

    /** paint for the filled are (if enabled) below the chart line */
    protected Paint mFilledPaint;

    /** paint for the inner circle of the value indicators */
    protected Paint mCirclePaintInner;

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

        mFilledPaint = new Paint();
        mFilledPaint.setStyle(Paint.Style.FILL);
        mFilledPaint.setColor(mColorDarkBlue);
        mFilledPaint.setAlpha(130); // alpha ~55%

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
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
            mDrawPaints[i].setStyle(Style.FILL);
            mDrawPaints[i].setColor(ct.getColors().get(i));
        }
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && valuesToHighlight()) {

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                int xIndex = mIndicesToHightlight[i].getXIndex();
                DataSet set = getDataSetByIndex(mIndicesToHightlight[i].getDataSetIndex());

                // check outofbounds
                if (xIndex < set.getYValCount() && xIndex >= 0) {

                    float[] pts = new float[] {
                            xIndex, mYChartMax, xIndex, mYChartMin, 0,
                            set.getYValForXIndex(xIndex), mDeltaX, set.getYValForXIndex(xIndex)
                    };

                    transformPointArray(pts);
                    // draw the highlight lines
                    mDrawCanvas.drawLines(pts, mHighlightPaint);
                }
            }
        }
    }

    /**
     * draws the given y values to the screen
     */
    @Override
    protected void drawData() {

        ArrayList<DataSet> dataSets = mData.getDataSets();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            DataSet dataSet = dataSets.get(i);
            ArrayList<Series> series = dataSet.getYVals();

            float[] valuePoints = new float[series.size() * 2];

            for (int j = 0; j < valuePoints.length; j += 2) {
                valuePoints[j] = series.get(j / 2).getXIndex();
                valuePoints[j + 1] = series.get(j / 2).getVal();
            }

            transformPointArray(valuePoints);

            for (int j = 0; j < valuePoints.length - 2; j += 2) {

                if (isOffCanvasRight(valuePoints[j]))
                    break;

                // make sure the lines don't do shitty things outside bounds
                if (j != 0 && isOffCanvasLeft(valuePoints[j - 1]))
                    continue;

                mDrawCanvas.drawLine(valuePoints[j], valuePoints[j + 1], valuePoints[j + 2],
                        valuePoints[j + 3],
                        mDrawPaints[i % mDrawPaints.length]);
            }
        }

        // if data is drawn filled
        if (mDrawFilled) {

            Path filled = new Path();
            filled.moveTo(0, getYValue(0));

            // create a new path
            for (int x = 1; x < mData.getYValCount(); x++) {

                filled.lineTo(x, getYValue(x));
            }

            // close up
            filled.lineTo(mData.getXValCount() - 1, mYChartMin);
            filled.lineTo(0f, mYChartMin);
            filled.close();

            transformPath(filled);

            mDrawCanvas.drawPath(filled, mFilledPaint);
        }
    }

    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues && mData.getYValCount() < mMaxVisibleCount * mScaleX) {

            // make sure the values do not interfear with the circles
            int valOffset = (int) (mCircleSize * 1.7f);

            if (!mDrawCircles)
                valOffset = valOffset / 2;

            ArrayList<DataSet> dataSets = mData.getDataSets();

            for (int i = 0; i < mData.getDataSetCount(); i++) {

                DataSet dataSet = dataSets.get(i);
                ArrayList<Series> series = dataSet.getYVals();

                float[] positions = new float[dataSet.getYValCount() * 2];

                for (int j = 0; j < positions.length; j += 2) {
                    positions[j] = series.get(j / 2).getXIndex();
                    positions[j + 1] = series.get(j / 2).getVal();
                }

                transformPointArray(positions);

                for (int j = 0; j < positions.length; j += 2) {

                    if (isOffCanvasRight(positions[j]))
                        break;

                    if (isOffCanvasLeft(positions[j]))
                        continue;

                    float val = series.get(j / 2).getVal();

                    if (mDrawUnitInChart) {

                        mDrawCanvas.drawText(mFormatValue.format(val) + mUnit,
                                positions[j], positions[j + 1] - valOffset, mValuePaint);
                    } else {

                        mDrawCanvas.drawText(mFormatValue.format(val), positions[j],
                                positions[j + 1] - valOffset, mValuePaint);
                    }
                }
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

            ArrayList<DataSet> dataSets = mData.getDataSets();

            for (int i = 0; i < mData.getDataSetCount(); i++) {

                DataSet dataSet = dataSets.get(i);
                ArrayList<Series> series = dataSet.getYVals();

                float[] positions = new float[dataSet.getYValCount() * 2];

                for (int j = 0; j < positions.length; j += 2) {
                    positions[j] = series.get(j / 2).getXIndex();
                    positions[j + 1] = series.get(j / 2).getVal();
                }

                transformPointArray(positions);

                for (int j = 0; j < positions.length; j += 2) {

                    if (isOffCanvasRight(positions[j]))
                        break;

                    // make sure the circles don't do shitty things outside
                    // bounds
                    if (isOffCanvasLeft(positions[j]))
                        continue;

                    mDrawCanvas.drawCircle(positions[j], positions[j + 1], mCircleSize,
                            mDrawPaints[i % mDrawPaints.length]);
                    mDrawCanvas.drawCircle(positions[j], positions[j + 1], mCircleSize / 2,
                            mCirclePaintInner);
                }
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

        for (int i = 0; i < mDrawPaints.length; i++) {
            mDrawPaints[i].setStrokeWidth(mLineWidth);
        }
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
     * sets the color for the fill-paint
     * 
     * @param color
     */
    public void setFillColor(int color) {
        mFilledPaint.setColor(color);
    }

    /**
     * set the width of the highlightning lines, default 3f
     * 
     * @param width
     */
    public void setHighlightLineWidth(float width) {
        mHighlightWidth = width;
    }

    /**
     * returns the width of the highlightning line, default 3f
     * 
     * @return
     */
    public float getHighlightLineWidth() {
        return mHighlightWidth;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_FILLED:
                mFilledPaint = p;
                break;
            case PAINT_CIRCLES_INNER:
                mCirclePaintInner = p;
                break;
            case PAINT_HIGHLIGHT_LINE:
                mHighlightPaint = p;
                break;
        }
    }
}
