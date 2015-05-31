package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by limt on 5/31/2015.
 */
public class TessEngine {

    static final String TAG = "DBG_" + TessEngine.class.getName();

    private String output;

    private Context context;

    private TessEngine(Context context){
        this.context = context;
    }

    public static TessEngine Generate(Context context) {
        return new TessEngine(context);
    }

    public String detectText(Bitmap bitmap) {
        Log.d(TAG, "Initialization of TessBaseApi");
        TessDataManager.initTessTrainedData(context);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String path = TessDataManager.getTesseractFolder();
        Log.d(TAG, "Tess folder: " + path);
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng");
        Log.d(TAG, "Ended initialization of TessEngine");
        Log.d(TAG, "Running inspection on bitmap");
        tessBaseAPI.setImage(bitmap);
        output = tessBaseAPI.getUTF8Text();
        Log.d(TAG, "Got data: " + output);
        tessBaseAPI.end();
        System.gc();
        return output;
    }

    public String getText() {
        return output;
    }

}

