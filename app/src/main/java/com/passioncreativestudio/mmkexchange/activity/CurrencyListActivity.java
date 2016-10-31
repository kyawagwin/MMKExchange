package com.passioncreativestudio.mmkexchange.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.passioncreativestudio.mmkexchange.Currency;
import com.passioncreativestudio.mmkexchange.CurrencyRate;
import com.passioncreativestudio.mmkexchange.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CurrencyListActivity extends BaseActivity {
    private RecyclerView curListRV;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private HashMap<String, Currency> currenciesMap;
    private ArrayList<String> selectedCurrencies;

    private CurrenciesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.app_bar_currency_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        curListRV = (RecyclerView) findViewById(R.id.content_currency_list_curListRV);

        currenciesMap = Currency.initCurrencies();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        selectedCurrencies = new ArrayList<>();
        for(Currency cur : currenciesMap.values()) {
            if(prefs.getBoolean(cur.getName(), true)) {
                selectedCurrencies.add(cur.getName());
            }
        }





        adapter = new CurrenciesAdapter(currenciesMap);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        curListRV.setLayoutManager(mLayoutManager);
        curListRV.setItemAnimator(new DefaultItemAnimator());
        curListRV.setAdapter(adapter);

        adapter.notifyDataSetChanged();
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
                editor.commit();
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CurrenciesAdapter extends RecyclerView.Adapter<CurrencyViewHolder> {
        private HashMap<String, Currency> currenciesMap;
        private List<String> keys;

        public CurrenciesAdapter(HashMap<String, Currency> currenciesMap) {
            this.currenciesMap = currenciesMap;

            this.keys = new ArrayList<String>(currenciesMap.keySet());
            Collections.sort(keys, new Comparator<String>() {
                @Override
                public int compare(String cr1, String cr2) {
                    return cr1.compareTo(cr2);
                }
            });
        }

        @Override
        public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_currency, parent, false);

            return new CurrencyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CurrencyViewHolder holder, int position) {
            Currency cur = currenciesMap.get(keys.get(position));

            int thumbId = getResources().getIdentifier(cur.getName().toLowerCase(), "drawable", getPackageName());
            holder.flagIV.setImageResource(thumbId);
            holder.curName.setText(cur.getName());
            holder.curDescription.setText(cur.getDescription());

            if(selectedCurrencies.contains(cur.getName()))
                holder.isSelectedIV.setVisibility(View.VISIBLE);
            else
                holder.isSelectedIV.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView isSelectedIV = (ImageView) view.findViewById(R.id.list_item_currency_isSelectedIV);
                    TextView curTV = (TextView) view.findViewById(R.id.list_item_currency_curName);
                    String curName = curTV.getText().toString();
                    if(isSelectedIV.getVisibility() != View.VISIBLE) {
                        selectedCurrencies.add(curName);
                        isSelectedIV.setVisibility(View.VISIBLE);
                        editor.putBoolean(curName, true);
                        editor.apply();
                    } else {
                        selectedCurrencies.remove(curName);
                        isSelectedIV.setVisibility(View.GONE);
                        editor.putBoolean(curName, false);
                        editor.apply();
                    }
                }
            });

            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return keys.size();
        }
    }

    private class CurrencyViewHolder extends RecyclerView.ViewHolder {
        private ImageView flagIV;
        private TextView curName;
        private TextView curDescription;
        private ImageView isSelectedIV;

        public CurrencyViewHolder(View itemView) {
            super(itemView);

            flagIV = (ImageView) itemView.findViewById(R.id.list_item_currency_flagIV);
            curName = (TextView) itemView.findViewById(R.id.list_item_currency_curName);
            curDescription = (TextView) itemView.findViewById(R.id.list_item_currency_curDescription);
            isSelectedIV = (ImageView) itemView.findViewById(R.id.list_item_currency_isSelectedIV);
        }
    }
}
