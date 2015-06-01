package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class MenuResultQuery extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_result_query);

        Bundle hashmap = this.getIntent().getExtras();
        String query = hashmap.getString("query");

        HelpMenuItem item = new HelpMenuItem(this, query);

        TextView name = (TextView) this.findViewById(R.id.name);
        name.setText(query);

        item.execute();
    }
}