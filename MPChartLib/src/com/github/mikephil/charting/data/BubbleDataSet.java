
package com.github.mikephil.charting.data;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class BubbleDataSet extends BarLineScatterCandleDataSet<BubbleEntry> {

    // NOTE: Do not initialize these, as the calcMinMax is called by the super,
    // and the initializers are called after that and can reset the values
    protected float mXMax;
    protected float mXMin;
    protected float mMaxSize;

    public BubbleDataSet(List<BubbleEntry> yVals, String label) {
        super(yVals, label);

        if (mMaxSize < 1.f)
            mMaxSize = 1.f;
    }

    @Override
    public void setColor(int color) {
        super.setColor(Color.argb(127, Color.red(color), Color.green(color), Color.blue(color)));
    }

    @Override
    protected void calcMinMax()
    {
        final List<BubbleEntry> entries = getYVals();

        //need chart width to guess this properly

        for (BubbleEntry entry : entries)
        {
            final float ymin = yMin(entry);
            final float ymax = yMax(entry);

            if (ymin < mYMin)
            {
                mYMin = ymin;
            }

            if (ymax > mYMax)
            {
                mYMax = ymax;
            }

            final float xmin = xMin(entry);
            final float xmax = xMax(entry);

            if (xmin < mXMin)
            {
                mXMin = xmin;
            }

            if (xmax > mXMax)
            {
                mXMax = xmax;
            }

            final float size = largestSize(entry);

            if (size > mMaxSize)
            {
                mMaxSize = size;
            }
        }
    }

    @Override
    public DataSet<BubbleEntry> copy() {

        List<BubbleEntry> yVals = new ArrayList<BubbleEntry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        BubbleDataSet copied = new BubbleDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }

    public float getXMax() {
        return mXMax;
    }

    public float getXMin() {
        return mXMin;
    }

    public float getMaxSize() {
        return mMaxSize;
    }

    private float yMin(BubbleEntry entry) {
        return entry.getVal();
    }

    private float yMax(BubbleEntry entry) {
        return entry.getVal();
    }

    private float xMin(BubbleEntry entry) {
        return (float)entry.getXIndex();
    }

    private float xMax(BubbleEntry entry) {
        return (float)entry.getXIndex();
    }

    private float largestSize(BubbleEntry entry) {
        return entry.getSize();
    }
}
