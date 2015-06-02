package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;


public class MenuResults extends Activity implements View.OnClickListener
{
    private String TAG = "MenuResults";

    // --------
    // onCreate : savedInstanceState -> void
    // called when Activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_results);

        Bundle hashmap = this.getIntent().getExtras();

        String menuText = hashmap.getString("menuText");
        menuText = menuText.toLowerCase();

        TextView inputText = (TextView) this.findViewById(R.id.inputText);
        LinearLayout resultsLayout = (LinearLayout) findViewById(R.id.menuLayout);

        ArrayList<Pair> menuItemPairs = getMenuItemNames(menuText, 10);

        int numItems = menuItemPairs.size();
        ArrayList<Button> menuItems = new ArrayList<Button>();
        for(int i = 0; i < numItems; i++)
        {
            Button curr = new Button(this);
            Pair thisPair = menuItemPairs.get(i);
            curr.setText((String) thisPair.getLeft());
            curr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            curr.setOnClickListener(this);
            curr.setBackgroundResource(R.drawable.button_style);
            resultsLayout.addView(curr);
            menuItems.add(curr);
        }

        inputText.setText("<< received text >>" + "\n'" + menuText + "'");
    }

    // -------
    // onClick : view -> void
    // handles selection of a menu result item
    @Override
    public void onClick(View view)
    {
        Button b = (Button) view;
        String query = b.getText().toString();
        Intent intent = new Intent(this, MenuResultQuery.class);
        intent.putExtra("query", query);
        this.startActivity(intent);
    }

    // ------------------------
    // getPotentialMenuItemNames : String -> ArrayList<String>
    // returns possible menu item names from raw menu text as an ArrayList<String>
    private ArrayList<String> getPotentialMenuItemNames(String s)
    {

        return getSubstrings(s, 4);
    }

    // -----------------
    // getSubstringOfLen : (String, int) -> ArrayList<String>
    // returns a list of all substrings of a given length
    private ArrayList<String> getSubstringsOfLen(String s, int len)
    {
        int fst = 0;
        int lst = len;
        int strlen = s.length();

        ArrayList<String> result = new ArrayList<String>();

        while (lst < strlen + 1)
        {
            result.add(s.substring(fst, lst));
            fst += 1;
            lst += 1;
        }

        return result;
    }

    // -------------
    // getSubstrings : (String, int) -> ArrayList<String>
    // returns a list of all substrings with some minimum length
    private ArrayList<String> getSubstrings(String s, int min)
    {
        ArrayList<String> result = new ArrayList<String>();

        int strlen = s.length();

        for (int i = min; i <= strlen; i++)
        {
            ArrayList<String> substrings = getSubstringsOfLen(s, i);
            for (String substring : substrings)
            {
                result.add(substring);
            }
        }

        return result;
    }

    // ------------------------
    // getPossibleMenuItemNames : void -> ArrayList<String>
    // retrieves possible menu item names from raw/items.txt
    private ArrayList<String> getPossibleMenuItemNames()
    {
        ArrayList<String> result = new ArrayList<String>();

        try
        {
            InputStream itemsStream = getResources().openRawResource(R.raw.items);
            InputStreamReader itemsReader_ = new InputStreamReader(itemsStream);
            BufferedReader itemsReader = new BufferedReader(itemsReader_);

            String line = itemsReader.readLine();

            while (line != null)
            {
                result.add(line);
                line = itemsReader.readLine();
            }

            try
            {
                itemsStream.close();
                itemsReader_.close();
                itemsReader.close();
            }
            catch (IOException ex)
            {
                Log.d(TAG, "IOException");
            }

        }
        catch (FileNotFoundException ex)
        {
            Log.d(TAG, "FileNotFoundException");
        }
        catch (IOException ex)
        {
            Log.d(TAG, "IOException");
        }

        return result;
    }

    // _______
    // removeDuplicatePairs : ArrayList<Pair> -> ArrayList<Pair>
    // removes duplicates based on value of left element
    private ArrayList<Pair> removeDuplicatePairs(ArrayList<Pair> pairs)
    {
        Iterator<Pair> iter = pairs.iterator();
        Hashtable<String, Boolean> seen = new Hashtable<String, Boolean>();

        while (iter.hasNext())
        {
            Pair curr = iter.next();
            String key = (String) curr.getLeft();
            if (seen.get(key) != null)
            {
                iter.remove();
            }
            else
            {
                seen.put((String) curr.getLeft(), true);
            }
        }

        return pairs;
    }

    // ----------------
    // getMenuItemNames : (String, int) -> ArrayList<Pair>
    // returns the top matched menu item names
    private ArrayList<Pair> getMenuItemNames(String menuText, int n)
    {
        ArrayList<String> possible = getPossibleMenuItemNames();
        ArrayList<String> potential = getPotentialMenuItemNames(menuText);

        ArrayList<Pair> result = new ArrayList<Pair>();

        for (String pot : potential)
        {
            for (String pos : possible)
            {
                double fuzziness = StringUtils.getJaroWinklerDistance(pot, pos);
                result.add(Pair.of(pos, fuzziness));
            }
        }

        Collections.sort(result, new Comparator<Pair>() {
                @Override
                public int compare(Pair p1, Pair p2) {
                    if ((double) p1.getRight() < (double) p2.getRight()) return 1;
                    else if ((double) p1.getRight() == (double) p2.getRight()) return 0;
                    else return -1;
                }
            }
        );
        Log.v(TAG, String.valueOf("BEFORE: " + result.size()));
        removeDuplicatePairs(result);
        Log.v(TAG, String.valueOf("AFTER: " + result.size()));

        return (n > result.size()) ?
                new ArrayList<Pair>(result.subList(0, result.size())) :
                new ArrayList<Pair>(result.subList(0, n));
    }
}
