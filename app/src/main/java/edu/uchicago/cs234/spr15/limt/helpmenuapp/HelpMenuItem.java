package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class HelpMenuItem extends AsyncTask<String, Void, JSONObject>
{
    private String _apiKey = "4vjBKxjJZ3ps7TQ7djXHcHpoUyEOqLdL9Dja6QYR";
    private String _name;
    private MenuResultQuery _act;


    public HelpMenuItem(MenuResultQuery act, String name)
    {
        _name = name;
        _act = act;
    }

    @Override
    protected JSONObject doInBackground(String... strings)
    {
        String query  = _name;
        String rawURL = "http://api.nal.usda.gov/usda/ndb/search/" +
                        "?format=" +    "json" +
                        "&q=" +         query.replace(" ", "%20") +
                        "&sort=" +      "r" +
                        "&max=" +       "1" +
                        "&offset=" +    "0" +
                        "&api_key=" +   _apiKey;

        String rawJSONString = getRawJSONString(rawURL);

        Log.v("asdoifjaoiwjfawef--->", rawJSONString);

        JSONObject j = null;
        try
        {
            j = getNDBDescription(new JSONObject(rawJSONString));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return j;
//
//        try
//        {
//            JSONObject json = new JSONObject(rawJSONString);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//
//        JSONObject json = new JSONObject();
//        try
//        {
//            URL url = new URL(rawURL);
//            URLConnection c = url.openConnection();
//            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
//
//            String line;
//
//            while ((line = in.readLine()) != null)
//            {
//                rawJSONString += line;
//            }
//            in.close();
//
//            json = new JSONObject(rawJSONString);
//            json = new JSONObject(json.getString("list"));
//            JSONArray j = new JSONArray(json.getString("item"));
//            json = j.getJSONObject(0);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return json;
    }

    private JSONObject getNDBDescription(JSONObject json)
    {
        String ndbno = "";
        try
        {
            JSONObject jObject = new JSONObject(json.getString("list"));
            JSONArray jArray = new JSONArray(jObject.getString("item"));
            jObject = jArray.getJSONObject(0);
            ndbno = jObject.getString("ndbno");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        String rawURL = "http://api.nal.usda.gov/usda/ndb/reports/" +
                        "?ndbno=" +     ndbno +
                        "&type=" +      "f" +
                        "&format=" +    "json" +
                        "&api_key=" +   _apiKey;

        String rawJSONString = getRawJSONString(rawURL);

        JSONObject j = null;
        try
        {
            j = new JSONObject(rawJSONString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return j;
    }

    private String getRawJSONString(String rawURL)
    {
        String result = "";

        try
        {
            URL url = new URL(rawURL);
            URLConnection c = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

            String line;

            while ((line = in.readLine()) != null)
            {
                result += line;
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(JSONObject j)
    {
        TextView name = (TextView) _act.findViewById(R.id.name);
        TextView random = (TextView) _act.findViewById(R.id.random);
        name.setText(_name);
        try
        {
            random.setText(j.toString(4));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            random.setText("Unfortunately, we could not find any details for '" + _name + "'.");
        }
    }
}
