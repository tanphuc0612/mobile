package com.example.giaothong.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetTask extends AsyncTask<String, String, String> {
    public AsyncResponse asyncResponse = null;
    public AfterGetLatLng afterGetLatLng = null;
    ProgressBar progressBar;

    public GetTask(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = "";
        try {
            data = downloadUrl(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);
        if (asyncResponse != null)
            asyncResponse.processFinish(s);
        else if (afterGetLatLng != null)
            afterGetLatLng.goToRouting(s);
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl.replaceAll(" ", "%20"));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        System.out.println(strUrl);
        System.out.println(data);
        return data;
    }
}
