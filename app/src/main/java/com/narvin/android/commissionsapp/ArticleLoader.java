package com.narvin.android.commissionsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ArticleLoader extends AsyncTaskLoader<ArrayList<ArticleContract>> {

    /**
     * Public constructor
     *
     * @param context Calling parent activity
     */
    public ArticleLoader(Context context) {
        super(context);
    }

    /**
     * @param content JSON formatted string to be parsed from webHose API
     * @return ArticleList to be displayed in the listView
     */
    public static ArrayList<ArticleContract> parseJsonFeed(String content) {

        ArrayList<ArticleContract> articleContractList = new ArrayList<>();

        //Begin JSON parsing, retrieves article data and constructs the articles in a list
        try {
            JSONObject jsonObject = new JSONObject(content);

            JSONArray jsonArray = jsonObject.getJSONArray("posts");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject response = jsonArray.getJSONObject(i);

                JSONObject fields = response.getJSONObject("thread");

                String url = fields.getString("url");

                String subHeader = fields.getString("section_title");

                String header = fields.getString("title");

                articleContractList.add(new ArticleContract(url, header, null, subHeader));

            }
            return articleContractList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public ArrayList<ArticleContract> loadInBackground() {

        return parseJsonFeed(makeHttpRequest("http://webhose.io/search?token=5bcfc50b-c3d8-4cf0-b11c-a996d01b458f&format=json&q=thread.title%3A(improve%20sales)%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)&size=25&sort=relevancy&ts=1485227676331"));
    }

    /**
     * @param url webHose APIs set Url to retrieve the data from
     * @return JSON formatted String
     */
    private String makeHttpRequest(String url) {

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream stream = null;

        //Attempt to make connection to the API
        try {
            URL newUrl = new URL(url);
            urlConnection = (HttpURLConnection) newUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            stream = urlConnection.getInputStream();
            jsonResponse = readFromStream(stream);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonResponse;

    }

    /**
     * Helper method that extracts the data line by line and composes a final JSON String
     *
     * @param inputStream total output from HTTP Request
     * @return JSON formatted String
     */
    private String readFromStream(InputStream inputStream) {
        StringBuilder result = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            try {
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

}