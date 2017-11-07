package com.ak.newstylo.activity;


import android.annotation.TargetApi;
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
import android.support.v7.app.AppCompatActivity;
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


import com.ak.newstylo.R;
import com.ak.newstylo.app.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.frm_content)
    FrameLayout preview;
    /*  @BindView(R.id.ib_photo_video_camera_switcher)
      ImageButton ibPhotoVideoCamSwitcher;
      @BindView(R.id.ib_record_button)
      ImageButton ibRecord;*/
    @BindView(R.id.ib_back_button)
    ImageButton ibBack;
    /*@BindView(R.id.tv_record_duration_text)
    TextView tvRecordDuration;*/
    @BindView(R.id.focus_index)
    View focusIndex;


    public static String TAG = CameraActivity.class.getName();
    //  public static int SWITCHER = 0; //0camera 1recording
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //Record Durations
    //  private long startTime = 0L;
    //   private Handler customHandler = new Handler();
    //  long timeInMilliseconds = 0L;
    //  long timeSwapBuff = 0L;
    //   long updatedTime = 0L;

    //    public MediaRecorder mMediaRecorder;
    //   public boolean isRecording;
    private Camera mCamera;
    private CameraPreview mCameraPreview;


    private float pointX, pointY;
    static final int FOCUS = 1;            //     Focus on
    static final int ZOOM = 2;            // Zoom
    private int mode;                      //0 is focused 1 is zoomed in
    private float dist;
    private Camera cameraInst = null;
    private Handler handler = new Handler();
    private Camera.Parameters parameters = null;

    public static File mediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        mCameraPreview.setFocusableInTouchMode(true);
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setRotation(90);
        mCamera.setParameters(params);
        preview.addView(mCameraPreview);

        mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    // Main point press
                    case MotionEvent.ACTION_DOWN:
                        pointX = motionEvent.getX();
                        pointY = motionEvent.getY();
                        mode = FOCUS;
                        break;
                    // Click the next point
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(motionEvent);
                        // If the distance between two consecutive points is greater than 10, then determine the multi-point mode
                        if (spacing(motionEvent) > 10f) {
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = FOCUS;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == FOCUS) {
                            pointFocus((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                        } else if (mode == ZOOM) {
                            float newDist = spacing(motionEvent);
                            if (newDist > 10f) {
                                float tScale = (newDist - dist) / dist;
                                if (tScale < 0) {
                                    tScale = tScale * 2;
                                }
                                addZoomIn((int) tScale);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        mCameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //pointFocus((int) pointX, (int) pointY);
                    Log.v("A", "X=" + pointX + " Y=" + pointY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusIndex.getLayoutParams());
                layout.setMargins((int) pointX - 60, (int) pointY - 60, 0, 0);
                focusIndex.setLayoutParams(layout);
                focusIndex.setVisibility(View.VISIBLE);
                ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(800);
                focusIndex.startAnimation(sa);

                handler.postDelayed(new Runnable() {
                    public void run() {
                        focusIndex.setVisibility(View.INVISIBLE);
                    }
                }, 800);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        //  SWITCHER = 0;
    }

    public void onBtnClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.ib_record_button:

                // switch (SWITCHER) {
                //    case 0:
                takePicture();
                //         break;
                //      case 1:
                //startRecording();
                //        break;
                // }
                break;

           /* case R.id.ib_photo_video_camera_switcher:
                if (SWITCHER == 0) {
                    SWITCHER = 1;
                    ibPhotoVideoCamSwitcher.setImageResource(R.drawable.ic_photo_camera_white_24dp);
                    ibRecord.setImageResource(R.drawable.start_video_record_button);
                    tvRecordDuration.setVisibility(View.VISIBLE);
                } else {
                    SWITCHER = 0;
                    ibPhotoVideoCamSwitcher.setImageResource(R.drawable.ic_videocam_white_24dp);
                    ibRecord.setImageResource(R.drawable.take_photo_button);
                    tvRecordDuration.setVisibility(View.INVISIBLE);
                }
                break;*/

            case R.id.ib_back_button:
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;

        }

    }

    private Camera getCameraInstance() {

        try {
            cameraInst = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return cameraInst;
    }


    /*Capture Image*/
    public void takePicture() {
        Log.i(TAG, "Tacking picture");
        Camera.PictureCallback callback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "Saving a bitmap to file");
                File pictureFile = null;
                try {
                    pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                    if (pictureFile == null) {
                        return;
                    }

                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    mCamera.startPreview();

                    NewSessionActivity.saveCapturedData.onPictureTaken(data, Uri.fromFile(mediaFile));

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        };


        mCamera.takePicture(null, null, callback);


    }

    /*Video recording*/
    /*public void startRecording() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            isRecording = false;
            ibRecord.setImageResource(R.drawable.start_video_record_button);

            timeSwapBuff += timeInMilliseconds;
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            customHandler.removeCallbacks(updateTimerThread);


            ibBack.setVisibility(View.VISIBLE);
            ibPhotoVideoCamSwitcher.setVisibility(View.VISIBLE);

            NewSessionActivity.saveCapturedData.onVideoCaptured(Uri.fromFile(mediaFile));

        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                ibRecord.setImageResource(R.drawable.stop_button_background);

                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);

                ibPhotoVideoCamSwitcher.setVisibility(View.INVISIBLE);
                ibBack.setVisibility(View.INVISIBLE);
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
    }

    private boolean prepareVideoRecorder() {

        //  mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());


        mMediaRecorder.setOrientationHint(90);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }


    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            tvRecordDuration.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%02d", milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };*/


    /*Generate Media File*/
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam");
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

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam" + File.separator +
                    "I" + timeStamp);
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam" + File.separator +
                    "V" + timeStamp);
        } else {
            return null;
        }

        return mediaFile;
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*closing animation*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    /*FOCUS*/
    private float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    //Zoom in and out
    int curZoomValue = 0;

    private void addZoomIn(int delta) {

        try {
            Camera.Parameters params = cameraInst.getParameters();
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported());
            if (!params.isZoomSupported()) {
                return;
            }
            curZoomValue += delta;
            if (curZoomValue < 0) {
                curZoomValue = 0;
            } else if (curZoomValue > params.getMaxZoom()) {
                curZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(curZoomValue);
                cameraInst.setParameters(params);
                return;
            } else {
                cameraInst.startSmoothZoom(curZoomValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fixed focus code
    private void pointFocus(int x, int y) {
        cameraInst.cancelAutoFocus();
        parameters = cameraInst.getParameters();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y);
        }
        cameraInst.setParameters(parameters);
        autoFocus();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showPoint(int x, int y) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            //Xy changed
            int rectY = -x * 2000 / mCameraPreview.getWidth() + 1000;
            int rectX = y * 2000 / mCameraPreview.getHeight() - 1000;

            int left = rectX < -900 ? -1000 : rectX - 100;
            int top = rectY < -900 ? -1000 : rectY - 100;
            int right = rectX > 900 ? 1000 : rectX + 100;
            int bottom = rectY > 900 ? 1000 : rectY + 100;
            Rect area1 = new Rect(left, top, right, bottom);
            areas.add(new Camera.Area(area1, 800));
            parameters.setMeteringAreas(areas);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    //To achieve autofocus
    private void autoFocus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (cameraInst == null) {
                    return;
                }
                cameraInst.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            //initCamera();//Implement the camera initialization parameters
                        }
                    }
                });
            }
        };
    }

}
