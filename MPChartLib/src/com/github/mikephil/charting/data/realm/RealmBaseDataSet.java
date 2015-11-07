package com.github.mikephil.charting.data.realm;

import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 06/11/15.
 */
public abstract class RealmBaseDataSet<T extends RealmObject> extends BaseDataSet<Entry> {

    protected List<Entry> mValues;
    private String yValuesField;
    private String xIndexField;

    public RealmBaseDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        this.yValuesField = yValuesField;
        this.xIndexField = xIndexField;
        mValues = new ArrayList<Entry>();

        results.sort(xIndexField, true);

        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            mValues.add(new Entry(dynamicObject.getFloat(yValuesField), dynamicObject.getInt(xIndexField)));
        }
    }

    @Override
    public float getYMin() {
        //return results.min(yValuesField).floatValue();
        return -50;
    }

    @Override
    public float getYMax() {
        //return results.max(yValuesField).floatValue();
        return 200;
    }

    @Override
    public int getEntryCount() {
        return mValues.size();
    }

    @Override
    public void calcMinMax(int start, int end) {

    }

    @Override
    public Entry getEntryForXIndex(int xIndex) {
        //DynamicRealmObject o = new DynamicRealmObject(results.where().equalTo(xIndexField, xIndex).findFirst());
        //return new Entry(o.getFloat(yValuesField), o.getInt(xIndexField));
        int index = getEntryIndex(xIndex);
        if (index > -1)
            return mValues.get(index);
        return null;
    }

    @Override
    public Entry getEntryForIndex(int index) {
        //DynamicRealmObject o = new DynamicRealmObject(results.get(index));
        //return new Entry(o.getFloat(yValuesField), o.getInt(xIndexField));
        return mValues.get(index);
    }

    @Override
    public int getEntryIndex(int xIndex) {

        int low = 0;
        int high = mValues.size() - 1;
        int closest = -1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (xIndex == mValues.get(m).getXIndex()) {
                while (m > 0 && mValues.get(m - 1).getXIndex() == xIndex)
                    m--;

                return m;
            }

            if (xIndex > mValues.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;

            closest = m;
        }

        return closest;
    }

    @Override
    public int getEntryIndex(Entry e) {
        return mValues.indexOf(e);
    }

    @Override
    public float getYValForXIndex(int xIndex) {
        //return new DynamicRealmObject(results.where().greaterThanOrEqualTo(xIndexField, xIndex).findFirst()).getFloat(yValuesField);
        Entry e = getEntryForXIndex(xIndex);

        if (e != null && e.getXIndex() == xIndex)
            return e.getVal();
        else
            return Float.NaN;
    }

    @Override
    public void addEntry(Entry e) {

    }

    @Override
    public boolean removeEntry(Entry e) {
        return false;
    }

    public String getValuesField() {
        return yValuesField;
    }

    public String getIndexField() {
        return xIndexField;
    }
}
