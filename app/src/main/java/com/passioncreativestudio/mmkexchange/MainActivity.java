package com.passioncreativestudio.mmkexchange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView rateListView;
    private TextView textView;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static String url = "http://mmk-exchange-kyawagwin.c9users.io/api/latest";
    private static ArrayList<String> selectedCurrency = new ArrayList<>();


    HashMap<String, Double> rateMap;
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

        rateMap = new HashMap<>();
        rateListView = (ListView) findViewById(R.id.content_main_rateListView);
        textView = (TextView) findViewById(R.id.content_main_textView);

        selectedCurrency.add("MMK");
        selectedCurrency.add("SGD");

        new GetRates().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
                    Gson gson = new Gson();
                    Rate rate = gson.fromJson(jsonStr, Rate.class);
                    for (Field field : rate.getClass().getDeclaredFields()) {
                        field.setAccessible(true); // You might want to set modifier to public first.
                        if(selectedCurrency.contains(field.getName())) {
                            Double value = (Double) field.get(rate);
                            if (value != null) {
                                sb.append(field.getName() + "=" + value);

                                rateMap.put(field.getName(), value);
                            }
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

            textView.setText(sb.toString());
            RatesAdapter adapter  = new RatesAdapter((Activity)MainActivity.this, rateMap);
            rateListView.setAdapter(adapter);
        }
    }

    private class RatesAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private HashMap<String, Double> data = new HashMap<>();
        private String[] keys;

        public RatesAdapter(Activity activity, HashMap<String, Double> data) {
            inflater = activity.getLayoutInflater();
            this.data = data;
            keys = data.keySet().toArray(new String[data.size()]);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(keys[position]);
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

            String key = keys[position];
            Double rate = data.get(key);

            int thumbId = getResources().getIdentifier(key, "drawable", getPackageName());
            viewHolder.thumbnail.setImageResource(thumbId);
            viewHolder.rate.setText(decimalFormat.format(rate));

            return null;
        }
    }

    private class ViewHolder {
        ImageView thumbnail;
        TextView rate;

        public ViewHolder(View view) {
            thumbnail = (ImageView) view.findViewById(R.id.list_item_rate_thumbnail);
            rate = (TextView) view.findViewById(R.id.list_item_rate_rateTextView);
        }
    }
}
