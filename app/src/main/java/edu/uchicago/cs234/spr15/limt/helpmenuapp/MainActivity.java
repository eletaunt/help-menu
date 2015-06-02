package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.uchicago.cs234.spr15.limt.helpmenuapp.CameraEngine;
import edu.uchicago.cs234.spr15.limt.helpmenuapp.FocusBoxView;
import edu.uchicago.cs234.spr15.limt.helpmenuapp.Tools;
import edu.uchicago.cs234.spr15.limt.helpmenuapp.TessAsyncEngine;


import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener,
        Camera.PictureCallback, Camera.ShutterCallback, AsyncResponse {

    static final String TAG = "DBG_" + MainActivity.class.getName();

    static public String recognizedText;

    Button shutterButton;
    Button focusButton;
    Button flashButton;
    FocusBoxView focusBox;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;

    private boolean isFlashOn = false;

    AsyncResponse listener;
    TessAsyncEngine asyncTask = new TessAsyncEngine(listener);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listener = this;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "Surface Created - starting camera");

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }

        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera engine already on");
            return;
        }

        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();

        Log.d(TAG, "Camera engine started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Entering onResume");
        super.onResume();

        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
        focusBox = (FocusBoxView) findViewById(R.id.focus_box);
        shutterButton = (Button) findViewById(R.id.shutter_button);
        focusButton = (Button) findViewById(R.id.focus_button);
        flashButton = (Button) findViewById(R.id.flash_button);

        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);
        flashButton.setOnClickListener(this);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraFrame.setOnClickListener(this);
        Log.d(TAG, "Exiting onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);

    }

    @Override
    public void onClick(View v) {
        if (cameraEngine != null && cameraEngine.isOn()) {
            if (v == shutterButton) {
                cameraEngine.takeShot(this, this, this);
            } else if (v == focusButton) {
                cameraEngine.requestFocus();
            } else if (v == flashButton) {
                if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    Button b = (Button) this.findViewById(R.id.flash_button);
                    Camera.Parameters p = cameraEngine.camera.getParameters();
                    if (isFlashOn) {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        cameraEngine.camera.setParameters(p);
                        cameraEngine.camera.startPreview();
                        isFlashOn = false;
                        b.setBackgroundDrawable(getResources().getDrawable(R.drawable.focus_touch));
                        Log.d(TAG, "Light turned off");
                    } else {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        cameraEngine.camera.setParameters(p);
                        cameraEngine.camera.startPreview();
                        isFlashOn = true;
                        b.setBackgroundDrawable(getResources().getDrawable(R.drawable.focus_normal));
                        Log.d(TAG, "Light turned on");
                    }
                } else {
                    Log.d(TAG, "Camera flash not supported on this device");
                }
            }
        }
        else {
            Log.d(TAG, "Camera not supported on this device");
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Log.d(TAG, "Picture taken");

        if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }

        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getBox());

        Log.d(TAG, "Got bitmap");

        Log.d(TAG, "Initialization of TessBaseApi");

        new TessAsyncEngine(listener).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);

    }

    @Override
    public void onShutter() {

    }

    public void processFinish(String result){
        Log.d(TAG, "Entered processFinish");
        Log.d(TAG, "Result: " + result);

        // Open MenuResults with OCRed string
        Intent next = new Intent(this, MenuResults.class);
        next.putExtra("menuText", result);
        this.startActivity(next);
    }

}
