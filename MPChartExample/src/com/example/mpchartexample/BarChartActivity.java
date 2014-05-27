package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.Approximator;
import com.github.mikephil.charting.BarChart;
import com.github.mikephil.charting.ChartData;
import com.github.mikephil.charting.ColorTemplate;
import com.github.mikephil.charting.DataSet;
import com.github.mikephil.charting.Series;
import com.github.mikephil.charting.Approximator.ApproximatorType;

import java.util.ArrayList;

public class BarChartActivity extends Activity implements OnSeekBarChangeListener {

	private BarChart mChart;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_barchart);

		tvX = (TextView) findViewById(R.id.tvXMax);
		tvY = (TextView) findViewById(R.id.tvYMax);

		mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBarX.setOnSeekBarChangeListener(this);

		mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
		mSeekBarY.setOnSeekBarChangeListener(this);

		mChart = (BarChart) findViewById(R.id.chart1);
		mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.FRESH_COLORS)));
		// mChart.setLegendDigits(2);
		// mChart.setValueDigits(2);

		// mChart.setDrawFilled(true);
		// mChart.setRoundedYLegend(false);
		// mChart.setStartAtZero(true);
		mChart.setDrawYValues(false);
		mChart.set3DEnabled(false);
		// mChart.setSpacePercent(20, 10);
		mChart.setYLegendCount(5);
		mChart.setTouchEnabled(true);

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
		case R.id.actionToggleRound: {
			if (mChart.isYLegendRounded())
				mChart.setRoundedYLegend(false);
			else
				mChart.setRoundedYLegend(true);
			mChart.invalidate();
			break;
		}
		case R.id.actionToggleValues: {
			if (mChart.isDrawYValuesEnabled())
				mChart.setDrawYValues(false);
			else
				mChart.setDrawYValues(true);
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
			if (mChart.isAdjustXLegendEnabled())
				mChart.setAdjustXLegend(false);
			else
				mChart.setAdjustXLegend(true);

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

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			xVals.add((i) + "");
		}

		ArrayList<Series> yVals = new ArrayList<Series>();

		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			float mult = (mSeekBarY.getProgress() + 1);
			float val = (float) (Math.random() * mult * 0.1) + 3;// + (float) ((mult * 0.1) / 10);
			yVals.add(new Series(val, i));
		}

		tvX.setText("" + (mSeekBarX.getProgress() + 1));
		tvY.setText("" + (mSeekBarY.getProgress() / 10));
		
//		Approximator approximator = new Approximator(ApproximatorType.DOUGLAS_PEUCKER);
//		ArrayList<Series> filtered = approximator.filter(yVals);

		DataSet set = new DataSet(yVals, 0);
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set);

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
