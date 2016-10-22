package com.passioncreativestudio.mmkexchange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView rateListView;
    private TextView rateDatetime;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static String url = "http://mmk-exchange.herokuapp.com/api/latest";
    SimpleDateFormat rateDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
    private HashMap<String, Currency> currenciesMap;
    private ArrayList<String> selectedCurrencies;
    private Date rateDate;

    ArrayList<CurrencyRate> rateList;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        currenciesMap = Currency.initCurrencies();
        selectedCurrencies = new ArrayList<>();

        rateList = new ArrayList<>();
        rateListView = (ListView) findViewById(R.id.content_main_rateListView);
        rateDatetime = (TextView) findViewById(R.id.content_main_updatedDate);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        for(Currency cur : currenciesMap.values()) {
            if(prefs.getBoolean(cur.getName(), true)) {
                selectedCurrencies.add(cur.getName());
            }
        }

        if(isNetworkConnected()) {
            new GetRates().execute();
        } else {
            Toast.makeText(this, "Please, check your internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                if(isNetworkConnected()) {
                    new GetRates().execute();
                } else {
                    Toast.makeText(this, "Please, check your internet connection!", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private class GetRates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler handler = new HttpHandler();
            String jsonStr = handler.makeServiceCall(url, "GET");
            if(jsonStr != null) {
                try {
                    rateList.clear();

                    Gson gson = new Gson();
                    Rate rate = gson.fromJson(jsonStr, Rate.class);
                    for (Field field : rate.getClass().getDeclaredFields()) {
                        field.setAccessible(true); // You might want to set modifier to public first.
                        if(selectedCurrencies.contains(field.getName())) {
                            Double value = (Double) field.get(rate);
                            if (value != null) {
                                sb.append(field.getName() + "=" + value);

                                rateList.add(new CurrencyRate(field.getName(), value));
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

            if(rateList.size() == 0) {
                Toast.makeText(MainActivity.this, "API Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            rateDatetime.setText(String.format("Updated On: %s", rateDateFormat.format(rateDate)));

            RatesAdapter adapter  = new RatesAdapter(MainActivity.this, rateList);
            rateListView.setAdapter(adapter);
        }
    }

    private class RatesAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<CurrencyRate> rates;

        public RatesAdapter(Activity activity, ArrayList<CurrencyRate> rateList) {
            inflater = activity.getLayoutInflater();
            this.rates = rateList;
            Collections.sort(rates, new Comparator<CurrencyRate>() {
                @Override
                public int compare(CurrencyRate cr1, CurrencyRate cr2) {
                    return cr1.getName().compareTo(cr2.getName());
                }
            });
        }

        @Override
        public int getCount() {
            return rates.size();
        }

        @Override
        public Object getItem(int position) {
            return rates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_rate, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CurrencyRate rate = rates.get(position);

            Currency currency = currenciesMap.get(rate.getName());

            int thumbId = getResources().getIdentifier(rate.getName().toLowerCase(), "drawable", getPackageName());
            viewHolder.rateThumbnail.setImageResource(thumbId);
            viewHolder.rateName.setText(String.format("%s(%s)", currency.getName(), currency.getSign()));
            viewHolder.rateValue.setText(decimalFormat.format(rate.getRate()));
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
