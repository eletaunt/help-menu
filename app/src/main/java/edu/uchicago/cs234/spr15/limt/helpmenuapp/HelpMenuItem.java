package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.os.AsyncTask;
import android.text.Html;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

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
        if (rawJSONString == null)
            return null;

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

        TableLayout infoLayout = (TableLayout) _act.findViewById(R.id.table);

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
            return null;
//            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(JSONObject j)
    {
        TextView name = (TextView) _act.findViewById(R.id.name);

        TableLayout infoLayout = (TableLayout) _act.findViewById(R.id.table);
        ArrayList<TextView> infoItems = new ArrayList<TextView>();

        name.setText(_name);
        try
        {
            TextView resultNameT = new TextView(_act);

            if (j == null)
            {
                resultNameT.setText(Html.fromHtml("<b>Unable to find '" + _name + "'.</b>"));
                infoLayout.addView(resultNameT);
                return;
            }

            JSONObject n = new JSONObject(j.getString("report"));
            n = new JSONObject(n.getString("food"));

            String resultName = n.getString("name");
            resultNameT.setText(Html.fromHtml("<b>" + resultName + "</b>"));


            infoLayout.addView(resultNameT);

            JSONArray a = new JSONArray(n.getString("nutrients"));

            int numObjects = a.length();
            for (int i = 0; i < numObjects; i++)
            {
                JSONObject curr = new JSONObject(a.getString(i));

                TableRow tr =  new TableRow(_act);

                if (i % 2 == 0)
                    tr.setBackgroundResource(R.color.blue);

                TextView c1 = new TextView(_act);
                c1.setGravity(Gravity.CENTER);
                c1.setText(curr.getString("name"));

                TextView c2 = new TextView(_act);
                c2.setGravity(Gravity.CENTER);
                c2.setText(curr.getString("value") + " " + curr.getString("unit"));

                tr.addView(c1);
                tr.addView(c2);

                infoLayout.addView(tr);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
