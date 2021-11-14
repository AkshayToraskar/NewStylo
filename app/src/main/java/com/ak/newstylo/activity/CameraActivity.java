package com.ak.newstylo.activity;


import android.annotation.TargetApi;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ak.newstylo.R;
import com.ak.newstylo.app.CameraPreview;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.video.encoding.MediaEncoderEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CameraActivity extends AppCompatActivity {

    public static String TAG = CameraActivity.class.getName();

    @BindView(R.id.ib_back_button)
    ImageButton ibBack;
    @BindView(R.id.camera)
    CameraView cameraView;

    public File imgFile;
    public static FileCallback callback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
        cameraView.setLifecycleOwner(this);
        cameraView.addCameraListener(new Listener());

        callback = new FileCallback() {
            @Override
            public void onFileReady(@Nullable File file) {
                byte[] data = null;
                NewSessionActivity.saveCapturedData.onPictureTaken(data, Uri.fromFile(imgFile));
            }
        };
    }

    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.ib_record_button:
                this.imgFile = getOutputMediaFile();
                cameraView.takePicture();
                break;
            case R.id.ib_back_button:
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
    }

    /*Generate Media File*/
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "NewStylo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "NewStylo" + File.separator +
                "I" + timeStamp);
        try {
            mediaFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaFile;
    }


    /*closing animation*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
            super.onCameraOpened(options);
        }

        @Override
        public void onCameraClosed() {
            super.onCameraClosed();
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
            Log.v(TAG,"result "+result);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            result.toFile(imgFile, callback);
        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
              Log.v(TAG, newValue + " X");
        }
    }
}
