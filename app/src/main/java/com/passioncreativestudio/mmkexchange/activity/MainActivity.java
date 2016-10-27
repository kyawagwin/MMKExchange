package com.passioncreativestudio.mmkexchange.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.passioncreativestudio.mmkexchange.Currency;
import com.passioncreativestudio.mmkexchange.CurrencyRate;
import com.passioncreativestudio.mmkexchange.HttpHandler;
import com.passioncreativestudio.mmkexchange.R;
import com.passioncreativestudio.mmkexchange.Rate;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private DecimalFormat curRateFormat = new DecimalFormat("0.00");
    private static String apiUrl = "http://mmk-exchange.herokuapp.com/api/latest";
    SimpleDateFormat rateDatetimeFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");

    private HashMap<String, Currency> currenciesMap;
    private ArrayList<String> selectedCurrencies;
    private ArrayList<CurrencyRate> currencyRates;
    private Date rateDate;

    private RatesAdapter adapter;

    StringBuilder sb = new StringBuilder();

    private ListView curRateListView;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = findViewById(android.R.id.content);
        curRateListView = (ListView) findViewById(R.id.content_main_curRateListView);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        currenciesMap = Currency.initCurrencies();
        selectedCurrencies = new ArrayList<>();
        currencyRates = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        for(Currency cur : currenciesMap.values()) {
            if(prefs.getBoolean(cur.getName(), true)) {
                selectedCurrencies.add(cur.getName());
            }
        }

        if(isNetworkConnected()) {
            new GetRates().execute();
        } else {
            Snackbar.make(rootView, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //Toast.makeText(this, "Please, check your internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetRates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Updating Rates...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler handler = new HttpHandler();
            String jsonStr = handler.makeServiceCall(apiUrl, "GET");
            if(jsonStr != null) {
                try {
                    currencyRates.clear();

                    Gson gson = new Gson();
                    Rate rate = gson.fromJson(jsonStr, Rate.class);
                    for (Field field : rate.getClass().getDeclaredFields()) {
                        field.setAccessible(true); // You might want to set modifier to public first.
                        if(selectedCurrencies.contains(field.getName())) {
                            Double value = (Double) field.get(rate);
                            if (value != null) {
                                sb.append(field.getName() + "=" + value);

                                Currency cur = currenciesMap.get(field.getName());

                                currencyRates.add(new CurrencyRate(field.getName(), 1 / value, cur.getDescription()));
                            }
                        } else if(field.getName().equals("rateTimestamp")) {
                            long rateTimestamp = (long) field.get(rate);
                            rateDate = new Date(rateTimestamp * 1000);

                        }
                    }
                } catch(Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if(currencyRates.size() == 0) {
                Snackbar.make(findViewById(android.R.id.content), "No response from the server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Toast.makeText(MainActivity.this, "API Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: 26/10/16 call snackbar to show the updated rate datetime
            //rateDatetime.setText(String.format("Updated On: %s", rateDateFormat.format(rateDate)));

            adapter  = new MainActivity.RatesAdapter(MainActivity.this, currencyRates);
            curRateListView.setAdapter(adapter);
        }
    }

    private class RatesAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<CurrencyRate> currencyRates;

        public RatesAdapter(Activity activity, ArrayList<CurrencyRate> currencyRates) {
            inflater = activity.getLayoutInflater();
            this.currencyRates = currencyRates;
            Collections.sort(currencyRates, new Comparator<CurrencyRate>() {
                @Override
                public int compare(CurrencyRate cr1, CurrencyRate cr2) {
                    return cr1.getName().compareTo(cr2.getName());
                }
            });
        }

        @Override
        public int getCount() {
            return currencyRates.size();
        }

        @Override
        public Object getItem(int position) {
            return currencyRates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.ViewHolder viewHolder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_rate, parent, false);
                viewHolder = new MainActivity.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MainActivity.ViewHolder) convertView.getTag();
            }

            CurrencyRate rate = currencyRates.get(position);

            Currency currency = currenciesMap.get(rate.getName());

            int thumbId = getResources().getIdentifier(rate.getName().toLowerCase(), "drawable", getPackageName());
            viewHolder.rateThumbnail.setImageResource(thumbId);
            viewHolder.rateThumbnail.setContentDescription(currency.getName());
            viewHolder.rateName.setText(String.format("%s(%s)", currency.getName(), currency.getSign()));
            viewHolder.rateValue.setText(curRateFormat.format(rate.getRate()));
            viewHolder.rateDescription.setText(currency.getDescription());

            return convertView;
        }
    }

    private class ViewHolder {
        ImageView rateThumbnail;
        TextView rateValue;
        TextView rateName;
        TextView rateDescription;

        public ViewHolder(View view) {
            rateThumbnail = (ImageView) view.findViewById(R.id.list_item_rate_thumbnail);
            rateName = (TextView) view.findViewById(R.id.list_item_rate_name);
            rateValue = (TextView) view.findViewById(R.id.list_item_rate_value);
            rateDescription = (TextView) view.findViewById(R.id.list_item_rate_description);
        }
    }
}
