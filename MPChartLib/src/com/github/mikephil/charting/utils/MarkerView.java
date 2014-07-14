
package com.github.mikephil.charting.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * View that can be displayed when selecting values in the chart. Extend this
 * class to provide custom layouts for your markers.
 * 
 * @author Philipp Jahoda
 */
public abstract class MarkerView extends RelativeLayout {

    /** draw offset on the x-axis */
    private float mXOffset = 0f;

    /** draw offset on the y-axis */
    private float mYOffset = 0f;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     * 
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MarkerView(Context context, int layoutResource) {
        super(context);
        setupLayoutResource(layoutResource);
    }

    /**
     * Sets the layout resource for a custom MarkerView.
     * 
     * @param layoutResource
     */
    private void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);

        inflated.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        // measure(getWidth(), getHeight());
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    /**
     * Draws the MarkerView on the given position on the screen with the given
     * Canvas object.
     * 
     * @param canvas
     * @param posx
     * @param posy
     */
    public void draw(Canvas canvas, float posx, float posy) {

        // take offsets into consideration
        posx += mXOffset;
        posy += mYOffset;

        // translate to the correct position and draw
        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }

    /**
     * this method enables a specified custom MarkerView to update it's content
     * everytime the MarkerView is redrawn
     * 
     * @param xIndex the index on the x-axis
     * @param value the actual selected value
     * @param dataSetIndex the index of the DataSet the selected value is in
     */
    public abstract void refreshContent(int xIndex, float value, int dataSetIndex);

    /**
     * Set the position offset of the MarkerView. By default, the top left edge
     * of the MarkerView is drawn directly where the selected value is at. In
     * order to change that, offsets in pixels can be defined. Default offset is
     * zero (0f) on both axes. For offsets dependent on the MarkerViews width
     * and height, use getMeasuredWidth() / getMeasuredHeight().
     * 
     * @param x
     * @param y
     */
    public void setOffsets(float x, float y) {
        this.mXOffset = x;
        this.mYOffset = y;
    }

    /**
     * returns the x-offset that is set for the MarkerView
     * 
     * @return
     */
    public float getXOffset() {
        return mXOffset;
    }

    /**
     * returns the y-offset that is set for the MarkerView
     * 
     * @return
     */
    public float getYOffset() {
        return mYOffset;
    }
}
