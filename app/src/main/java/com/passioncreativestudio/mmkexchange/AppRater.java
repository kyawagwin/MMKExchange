package com.passioncreativestudio.mmkexchange;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.passioncreativestudio.mmkexchange.Constants.APP_DOMAIN;
import static com.passioncreativestudio.mmkexchange.Constants.APP_TITLE;
import static com.passioncreativestudio.mmkexchange.Constants.DAYS_UNTIL_PROMPT;
import static com.passioncreativestudio.mmkexchange.Constants.KEY_SHARED_PREFS_APP_RATER;
import static com.passioncreativestudio.mmkexchange.Constants.KEY_SHARED_PREFS_DATE_FIRST_LAUNCH;
import static com.passioncreativestudio.mmkexchange.Constants.KEY_SHARED_PREFS_DONT_SHOW_AGAIN;
import static com.passioncreativestudio.mmkexchange.Constants.KEY_SHARED_PREFS_LAUNCH_COUNT;
import static com.passioncreativestudio.mmkexchange.Constants.LAUNCHES_UNTIL_PROMPT;

public class AppRater {
    public static void appLaunched(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_SHARED_PREFS_APP_RATER, 0);
        if(preferences.getBoolean(KEY_SHARED_PREFS_DONT_SHOW_AGAIN, false))
            return;

        SharedPreferences.Editor editor = preferences.edit();

        long launchCount = preferences.getLong(KEY_SHARED_PREFS_LAUNCH_COUNT, 0) + 1;
        editor.putLong(KEY_SHARED_PREFS_LAUNCH_COUNT, launchCount);

        long dateFirstLaunch = preferences.getLong(KEY_SHARED_PREFS_DATE_FIRST_LAUNCH, 0);
        if(dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong(KEY_SHARED_PREFS_DATE_FIRST_LAUNCH, dateFirstLaunch);
        }

        if(launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if(System.currentTimeMillis() >= dateFirstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(context, editor);
            }
        }

        editor.apply();
    }

    private static void showRateDialog(final Context context, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        TextView textView = new TextView(context);
        textView.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        textView.setWidth(500);
        textView.setPadding(4, 0, 4, 10);
        linearLayout.addView(textView);

        Button rateButton = new Button(context);
        rateButton.setText(String.format("Rate %s", APP_TITLE));
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_DOMAIN)));
                dialog.dismiss();
            }
        });
        linearLayout.addView(rateButton);

        Button laterButton = new Button(context);
        laterButton.setText(R.string.remind_me_later);
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset the counter to pop up rate again
                editor.putLong(KEY_SHARED_PREFS_DATE_FIRST_LAUNCH, System.currentTimeMillis());
                editor.putLong(KEY_SHARED_PREFS_LAUNCH_COUNT, 0);

                dialog.dismiss();
            }
        });
        linearLayout.addView(laterButton);

        Button noButton = new Button(context);
        noButton.setText(R.string.no_thank_you);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editor != null) {
                    editor.putBoolean(KEY_SHARED_PREFS_DONT_SHOW_AGAIN, true);
                    editor.commit();
                }

                dialog.dismiss();
            }
        });
        linearLayout.addView(noButton);

        dialog.setContentView(linearLayout);
        dialog.show();
    }
}

