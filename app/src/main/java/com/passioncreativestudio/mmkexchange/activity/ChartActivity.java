package com.passioncreativestudio.mmkexchange.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.passioncreativestudio.mmkexchange.R;

import java.text.DecimalFormat;

public class ChartActivity extends AppCompatActivity {
    private static final String TAG = ChartActivity.class.getSimpleName();

    ImageView fiveDayGraph;
    ImageView threeMonthGraph;
    ImageView oneYearGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fiveDayGraph = (ImageView) findViewById(R.id.content_chart_fiveDayRateGraph);
        threeMonthGraph = (ImageView) findViewById(R.id.content_chart_threeMonthRateGraph);
        oneYearGraph = (ImageView) findViewById(R.id.content_chart_oneYearRateGraph);

        Intent intent = getIntent();
        String currency = intent.getStringExtra("Currency");
        Double rate = intent.getDoubleExtra("Rate", 0);

        if(!currency.isEmpty()) {
            getSupportActionBar().setTitle(String.format("Current: %s", new DecimalFormat("0.00").format(rate)));

            Glide.with(this).load("https://chart.finance.yahoo.com/w?s=" + currency + "MMK%3dX&lang=en-SG&region=SG").into(fiveDayGraph);
            Glide.with(this).load("https://chart.finance.yahoo.com/3m?" + currency + "MMK=x&lang=en-SG&region=SG").into(threeMonthGraph);
            Glide.with(this).load("https://chart.finance.yahoo.com/1y?" + currency + "MMK=x&lang=en-SG&region=SG").into(oneYearGraph);
        } else {
            getSupportActionBar().setTitle("Chart not found!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.show();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, Main2Activity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }

        return true;
    }

}
