package com.passioncreativestudio.mmkexchange.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.passioncreativestudio.mmkexchange.Currency;
import com.passioncreativestudio.mmkexchange.CurrencyRate;
import com.passioncreativestudio.mmkexchange.HttpHandler;
import com.passioncreativestudio.mmkexchange.R;
import com.passioncreativestudio.mmkexchange.localbank.Rate;

import java.lang.reflect.Field;
import java.util.Date;

import static com.passioncreativestudio.mmkexchange.Constants.APP_DOMAIN;

public class LocalBankActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_bank);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_DOMAIN)));
        } else if(id == R.id.nav_localBanks) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_DOMAIN)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetLocalBankAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LocalBankActivity.this);
            progressDialog.setMessage("Updating Rates...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Gson gson = new Gson();
            HttpHandler handler = new HttpHandler();
            String apiUrl = "http://mmk-exchange.herokuapp.com/api/localbanks";
            String jsonStr = handler.makeServiceCall(apiUrl, "GET");
            if(jsonStr != null) {
                try {

                    /*
                    Rate rate = gson.fromJson(jsonStr, Rate.class);
                    for (Field field : rate.getClass().getDeclaredFields()) {
                        field.setAccessible(true); // You might want to set modifier to public first.
                        if(selectedCurrencies.contains(field.getName())) {
                            Double value = (Double) field.get(rate);
                            if (value != null) {

                                Currency cur = currenciesMap.get(field.getName());

                                currencyRates.add(new CurrencyRate(field.getName(), value, cur.getDescription()));
                            }
                        } else if(field.getName().equals("rateTimestamp")) {
                            long rateTimestamp = (long) field.get(rate);
                            rateDate = new Date(rateTimestamp * 1000);

                        }
                    }
                    */
                } catch(Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
    }
}
