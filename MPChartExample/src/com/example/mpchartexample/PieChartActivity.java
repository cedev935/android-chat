package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.ChartData;
import com.github.mikephil.charting.ColorTemplate;
import com.github.mikephil.charting.DataSet;
import com.github.mikephil.charting.Highlight;
import com.github.mikephil.charting.OnChartValueSelectedListener;
import com.github.mikephil.charting.PieChart;
import com.github.mikephil.charting.Series;

import java.util.ArrayList;

public class PieChartActivity extends Activity implements OnSeekBarChangeListener, OnChartValueSelectedListener {

	private PieChart mChart;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_piechart);

		tvX = (TextView) findViewById(R.id.tvXMax);
		tvY = (TextView) findViewById(R.id.tvYMax);

		mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

		mSeekBarX.setOnSeekBarChangeListener(this);
		mSeekBarY.setOnSeekBarChangeListener(this);

		mChart = (PieChart) findViewById(R.id.chart1);
		mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.COLORFUL_COLORS)));

		mChart.setDrawYValues(true);
		mChart.setDrawCenterText(true);

		mChart.setDescription("This is a description.");
		mChart.setDrawHoleEnabled(true);
		mChart.setDrawXValues(true);
		mChart.setTouchEnabled(true);
		mChart.setUsePercentValues(false);
		mChart.setOnChartValueSelectedListener(this);

		mSeekBarX.setProgress(5);
		mSeekBarY.setProgress(100);

		// float diameter = mChart.getDiameter();
		// float radius = mChart.getRadius();
		//
		// Log.i("Piechart", "diameter: " + diameter + ", radius: " + radius);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pie, menu);
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
		case R.id.actionTogglePercent: {
			if (mChart.isUsePercentValuesEnabled())
				mChart.setUsePercentValues(false);
			else
				mChart.setUsePercentValues(true);
			mChart.invalidate();
			break;
		}
		case R.id.actionToggleHole: {
			if (mChart.isDrawHoleEnabled())
				mChart.setDrawHoleEnabled(false);
			else
				mChart.setDrawHoleEnabled(true);
			mChart.invalidate();
			break;
		}
		case R.id.actionDrawCenter: {
			if (mChart.isDrawCenterTextEnabled())
				mChart.setDrawCenterText(false);
			else
				mChart.setDrawCenterText(true);
			mChart.invalidate();
			break;
		}
		case R.id.actionToggleXVals: {
			if (mChart.isDrawXValuesEnabled())
				mChart.setDrawXValues(false);
			else
				mChart.setDrawXValues(true);
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

		ArrayList<Series> yVals = new ArrayList<Series>();

		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			float mult = (mSeekBarY.getProgress());
			float val = (float) (Math.random() * mult) + mult / 5;// + (float) ((mult * 0.1) / 10);
			yVals.add(new Series(val, i, 0));
		}

		tvX.setText("" + (mSeekBarX.getProgress()));
		tvY.setText("" + (mSeekBarY.getProgress()));

		ArrayList<String> xVals = new ArrayList<String>();

		for (int i = 0; i < yVals.size(); i++)
			xVals.add("Text" + (i + 1));

		DataSet set = new DataSet(yVals, 0);
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set);

        ChartData data = new ChartData(xVals, dataSets);
		mChart.setData(data);
		mChart.setCenterText("Total Value\n" + (int) mChart.getYValueSum() + "\n(all slices)");
		mChart.invalidate();
	}

	@Override
	public void onValuesSelected(float[] values, Highlight[] highs) {
	    
	    StringBuffer a = new StringBuffer();

        for (int i = 0; i < values.length; i++)
            a.append("val: " + values[i] + ", x-ind: " + highs[i].getXIndex() + ", dataset-ind: " + highs[i].getDataSetIndex() + "\n");

        Log.i("PieChart", "Selected: " + a.toString());
	}

	@Override
	public void onNothingSelected() {
		Log.i("PieChart", "nothing selected");
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
