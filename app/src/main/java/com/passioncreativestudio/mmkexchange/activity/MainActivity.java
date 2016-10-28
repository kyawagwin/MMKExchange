package com.passioncreativestudio.mmkexchange.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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

    private RecyclerView curRateRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        curRateRV = (RecyclerView) findViewById(R.id.content_main_curRatesRV);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.app_bar_main_refreshFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected())
                    new GetRates().execute();
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

        adapter = new RatesAdapter(currencyRates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        curRateRV.setLayoutManager(mLayoutManager);
        curRateRV.setItemAnimator(new DefaultItemAnimator());
        curRateRV.setAdapter(adapter);
        setUpItemTouchHelper();



        if(isNetworkConnected()) {
            new GetRates().execute();
        } else {
            Snackbar.make(findViewById(R.id.app_bar_main_refreshFab), "Network is not connected!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //Toast.makeText(this, "Please, check your internet connection!", Toast.LENGTH_LONG).show();
        }
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

    private void setUpItemTouchHelper() {


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_action_remove);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                if (adapter.isUndoOn() && adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                /*
                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark RIGHT
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);
                */

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(curRateRV);
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
                Snackbar.make(findViewById(R.id.fab), "No response from the server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Toast.makeText(MainActivity.this, "API Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: 26/10/16 call snackbar to show the updated rate datetime
            Snackbar.make(findViewById(R.id.app_bar_main_refreshFab), String.format("Updated: %s", rateDatetimeFormat.format(rateDate)), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            adapter.notifyDataSetChanged();
        }
    }

    private class RatesAdapter extends RecyclerView.Adapter<RateViewHolder> {
        private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

        private ArrayList<CurrencyRate> currencyRates;
        private ArrayList<CurrencyRate> currencyRatesPendingRemoval;
        boolean undoOn; // is undo on, you can turn it on from the toolbar menu
        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<CurrencyRate, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public RatesAdapter(ArrayList<CurrencyRate> currencyRates) {
            this.currencyRates = currencyRates;
            Collections.sort(currencyRates, new Comparator<CurrencyRate>() {
                @Override
                public int compare(CurrencyRate cr1, CurrencyRate cr2) {
                    return cr1.getName().compareTo(cr2.getName());
                }
            });

            currencyRatesPendingRemoval = new ArrayList<>();
        }

        @Override
        public RateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rate, parent, false);

            return new RateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RateViewHolder holder, int position) {
            CurrencyRate rate = currencyRates.get(position);

            Currency currency = currenciesMap.get(rate.getName());

            int thumbId = getResources().getIdentifier(rate.getName().toLowerCase(), "drawable", getPackageName());
            holder.rateThumbnail.setImageResource(thumbId);
            holder.rateThumbnail.setContentDescription(currency.getName());
            holder.rateName.setText(String.format("%s(%s)", currency.getName(), currency.getSign()));
            holder.rateValue.setText(curRateFormat.format(rate.getRate()));
            holder.rateDescription.setText(currency.getDescription());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return currencyRates.size();
        }

        public void setUndoOn(boolean undoOn) {
            this.undoOn = undoOn;
        }

        public boolean isUndoOn() {
            return undoOn;
        }

        public void pendingRemoval(int position) {
            final CurrencyRate rate = currencyRates.get(position);
            if (!currencyRatesPendingRemoval.contains(rate)) {
                currencyRatesPendingRemoval.add(rate);
                // this will redraw row in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(currencyRates.indexOf(rate));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(rate, pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            CurrencyRate rate = currencyRates.get(position);
            if (currencyRatesPendingRemoval.contains(rate)) {
                currencyRatesPendingRemoval.remove(rate);
            }
            if (currencyRates.contains(rate)) {
                currencyRates.remove(position);
                notifyItemRemoved(position);
            }
        }

        public boolean isPendingRemoval(int position) {
            CurrencyRate rate = currencyRates.get(position);
            return currencyRates.contains(rate);
        }
    }

    private class RateViewHolder extends RecyclerView.ViewHolder {
        ImageView rateThumbnail;
        TextView rateValue;
        TextView rateName;
        TextView rateDescription;

        public RateViewHolder(View view) {
            super(view);

            rateThumbnail = (ImageView) view.findViewById(R.id.list_item_rate_thumbnail);
            rateName = (TextView) view.findViewById(R.id.list_item_rate_name);
            rateValue = (TextView) view.findViewById(R.id.list_item_rate_value);
            rateDescription = (TextView) view.findViewById(R.id.list_item_rate_description);
        }
    }
}
