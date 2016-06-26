package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.realm.base.RealmLineScatterCandleRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmScatterDataSet<T extends RealmObject> extends RealmLineScatterCandleRadarDataSet<T, Entry> implements IScatterDataSet {

    /**
     * the size the scattershape will have, in density pixels
     */
    private float mShapeSize = 10f;

    /**
     * the type of shape that is set to be drawn where the values are at,
     * default ScatterShape.SQUARE
     */
    private String mScatterShape = ScatterChart.ScatterShape.SQUARE.toString();

    /**
     * The radius of the hole in the shape (applies to Square, Circle and Triangle)
     * - default: 0.0
     */
    private float mScatterShapeHoleRadius = 0f;

    /**
     * Color for the hole in the shape.
     * Setting to `ColorTemplate.COLOR_NONE` will behave as transparent.
     * - default: ColorTemplate.COLOR_NONE
     */
    private int mScatterShapeHoleColor = ColorTemplate.COLOR_NONE;

    /**
     * Constructor for creating a ScatterDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     */
    public RealmScatterDataSet(RealmResults<T> result, String yValuesField) {
        super(result, yValuesField);

        build(this.results);
        calcMinMax();
    }

    /**
     * Constructor for creating a ScatterDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param xValuesField the name of the field in your data object that represents the x value
     * @param yValuesField  the name of the field in your data object that represents the y value
     */
    public RealmScatterDataSet(RealmResults<T> result, String xValuesField, String yValuesField) {
        super(result, xValuesField, yValuesField);

        build(this.results);
        calcMinMax();
    }

    /**
     * Sets the size in density pixels the drawn scattershape will have. This
     * only applies for non custom shapes.
     *
     * @param size
     */
    public void setScatterShapeSize(float size) {
        mShapeSize = size;
    }

    @Override
    public float getScatterShapeSize() {
        return mShapeSize;
    }


    /**
     * Sets the shape that is drawn on the position where the values are at.
     *
     * @param shape
     */
    public void setScatterShape(ScatterChart.ScatterShape shape) {
        mScatterShape = shape.toString();
    }


    /**
     * Sets the shape that is drawn on the position where the values are at. If
     * "CUSTOM" is chosen, you need to call setCustomScatterShape(...) and
     * provide a path object that is drawn as the custom scattershape.
     *
     * @param shape
     */
    public void setScatterShape(String shape) {
        mScatterShape = shape;
    }

    @Override
    public String getScatterShape() {
        return mScatterShape;
    }

    /**
     * Sets the radius of the hole in the shape
     *
     * @param holeRadius
     */
    public void setScatterShapeHoleRadius(float holeRadius) {
        mScatterShapeHoleRadius = holeRadius;
    }

    @Override
    public float getScatterShapeHoleRadius() {
        return mScatterShapeHoleRadius;
    }

    /**
     * Sets the color for the hole in the shape
     *
     * @param holeColor
     */
    public void setScatterShapeHoleColor(int holeColor) {
        mScatterShapeHoleColor = holeColor;
    }

    @Override
    public int getScatterShapeHoleColor() {
        return mScatterShapeHoleColor;
    }
}
