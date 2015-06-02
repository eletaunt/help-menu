package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by limt on 5/31/2015.
 */
public class TessAsyncEngine extends AsyncTask<Object, Void, String> {
    public AsyncResponse listener;

    static final String TAG = "DBG_" + TessAsyncEngine.class.getName();

    private Bitmap bmp;

    private Activity context;

    @Override
    protected String doInBackground(Object... params) {

        try {

            if(params.length < 2) {
                Log.e(TAG, "Error passing parameter to execute - missing params");
                return null;
            }

            if(!(params[0] instanceof Activity) || !(params[1] instanceof Bitmap)) {
                Log.e(TAG, "Error passing parameter to execute(context, bitmap)");
                return null;
            }

            context = (Activity)params[0];

            bmp = (Bitmap)params[1];

            if(context == null || bmp == null) {
                Log.e(TAG, "Error passed null parameter to execute(context, bitmap)");
                return null;
            }

            int rotate = 0;

            if(params.length == 3 && params[2]!= null && params[2] instanceof Integer){
                rotate = (Integer) params[2];
            }

            if(rotate >= -180 && rotate <= 180 && rotate != 0)
            {
                bmp = Tools.preRotateBitmap(bmp, rotate);
                Log.d(TAG, "Rotated OCR bitmap " + rotate + " degrees");
            }

            TessEngine tessEngine =  TessEngine.Generate(context);

            bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

            String result = tessEngine.detectText(bmp);

            Log.d(TAG, result);

            MainActivity.recognizedText = result; // set string

            return result;

        } catch (Exception ex) {
            Log.d(TAG, "Error: " + ex + "\n" + ex.getMessage());
        }

        return null;
    }

    public TessAsyncEngine(AsyncResponse listener){
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null || bmp == null || context == null) {
            Log.d(TAG, "Output/bmp/context is null!");
            return;
        }

        if (listener == null) {
            Log.d(TAG, "listener is null");
        }


        String output = result;
        Log.d(TAG, "About to run processFinish");
        listener.processFinish(output);
        Log.d(TAG, "Done running processFinish");
    }
}

