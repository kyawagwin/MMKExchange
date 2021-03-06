package com.passioncreativestudio.mmkexchange.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passioncreativestudio.mmkexchange.PrefManager;
import com.passioncreativestudio.mmkexchange.R;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private WelcomePagerAdapter pagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button skipButton, nextButton;
    private PrefManager prefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if(!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        skipButton = (Button) findViewById(R.id.btn_skip);
        nextButton = (Button) findViewById(R.id.btn_next);

        layouts = new int[] {
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4
        };

        addBottomDots(0);

        changeStatusBarColor();

        pagerAdapter = new WelcomePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = getItem(+1);
                if(current < layouts.length)
                    viewPager.setCurrentItem(current);
                else
                    launchHomeScreen();
            }
        });
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for(int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dots[i].setText(Html.fromHtml("&#8226;",Html.FROM_HTML_MODE_LEGACY));
            } else {
                dots[i].setText(Html.fromHtml("&#8226;"));
            }

            dots[i].setTextSize(36);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if(position == layouts.length - 1) {
                nextButton.setText(getString(R.string.start));
                skipButton.setVisibility(View.GONE);
            } else {
                nextButton.setText(getString(R.string.next));
                skipButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class WelcomePagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public WelcomePagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
