package com.passioncreativestudio.mmkexchange;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {

    }

    public String makeServiceCall(String requestUrl, String requestMethod) {
        String response = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);

            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(inputStream);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        return sb.toString();
    }
}
