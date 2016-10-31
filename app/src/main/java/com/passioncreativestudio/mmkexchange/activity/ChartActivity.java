package com.passioncreativestudio.mmkexchange.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.passioncreativestudio.mmkexchange.R;

import java.text.DecimalFormat;

public class ChartActivity extends BaseActivity {

    private DecimalFormat curFormat = new DecimalFormat("0.00");

    ImageView fiveDayGraph;
    ImageView threeMonthGraph;
    ImageView oneYearGraph;

    EditText srcValueET;
    EditText destValueET;
    ImageView destFlagIV;

    private Double rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chart);



        Intent intent = getIntent();
        String currency = intent.getStringExtra(MainActivity.SEND_CURRENCY_NAME);
        rate = intent.getDoubleExtra(MainActivity.SEND_CURRENCY_RATE, 0);



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
        srcValueET = (EditText) findViewById(R.id.content_chart_srcValueET);
        destValueET = (EditText) findViewById(R.id.content_chart_destValueET);
        destFlagIV = (ImageView) findViewById(R.id.content_chart_destFlagIV);

        srcValueET.setText(curFormat.format(rate));

        srcValueET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(srcValueET.getText().length() > 0) {
                    double value = compareSource(Double.parseDouble(srcValueET.getText().toString()));
                    destValueET.setText(curFormat.format(value));
                } else {
                    destValueET.setText("0.00");
                }

                return false;
            }
        });

        destValueET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(destValueET.getText().length() > 0) {
                    double value = compareDestination(Double.parseDouble(destValueET.getText().toString()));
                    srcValueET.setText(curFormat.format(value));
                } else {
                    srcValueET.setText("0.00");
                }

                return false;
            }
        });

        int thumbId = getResources().getIdentifier(currency.toLowerCase(), "drawable", getPackageName());
        destFlagIV.setImageResource(thumbId);




        if(!currency.isEmpty()) {
            getSupportActionBar().setTitle(String.format("%s: %s Ks", currency, new DecimalFormat("0.00").format(rate)));

            Glide.with(this).load("https://chart.finance.yahoo.com/w?s=" + currency + "MMK%3dX&lang=en-SG&region=SG").into(fiveDayGraph);
            Glide.with(this).load("https://chart.finance.yahoo.com/3m?" + currency + "MMK=x&lang=en-SG&region=SG").into(threeMonthGraph);
            Glide.with(this).load("https://chart.finance.yahoo.com/1y?" + currency + "MMK=x&lang=en-SG&region=SG").into(oneYearGraph);
        } else {
            getSupportActionBar().setTitle("Chart not found!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chart, menu);

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
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
        }

        return true;
    }

    private double compareSource(double value) {
        if(value > 0)
            return value / rate;

        return 0;
    }

    private double compareDestination(double value) {
        if(value > 0)
            return value * rate;

        return 0;
    }
}
