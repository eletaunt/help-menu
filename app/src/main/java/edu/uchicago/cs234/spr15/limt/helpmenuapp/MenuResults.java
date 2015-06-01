package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.getJaroWinklerDistance;

public class MenuResults extends Activity {

    private String _menuText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_results);

        Bundle hashmap = this.getIntent().getExtras();

        if (hashmap == null)
        {
            _menuText = "Spider Maki (5pcs)";
        }
        else
        {
            _menuText = hashmap.getString("menuText");
        }

        // retrieve components
        TextView testText = (TextView) this.findViewById(R.id.testText);
        TextView origText = (TextView) this.findViewById(R.id.origText);

        String displayText = new String();

        ArrayList<String> substrings = getPossibleMenuItemNames(_menuText);
        for (String s : substrings)
        {
            displayText += " ";
            displayText += s;
        }

        double test = getJaroWinklerDistance("mono maki chicken", "mono maki beef");

        testText.setText(String.valueOf(test));
        origText.setText(_menuText);

    }

    // getPossibleMenuItemNames
    // inputs : string s
    // return : possible menu item names from _s_
    private ArrayList<String> getPossibleMenuItemNames(String s)
    {
        //
        //
        return getSubstrings(s, 4);
    }

    // getSubstringOfLen
    // inputs : string s, int len
    // return : all substrings of length _len_ from _s_
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

    // getSubstrings
    // inputs : string s, int min
    // return : all substrings of any length from _s_ with minimum length _min_
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

    private ArrayList<String> getMenuItemNames()
    {
        // TODO

        return null;
    }
}
