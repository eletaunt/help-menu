package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class HelpMenuItem extends AsyncTask<String, Void, JSONObject>
{
    private String _name;

    public HelpMenuItem(String name)
    {
        _name = name;
    }

    @Override
    protected JSONObject doInBackground(String... strings)
    {
        String query  = _name;
        String apiKey = "4vjBKxjJZ3ps7TQ7djXHcHpoUyEOqLdL9Dja6QYR";
        String rawURL = "http://api.nal.usda.gov/usda/ndb/search/" +
                        "?format=" +    "json" +
                        "&q=" +         "banana" +
                        "&sort=" +      "r" +
                        "&max=" +       "1" +
                        "&offset=" +    "0" +
                        "&api_key=" +   apiKey;

        String rawJSONString = "";
        JSONObject json = new JSONObject();
        try
        {
            URL url = new URL(rawURL);
            URLConnection c = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

            String line;

            while ((line = in.readLine()) != null)
            {
                rawJSONString += line;
            }
            Log.v("THING ---->", "\n\n\n" + rawJSONString + "\n\n\n");
            in.close();

            json = new JSONObject(rawJSONString);
            Log.v("------->", json.getString("list"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return json;
    }
}
