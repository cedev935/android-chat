
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.List;

public class PieChartRenderer extends DataRenderer {

    protected PieChart mChart;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    protected Paint mHolePaint;
    protected Paint mTransparentCirclePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private TextPaint mCenterTextPaint;

    private StaticLayout mCenterTextLayout;
    private CharSequence mCenterTextLastValue;
    private RectF mCenterTextLastBounds = new RectF();
    private RectF[] mRectBuffer = {new RectF(), new RectF(), new RectF()};

    /**
     * Bitmap for drawing the center hole
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    protected Canvas mBitmapCanvas;

    public PieChartRenderer(PieChart chart, ChartAnimator animator,
                            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);
        mHolePaint.setStyle(Style.FILL);

        mTransparentCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentCirclePaint.setColor(Color.WHITE);
        mTransparentCirclePaint.setStyle(Style.FILL);
        mTransparentCirclePaint.setAlpha(105);

        mCenterTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(Color.BLACK);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));

        mValuePaint.setTextSize(Utils.convertDpToPixel(13f));
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Align.CENTER);
    }

    public Paint getPaintHole() {
        return mHolePaint;
    }

    public Paint getPaintTransparentCircle() {
        return mTransparentCirclePaint;
    }

    public TextPaint getPaintCenterText() {
        return mCenterTextPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub
    }

    @Override
    public void drawData(Canvas c) {

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        if (mDrawBitmap == null
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        PieData pieData = mChart.getData();

        for (IPieDataSet set : pieData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    private Path mPathBuffer = new Path();
    private RectF mInnerRectBuffer = new RectF();

    protected void drawDataSet(Canvas c, IPieDataSet dataSet) {

        float angle = 0;
        float rotationAngle = mChart.getRotationAngle();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        final RectF circleBox = mChart.getCircleBox();

        final int entryCount = dataSet.getEntryCount();
        final float[] drawAngles = mChart.getDrawAngles();
        final PointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final float innerRadius = mChart.isDrawHoleEnabled() && mChart.isHoleTransparent()
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        // API < 21 does not receive floats in addArc, but a RectF
        mInnerRectBuffer.set(
                center.x - innerRadius,
                center.y - innerRadius,
                center.x + innerRadius,
                center.y + innerRadius);

        for (int j = 0; j < entryCount; j++) {

            float sliceAngle = drawAngles[j];
            float sliceSpace = dataSet.getSliceSpace();
            final float sliceSpaceOuterAngle = sliceSpace / (Utils.FDEG2RAD * radius);
            final float sliceSpaceInnerAngle = sliceSpace / (Utils.FDEG2RAD * innerRadius);

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                if (!mChart.needsHighlight(e.getXIndex(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    mRenderPaint.setColor(dataSet.getColor(j));

                    final float startAngleOuter = rotationAngle + (angle + sliceSpaceOuterAngle / 2.f) * phaseY;
                    float sweepAngleOuter = (sliceAngle - sliceSpaceOuterAngle) * phaseY;
                    if (sweepAngleOuter < 0.f)
                    {
                        sweepAngleOuter = 0.f;
                    }

                    mPathBuffer.reset();

                    mPathBuffer.moveTo(
                            center.x + radius * (float)Math.cos(startAngleOuter * Utils.FDEG2RAD),
                            center.y + radius * (float)Math.sin(startAngleOuter * Utils.FDEG2RAD));

                    mPathBuffer.arcTo(
                            circleBox,
                            startAngleOuter,
                            sweepAngleOuter
                    );

                    if (innerRadius > 0.0)
                    {
                        final float startAngleInner = rotationAngle + (angle + sliceSpaceInnerAngle / 2.f) * phaseY;
                        float sweepAngleInner = (sliceAngle - sliceSpaceInnerAngle) * phaseY;
                        if (sweepAngleInner < 0.f)
                        {
                            sweepAngleInner = 0.f;
                        }
                        final float endAngleInner = startAngleInner + sweepAngleInner;

                        mPathBuffer.lineTo(
                                center.x + innerRadius * (float) Math.cos(endAngleInner * Utils.FDEG2RAD),
                                center.y + innerRadius * (float) Math.sin(endAngleInner * Utils.FDEG2RAD));

                        mPathBuffer.arcTo(
                                mInnerRectBuffer,
                                endAngleInner,
                                -sweepAngleInner
                        );
                    }

                    mPathBuffer.close();

                    mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
                }
            }

            angle += sliceAngle * phaseX;
        }
    }

    @Override
    public void drawValues(Canvas c) {

        PointF center = mChart.getCenterCircleBox();

        // get whole the radius
        float r = mChart.getRadius();
        float rotationAngle = mChart.getRotationAngle();
        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float off = r / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            off = (r - (r / 100f * mChart.getHoleRadius())) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        PieData data = mChart.getData();
        List<IPieDataSet> dataSets = data.getDataSets();

        float yValueSum = data.getYValueSum();

        boolean drawXVals = mChart.isDrawSliceTextEnabled();

        float angle;
        int xIndex = 0;

        for (int i = 0; i < dataSets.size(); i++) {

            IPieDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled() && !drawXVals)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            float lineHeight = Utils.calcTextHeight(mValuePaint, "Q")
                    + Utils.convertDpToPixel(4f);

            int entryCount = dataSet.getEntryCount();

            for (int j = 0, maxEntry = Math.min(
                    (int) Math.ceil(entryCount * phaseX), entryCount); j < maxEntry; j++) {

                Entry entry = dataSet.getEntryForIndex(j);

                if (xIndex == 0)
                    angle = 0.f;
                else
                    angle = absoluteAngles[xIndex - 1] * phaseX;

                final float sliceAngle = drawAngles[xIndex];
                final float sliceSpace = dataSet.getSliceSpace();
                final float sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * r);

                // offset needed to center the drawn text in the slice
                final float offset = (sliceAngle - sliceSpaceMiddleAngle / 2.f) / 2.f;

                angle = angle + offset;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians(rotationAngle + angle))
                        + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians(rotationAngle + angle))
                        + center.y);

                float value = mChart.isUsePercentValuesEnabled() ? entry.getVal()
                        / yValueSum * 100f : entry.getVal();

                ValueFormatter formatter = dataSet.getValueFormatter();

                boolean drawYVals = dataSet.isDrawValuesEnabled();

                // draw everything, depending on settings
                if (drawXVals && drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y, dataSet.getValueTextColor(j));

                    if (j < data.getXValCount()) {
                        c.drawText(data.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);
                    }

                } else if (drawXVals) {
                    if (j < data.getXValCount()) {
                        mValuePaint.setColor(dataSet.getValueTextColor(j));
                        c.drawText(data.getXVals().get(j), x, y + lineHeight / 2f, mValuePaint);
                    }
                } else if (drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y + lineHeight / 2f, dataSet.getValueTextColor(j));
                }

                xIndex++;
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        // drawCircles(c);
        drawHole(c);
        c.drawBitmap(mDrawBitmap.get(), 0, 0, null);
        drawCenterText(c);
    }

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    protected void drawHole(Canvas c) {

        if (mChart.isDrawHoleEnabled()) {

            float transparentCircleRadius = mChart.getTransparentCircleRadius();
            float holeRadius = mChart.getHoleRadius();
            float radius = mChart.getRadius();

            PointF center = mChart.getCenterCircleBox();

            // only draw the circle if it can be seen (not covered by the hole)
            if (transparentCircleRadius > holeRadius) {

                // get original alpha
                int alpha = mTransparentCirclePaint.getAlpha();
                mTransparentCirclePaint.setAlpha((int) ((float) alpha * mAnimator.getPhaseX() * mAnimator.getPhaseY()));

                // draw the transparent-circle
                mBitmapCanvas.drawCircle(center.x, center.y,
                        radius / 100 * transparentCircleRadius, mTransparentCirclePaint);

                // reset alpha
                mTransparentCirclePaint.setAlpha(alpha);
            }

            // draw the hole-circle
            mBitmapCanvas.drawCircle(center.x, center.y,
                    radius / 100 * holeRadius, mHolePaint);
        }
    }

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    protected void drawCenterText(Canvas c) {

        CharSequence centerText = mChart.getCenterText();

        if (mChart.isDrawCenterTextEnabled() && centerText != null) {

            PointF center = mChart.getCenterCircleBox();

            float innerRadius = mChart.isDrawHoleEnabled() && mChart.isHoleTransparent()
                    ? mChart.getRadius() * (mChart.getHoleRadius() / 100f)
                    : mChart.getRadius();

            RectF holeRect = mRectBuffer[0];
            holeRect.left = center.x - innerRadius;
            holeRect.top = center.y - innerRadius;
            holeRect.right = center.x + innerRadius;
            holeRect.bottom = center.y + innerRadius;
            RectF boundingRect = mRectBuffer[1];
            boundingRect.set(holeRect);

            float radiusPercent = mChart.getCenterTextRadiusPercent() / 100f;
            if (radiusPercent > 0.0) {
                boundingRect.inset(
                        (boundingRect.width() - boundingRect.width() * radiusPercent) / 2.f,
                        (boundingRect.height() - boundingRect.height() * radiusPercent) / 2.f
                );
            }

            if (!centerText.equals(mCenterTextLastValue) || !boundingRect.equals(mCenterTextLastBounds)) {

                // Next time we won't recalculate StaticLayout...
                mCenterTextLastBounds.set(boundingRect);
                mCenterTextLastValue = centerText;

                float width = mCenterTextLastBounds.width();

                // If width is 0, it will crash. Always have a minimum of 1
                mCenterTextLayout = new StaticLayout(centerText, 0, centerText.length(),
                        mCenterTextPaint,
                        (int) Math.max(Math.ceil(width), 1.f),
                        Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, false);
            }

            //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
            float layoutHeight = mCenterTextLayout.getHeight();

            c.save();
            if (Build.VERSION.SDK_INT >= 18) {
                Path path = new Path();
                path.addOval(holeRect, Path.Direction.CW);
                c.clipPath(path);
            }

            c.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layoutHeight) / 2.f);
            mCenterTextLayout.draw(c);

            c.restore();
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float angle;
        float rotationAngle = mChart.getRotationAngle();

        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();
        final PointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final float innerRadius = mChart.isDrawHoleEnabled() && mChart.isHoleTransparent()
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        final RectF highlightedCircleBox = new RectF();

        for (int i = 0; i < indices.length; i++) {

            // get the index to highlight
            int xIndex = indices[i].getXIndex();
            if (xIndex >= drawAngles.length)
                continue;

            IPieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            if (xIndex == 0)
                angle = 0.f;
            else
                angle = absoluteAngles[xIndex - 1] * phaseX;

            float sliceAngle = drawAngles[xIndex];
            float sliceSpace = set.getSliceSpace();
            final float sliceSpaceOuterAngle = sliceSpace / (Utils.FDEG2RAD * radius);
            final float sliceSpaceInnerAngle = sliceSpace / (Utils.FDEG2RAD * innerRadius);

            float shift = set.getSelectionShift();
            final float highlightedRadius = radius + shift;
            highlightedCircleBox.set(mChart.getCircleBox());
            highlightedCircleBox.inset(-shift, -shift);

            mRenderPaint.setColor(set.getColor(xIndex));

            final float startAngleOuter = rotationAngle + (angle + sliceSpaceOuterAngle / 2.f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceOuterAngle) * phaseY;
            if (sweepAngleOuter < 0.f)
            {
                sweepAngleOuter = 0.f;
            }

            mPathBuffer.reset();

            mPathBuffer.moveTo(
                    center.x + highlightedRadius * (float)Math.cos(startAngleOuter * Utils.FDEG2RAD),
                    center.y + highlightedRadius * (float)Math.sin(startAngleOuter * Utils.FDEG2RAD));

            mPathBuffer.arcTo(
                    highlightedCircleBox,
                    startAngleOuter,
                    sweepAngleOuter
            );

            if (innerRadius > 0.0)
            {
                final float startAngleInner = rotationAngle + (angle + sliceSpaceInnerAngle / 2.f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceInnerAngle) * phaseY;
                if (sweepAngleInner < 0.f)
                {
                    sweepAngleInner = 0.f;
                }
                final float endAngleInner = startAngleInner + sweepAngleInner;

                mPathBuffer.lineTo(
                        center.x + innerRadius * (float) Math.cos(endAngleInner * Utils.FDEG2RAD),
                        center.y + innerRadius * (float) Math.sin(endAngleInner * Utils.FDEG2RAD));

                mPathBuffer.arcTo(
                        mInnerRectBuffer,
                        endAngleInner,
                        -sweepAngleInner
                );
            }

            mPathBuffer.close();

            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
        }
    }

    /**
     * This gives all pie-slices a rounded edge.
     *
     * @param c
     */
    protected void drawRoundedSlices(Canvas c) {

        if (!mChart.isDrawRoundedSlicesEnabled())
            return;

        IPieDataSet dataSet = mChart.getData().getDataSet();

        if (!dataSet.isVisible())
            return;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        PointF center = mChart.getCenterCircleBox();
        float r = mChart.getRadius();

        // calculate the radius of the "slice-circle"
        float circleRadius = (r - (r * mChart.getHoleRadius() / 100f)) / 2f;

        float[] drawAngles = mChart.getDrawAngles();
        float angle = mChart.getRotationAngle();

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            float sliceAngle = drawAngles[j];

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                float x = (float) ((r - circleRadius)
                        * Math.cos(Math.toRadians((angle + sliceAngle)
                        * phaseY)) + center.x);
                float y = (float) ((r - circleRadius)
                        * Math.sin(Math.toRadians((angle + sliceAngle)
                        * phaseY)) + center.y);

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += sliceAngle * phaseX;
        }
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mDrawBitmap != null) {
            mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
