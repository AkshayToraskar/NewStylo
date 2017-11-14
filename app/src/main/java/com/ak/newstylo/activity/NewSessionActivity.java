package com.ak.newstylo.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ak.newstylo.R;
import com.ak.newstylo.adapter.SessionImageAdapter;
import com.ak.newstylo.app.PreviewData;
import com.ak.newstylo.app.SaveCapturedData;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.app.Validate;
import com.ak.newstylo.model.Customer;
import com.ak.newstylo.model.ImageData;
import com.ak.newstylo.model.Session;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

//import com.aviary.android.feather.library.Constants;

public class NewSessionActivity extends AppCompatActivity implements SaveCapturedData, PreviewData {

    @BindView(R.id.etBillno)
    EditText etBillno;

    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.rvImageCollection)
    RecyclerView rvImageCollection;
    @BindView(R.id.llNoData)
    LinearLayout llNodata;
    @BindView(R.id.fabCapture)
    FloatingActionButton fabCapture;
    @BindView(R.id.tvMobile)
    TextView tvMobile;
    @BindView(R.id.tvLocality)
    TextView tvLocality;
    @BindView(R.id.tvDate)
    TextView tvDate;
    public static SaveCapturedData saveCapturedData;
    Realm realm;
    Validate validate;

    Long sessionId;
    Customer customer;

    public static long editImageId;
    private RealmList<ImageData> sessionImageList;
    public SessionImageAdapter mAdapter;
    public static int EDITIMAGE = 2;
    Session session;
    String dateText;

    // Customer patients;

    int sessionPos;
    //AwsOp awsopListener;
    //AwsOperations awsOperations;
    long customerId;

    public static ArrayList<ImageData> imageData;
    SessionManager sessionManager;
    public static PreviewData previewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validate = new Validate();
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        saveCapturedData = this;
        previewData = this;
        sessionManager = new SessionManager(this);
        // awsopListener = this;
        // awsOperations = new AwsOperations(this, awsopListener);


        //  awsOperations.getFileList("gyneacam/SessionData/akshay/1505381276384/1505381282706");


        imageData = new ArrayList<>();

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Session");

        etNote.addTextChangedListener(textWatcher);
        etBillno.addTextChangedListener(textWatcher);

        sessionImageList = new RealmList<>();

        //  try {

        dateText = String.valueOf(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));


        // Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);
        // dateText = new SimpleDateFormat("yyyy-MM-dd").format(date);
        //System.out.println(dateText); // 2011-01-18
        // } catch (ParseException e) {
        //     e.printStackTrace();
        // }


        if (getIntent().getExtras() != null) {
            customerId = getIntent().getExtras().getLong("customerId");
            sessionId = getIntent().getExtras().getLong("sessionId");
            sessionPos = getIntent().getExtras().getInt("pos");
            customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();
            Log.v("customer id", " " + customerId);

            if (customer != null) {
                getSupportActionBar().setTitle(customer.getFullname().equals("") || customer.getFullname() == null ? "New Session" : customer.getFullname());
                tvMobile.setText("Mobile: " + (customer.getMobile().equals("") || customer.getFullname() == null ? "-" : customer.getMobile()));
                tvLocality.setText("Locality: " + (customer.getLocality().equals("") || customer.getFullname() == null ? "-" : customer.getLocality()));
            }

            if (sessionId != 0) {
                session = realm.where(Session.class).equalTo("id", sessionId).findFirst();
                //session = MainActivity.doctor.getPatients().get(PatientHistoryActivity.pos).getSessions().get(sessionPos);

                // sessionImageList.clear();
                // sessionImageList.addAll(realm.where(ImageData.class).equalTo("sessionId", sessionId).findAll());
                // Collections.reverse(sessionImageList);
                if (session.getBillNo() != null) {
                    etBillno.setText(String.valueOf(session.getBillNo()));
                }
                if (session.getNote() != null) {
                    etNote.setText(session.getNote() + " ");
                }

                tvDate.setText("Date: " + session.getDate());


            } else {
                sessionId = new Date().getTime();


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        session = realm.createObject(Session.class, sessionId);
                        //session.setPatients(patients);
                        session.setDate(dateText);
                        session.setCustomerId(customerId);
                        session.setExported(false);
                        //MainActivity.doctor.getPatients().get(PatientHistoryActivity.pos).getSessions().add(session);
                        realm.copyToRealmOrUpdate(session);
                    }
                });

            }
            mAdapter = new SessionImageAdapter(this, sessionImageList, previewData);
            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rvImageCollection.setLayoutManager(mLayoutManager);
            rvImageCollection.setItemAnimator(new DefaultItemAnimator());
            rvImageCollection.setAdapter(mAdapter);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        sessionImageList.clear();
        sessionImageList.addAll(realm.where(ImageData.class).equalTo("sessionId", sessionId).findAll());
        Collections.reverse(sessionImageList);
        mAdapter.notifyDataSetChanged();

        if (sessionImageList.size() > 0) {
            rvImageCollection.setVisibility(View.VISIBLE);
            llNodata.setVisibility(View.GONE);
        } else {
            rvImageCollection.setVisibility(View.GONE);
            llNodata.setVisibility(View.VISIBLE);
        }


    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            saveData();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_session, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                if (etBillno.getText().toString().equals("") && etNote.getText().toString().equals("") && sessionImageList.size() == 0) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            session.deleteFromRealm();
                            Log.v("Session", "Deleted");
                        }
                    });

                }

                finish();
                break;


            case R.id.action_delete_session:

                new AlertDialog.Builder(this)
                        .setTitle("Delete Session")
                        .setMessage("Would you like to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Session session = realm.where(Session.class).equalTo("id", sessionId).findFirst();
                                        session.deleteFromRealm();

                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (etBillno.getText().toString().equals("") && etNote.getText().toString().equals("") && sessionImageList.size() == 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    session.deleteFromRealm();
                    Log.v("Session", "Deleted");
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  final String imageUri = String.valueOf(data.getData());

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == EDITIMAGE) {

                // final Uri path = Uri.fromFile(new File(data.getData().toString()));


                Bundle extra = data.getExtras();

                final String filepath = data.getStringExtra("path");

                if (null != extra) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ImageData imgdata = realm.where(ImageData.class).equalTo("id", editImageId).findFirst();
                                    /*imgdata.setByteArrayImage(inputData);*/
                            imgdata.setSessionId(sessionId);
                            imgdata.setDate(String.valueOf(new Date()));
                            imgdata.setPath(filepath);
                            realm.copyToRealmOrUpdate(imgdata);
                        }
                    });


                    mAdapter.notifyDataSetChanged();

                    previewData.updateData();

                }


            }

        }

    }

    public String inputStreamToFile(String uri) {

        OutputStream outputStream = null;
        File mediaFile = null;
        try {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam" + File.separator +
                    "Edited_I" + timeStamp);

            outputStream = new FileOutputStream(mediaFile);

            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(uri)));
            outputStream = new FileOutputStream(mediaFile);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return mediaFile.getPath();
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.fabCapture:
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                startActivity(cameraIntent);
                break;
        }
    }


    @Override
    public void onPictureTaken(final byte[] data, final Uri uri) {


       /* final MediaPlayer mp = MediaPlayer.create(this, R.raw.camera_shutter_click);
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });*/

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                String filepath[] = uri.getPath().split("/");

                String filename = filepath[filepath.length - 1];


                ImageData imgdata = realm.createObject(ImageData.class, new Date().getTime());
                //imgdata.setByteArrayImage(data);
                //imgdata.setOffline(true);
                imgdata.setSessionId(sessionId);
                imgdata.setPath(uri.getPath());
                //imgdata.setMediaType(1);
                imgdata.setDate(dateText);
                imgdata.setFilename(filename);
                sessionImageList.add(imgdata);
                mAdapter.notifyDataSetChanged();
                Log.v("updated TIme", "" + String.valueOf(new Date().getTime()));

                sessionManager.setLastUpdateTime(String.valueOf(new Date().getTime()));
            }
        });

        saveData();
    }

    @Override
    public void onVideoCaptured(final Uri uri) {

        // try {

        //  File videoFile = new File(uri.getPath());

        //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //  FileInputStream fis = new FileInputStream(videoFile);

        // byte[] buf = new byte[1024];
        // int n;

        // while (-1 != (n = fis.read(buf))) {
        //     baos.write(buf, 0, n);
        // }

        //    final byte[] videoBytes = baos.toByteArray();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ImageData imgdata = realm.createObject(ImageData.class, new Date().getTime());

                //   if(videoBytes!=null) {
                //      imgdata.setByteArrayImage(videoBytes);
                //  }
                imgdata.setSessionId(sessionId);
                //imgdata.setMediaType(2);
                imgdata.setPath(uri.getPath());
                imgdata.setDate(dateText);
                sessionImageList.add(imgdata);
                mAdapter.notifyDataSetChanged();
                Log.v("updated TIme", "" + String.valueOf(new Date().getTime()));
                sessionManager.setLastUpdateTime(String.valueOf(new Date().getTime()));
            }
        });

        saveData();

        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public void saveData() {


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String billnoTxt = etBillno.getText().toString().trim();
                // String problemTxt = etProblems.getText().toString().trim();
                String noteTxt = etNote.getText().toString().trim();

                if (!billnoTxt.equals("")) {
                    session.setBillNo(billnoTxt);
                }


                if (!noteTxt.equals("")) {
                    session.setNote(noteTxt);
                }

                session.setByteArrayImageData(sessionImageList);
                session.setExported(false);
                realm.copyToRealmOrUpdate(session);

                Log.v("updated TIme", "" + String.valueOf(new Date().getTime()));
                sessionManager.setLastUpdateTime(String.valueOf(new Date().getTime()));


            }
        });

    }

    @Override
    public void previewInfo(int position) {


        imageData.clear();

        imageData.addAll(sessionImageList);


        Bundle bundle = new Bundle();
        // bundle.putSerializable("images", (Serializable) imageData);
        bundle.putInt("position", position);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    @Override
    public void updateData() {
        //session = MainActivity.doctor.getPatients().get(PatientHistoryActivity.pos).getSessions().get(sessionPos);
        sessionImageList.clear();
        sessionImageList.addAll(realm.where(ImageData.class).equalTo("sessionId", sessionId).findAll());
        mAdapter.notifyDataSetChanged();
    }


}
