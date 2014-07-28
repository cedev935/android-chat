package com.xxmassdeveloper.mpchartexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.xxmassdeveloper.mpchartexample.fragments.SimpleChartDemo;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		Button btn1 = (Button) findViewById(R.id.button1);
		Button btn2 = (Button) findViewById(R.id.button2);
		Button btn3 = (Button) findViewById(R.id.button3);
		Button btn4 = (Button) findViewById(R.id.button4);
		Button btn5 = (Button) findViewById(R.id.button5);
		Button btn6 = (Button) findViewById(R.id.button6);
		Button btn7 = (Button) findViewById(R.id.button7);
		Button btn8 = (Button) findViewById(R.id.button8);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent i;

		switch (v.getId()) {
		case R.id.button1:
			i = new Intent(this, LineChartActivity.class);
			startActivity(i);
			break;
		case R.id.button2:
			i = new Intent(this, BarChartActivity.class);
			startActivity(i);
			break;
		case R.id.button3:
			i = new Intent(this, PieChartActivity.class);
			startActivity(i);
			break;
		case R.id.button4:
			i = new Intent(this, MultiLineChartActivity.class);
			startActivity(i);
			break;
		case R.id.button5:
			i = new Intent(this, BarChartActivityMultiDataset.class);
			startActivity(i);
			break;
		case R.id.button6:
		    i = new Intent(this, ScatterChartActivity.class);
            startActivity(i);
			break;
		case R.id.button7:
			i = new Intent(this, DrawChartActivity.class);
			startActivity(i);
			break;
		case R.id.button8:
            i = new Intent(this, SimpleChartDemo.class);
            startActivity(i);
            break;
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        Intent i = null;       
        
        switch(item.getItemId()) {
            case R.id.viewGithub:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart"));
                startActivity(i);
                break;
            case R.id.report:
                i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","philjay.librarysup@gmail.com", null));
                i.putExtra(Intent.EXTRA_SUBJECT, "MPAndroidChart Issue");
                i.putExtra(Intent.EXTRA_TEXT, "Your error report here...");
                startActivity(Intent.createChooser(i, "Report Problem"));
                break;
            case R.id.website:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.xxmassdeveloper.com"));
                startActivity(i);
                break;
        }
        
        return true;
    }

}
