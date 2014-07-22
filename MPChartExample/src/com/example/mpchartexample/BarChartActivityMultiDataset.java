
package com.example.mpchartexample;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class BarChartActivityMultiDataset extends Activity implements OnSeekBarChangeListener {

    private BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener(this);

        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarY.setOnSeekBarChangeListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);

        ColorTemplate ct = new ColorTemplate();

        // add colors for the first dataset
        ct.addDataSetColors(ColorTemplate.FRESH_COLORS, this);

        // the second dataset only has one color
        ct.addDataSetColors(new int[] {
            R.color.liberty_2
        }, this);

        // add colors for the third dataset
        ct.addDataSetColors(ColorTemplate.COLORFUL_COLORS, this);

        mChart.setColorTemplate(ct);

        // disable the drawing of values
        mChart.setDrawYValues(false);

        // disable 3D
        mChart.set3DEnabled(false);
        mChart.setYLabelCount(5);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        
//        mChart.setDrawLegend(false);

        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                if (mChart.isDrawYValuesEnabled())
                    mChart.setDrawYValues(false);
                else
                    mChart.setDrawYValues(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggle3D: {
                if (mChart.is3DEnabled())
                    mChart.set3DEnabled(false);
                else
                    mChart.set3DEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.isHighlightEnabled())
                    mChart.setHighlightEnabled(false);
                else
                    mChart.setHighlightEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStartzero: {
                if (mChart.isStartAtZeroEnabled())
                    mChart.setStartAtZero(false);
                else
                    mChart.setStartAtZero(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAdjustXLegend: {
                if (mChart.isAdjustXLabelsEnabled())
                    mChart.setAdjustXLabels(false);
                else
                    mChart.setAdjustXLabels(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                // mChart.saveToGallery("title"+System.currentTimeMillis());
                mChart.saveToPath("title" + System.currentTimeMillis(), "");
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        
        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mSeekBarX.getProgress(); i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
        ArrayList<Entry> yVals3 = new ArrayList<Entry>();

        for (int i = 0; i < mSeekBarX.getProgress() / 3; i++) {
            float val = (float) (Math.random() * mSeekBarY.getProgress()) + 3;
            yVals1.add(new Entry(val, i));
        }

        for (int i = mSeekBarX.getProgress() / 3; i < mSeekBarX.getProgress() / 3 * 2; i++) {
            float val = (float) (Math.random() * mSeekBarY.getProgress()) + 3;
            yVals2.add(new Entry(val, i));
        }

        for (int i = mSeekBarX.getProgress() / 3 * 2; i < mSeekBarX.getProgress(); i++) {
            float val = (float) (Math.random() * mSeekBarY.getProgress()) + 3;
            yVals3.add(new Entry(val, i));
        }

        // create 3 datasets with different types
        DataSet set1 = new DataSet(yVals1, "DS 1");
        DataSet set2 = new DataSet(yVals2, "DS 2");
        DataSet set3 = new DataSet(yVals3, "DS 3");
        
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);

        ChartData data = new ChartData(xVals, dataSets);

        mChart.setData(data);
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
