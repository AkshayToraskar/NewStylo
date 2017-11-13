package com.ak.newstylo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.newstylo.R;
import com.ak.newstylo.adapter.CustomerAdapter;
import com.ak.newstylo.adapter.SessionHistoryAdapter;
import com.ak.newstylo.app.CsvOperation;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.model.Customer;
import com.ak.newstylo.model.ImageData;
import com.ak.newstylo.model.Session;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private List<Customer> customerList;
    private List<Session> sessionList;

    @BindView(R.id.rv_patient)
    RecyclerView recyclerView;

    @BindView(R.id.etSearchPatient)
    EditText etSearchPatient;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /* @BindView(R.id.spnFilter)
     Spinner spnFilter;*/
    @BindView(R.id.rg_filter)
    RadioGroup rgFilter;
    @BindView(R.id.rb_name)
    RadioButton rbName;
    @BindView(R.id.rb_mobile)
    RadioButton rbMobile;
    @BindView(R.id.rb_billno)
    RadioButton rbBillno;
    @BindView(R.id.llNoData)
    LinearLayout llNoData;

    int rbSelection = 0;
    public CustomerAdapter mCustomerAdapter;
    public SessionHistoryAdapter mSessionAdapter;

    Realm realm;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    public static int REQUEST_CODE = 14;
    private File selectedFile;

    SessionManager sessionManager;
    public static int spnPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        sessionManager = new SessionManager(this);
        setSupportActionBar(toolbar);


        getPermission();

        customerList = new ArrayList<>();
        sessionList = new ArrayList<>();

        setCustomerAdapter();


        /*spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    etSearchPatient.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    etSearchPatient.setInputType(InputType.TYPE_CLASS_PHONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {

                    case R.id.rb_name:
                        customerList.clear();
                        customerList.addAll(realm.where(Customer.class).findAll());
                        Collections.reverse(customerList);

                        etSearchPatient.setInputType(InputType.TYPE_CLASS_TEXT);
                        rbSelection = 0;
                        setCustomerAdapter();
                        etSearchPatient.setHint("Search Customer by Name");
                        break;

                    case R.id.rb_mobile:
                        customerList.clear();
                        customerList.addAll(realm.where(Customer.class).findAll());
                        Collections.reverse(customerList);

                        etSearchPatient.setInputType(InputType.TYPE_CLASS_PHONE);
                        rbSelection = 1;
                        setCustomerAdapter();
                        etSearchPatient.setHint("Search Customer by Mobile");
                        break;

                    case R.id.rb_billno:
                        etSearchPatient.setInputType(InputType.TYPE_CLASS_PHONE);
                        rbSelection = 2;

                        sessionList.clear();
                        sessionList.addAll(realm.where(Session.class).findAll());
                        Collections.reverse(sessionList);
                        setSessionAdapter();
                        etSearchPatient.setHint("Search measurment by billno");
                        break;

                }
            }
        });


        etSearchPatient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                customerList.clear();
                if (editable == null) {
                    switch (rbSelection) {
                        case 0:
                        case 1:
                            RealmResults<Customer> results = realm.where(Customer.class).findAll();
                            customerList.addAll(results);
                            Collections.reverse(customerList);
                            notifyData();
                            break;

                        case 2:
                            sessionList.clear();
                            sessionList.addAll(realm.where(Session.class).findAll());
                            Collections.reverse(sessionList);
                            notifyData();
                            break;
                    }

                    //mAdapter.notifyDataSetChanged();
                } else {

                    // spnPosition = spnFilter.getSelectedItemPosition();


                    switch (rbSelection) {

                        case 0:

                            customerList.addAll(realm.where(Customer.class).beginsWith("fullname", String.format(editable.toString())).findAll());
                            Collections.reverse(customerList);

                            mCustomerAdapter.notifyDataSetChanged();
                            break;

                        case 1:

                            customerList.addAll(realm.where(Customer.class).beginsWith("mobile", String.format(editable.toString())).findAll());
                            Collections.reverse(customerList);

                            mCustomerAdapter.notifyDataSetChanged();

                            break;

                        case 2:
                            sessionList.clear();
                            sessionList.addAll(realm.where(Session.class).beginsWith("billNo", String.format(editable.toString())).findAll());
                            Collections.reverse(sessionList);

                            mSessionAdapter.notifyDataSetChanged();
                            break;


                    }
                }


            }
        });


    }


    public void setCustomerAdapter() {
        mCustomerAdapter = new CustomerAdapter(this, customerList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);*/
        recyclerView.setAdapter(mCustomerAdapter);
    }

    public void setSessionAdapter() {
        mSessionAdapter = new SessionHistoryAdapter(this, sessionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);*/
        recyclerView.setAdapter(mSessionAdapter);
    }

    public void notifyData() {
        switch (rbSelection) {
            case 0:
                mCustomerAdapter.notifyDataSetChanged();
                break;

            case 1:
                mCustomerAdapter.notifyDataSetChanged();
                break;

            case 2:
                mSessionAdapter.notifyDataSetChanged();
                break;
        }
    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.fabAddNewPatient:
                startActivity(new Intent(this, AddCustomerActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;


            case R.id.action_export:
                //startActivity(new Intent(this, ProfileActivity.class));
                showDialog(this, "Import/Export Data");
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (rbSelection) {
            case 0:
            case 1:
                customerList.clear();
                customerList.addAll(realm.where(Customer.class).findAll());
                Collections.reverse(customerList);
                setCustomerAdapter();
                break;

            case 2:
                sessionList.clear();
                sessionList.addAll(realm.where(Session.class).findAll());
                Collections.reverse(sessionList);
                setSessionAdapter();
                break;
        }


        //mAdapter.notifyDataSetChanged();
        notifyData();

        if (customerList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }


    }


    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            // txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])) {
                //   txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void proceedAfterPermission() {
        //   txtPermissions.setText("We've got all permissions");
        // Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.iv_close);
        Button btnImport = (Button) dialog.findViewById(R.id.btn_import);
        Button btnExport = (Button) dialog.findViewById(R.id.btn_export);

        text.setText(msg);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilePickerActivityLatest.class);
                startActivityForResult(intent, REQUEST_CODE);

                dialog.dismiss();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCSV();

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {


            if (data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {

                selectedFile = new File
                        (data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));


                Log.v("file path", " " + selectedFile.getPath());
                parseCSVData();
            }


        }

    }


    //export the data into csv file
    public void generateCSV() {

        try {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "NewStylo");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            List<String[]> data = new ArrayList<String[]>();
            CsvOperation csvOperation = new CsvOperation();
            List<String[]> strData = csvOperation.generateExportedList();
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "NewStylo" + File.separator + "Exported Data_" + currentDateTimeString + ".csv";
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csv));

            for (int k = 0; k < strData.size(); k++) {
                data.add(strData.get(k));
            }


            writer.writeAll(data);
            writer.close();
            Log.v("Export Data", "SUCCESS");

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    List<Session> sessions = realm.where(Session.class).equalTo("isExported", false).findAll();
                    for (Session se : sessions) {
                        se.setExported(true);
                        realm.copyToRealmOrUpdate(se);
                    }

                    List<ImageData> imgD = realm.where(ImageData.class).equalTo("isExported", false).findAll();
                    for (ImageData imD : imgD) {
                        imD.setExported(true);
                        realm.copyToRealmOrUpdate(imD);
                    }
                }
            });


            Toast.makeText(getApplicationContext(), "Data Exported Successfully into " + File.separator + "Exported Data_" + currentDateTimeString + ".csv", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Export Data", "FAIL");
        }
    }

    //import csv data
    private void parseCSVData() {

        CSVReader reader;
        try {
            if (getFileExt(selectedFile.getName()).equals("csv")) {

                reader = new CSVReader(new FileReader(selectedFile));
                String[] row;
                List<?> content = reader.readAll();
                int rowCount = 0;
                if (content != null) {
                    for (Object object : content) {
                        if (rowCount > 0) {
                            row = (String[]) object;
                            for (int i = 0; i < row.length; i++) {
                                // display CSV values
                                System.out.println("Cell column index: " + i);
                                System.out.println("Cell Value: " + row[i]);
                                System.out.println("-------------");
                            }
                            // final String strId = row[0] + row[2] + row[3] + row[5];

                            final String[] finalRow = row;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    long id = Long.parseLong(finalRow[0]);
                                    Customer customer = realm.where(Customer.class).equalTo("id", id).findFirst();
                                    if (customer == null) {
                                        customer = realm.createObject(Customer.class, id);
                                    }
                                    customer.setFullname(finalRow[1]);
                                    customer.setLocality(finalRow[2]);
                                    customer.setMobile(finalRow[3]);
                                    realm.copyToRealmOrUpdate(customer);


                                    if (finalRow.length > 4) {
                                        long sessionId = Long.parseLong(finalRow[4]);
                                        Session session = realm.where(Session.class).equalTo("id", sessionId).findFirst();
                                        if (session == null) {
                                            session = realm.createObject(Session.class, sessionId);
                                        }
                                        session.setDate(finalRow[5]);
                                        session.setBillNo(finalRow[6]);
                                        session.setNote(finalRow[7]);
                                        session.setCustomerId(Long.parseLong(finalRow[8]));
                                        realm.copyToRealmOrUpdate(session);
                                    }

                                    if (finalRow.length > 9) {
                                        long imageId = Long.parseLong(finalRow[9]);
                                        ImageData imageData = realm.where(ImageData.class).equalTo("id", imageId).findFirst();
                                        if (imageData == null) {
                                            imageData = realm.createObject(ImageData.class, imageData);
                                        }
                                        imageData.setDate(finalRow[10]);
                                        imageData.setFilename(finalRow[11]);
                                        imageData.setPath(finalRow[12]);
                                        imageData.setSessionId(Long.parseLong(finalRow[13]));
                                        realm.copyToRealmOrUpdate(imageData);
                                    }
                                }
                            });
                        } else {
                            rowCount = rowCount + 1;
                        }
                    }
                }

                notifyData();
                Toast.makeText(getApplicationContext(), "Data Successfully Imported..!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Select .csv file", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());

            Toast.makeText(getApplicationContext(), "File is not proper format", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            Toast.makeText(getApplicationContext(), "File is not proper format", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Toast.makeText(getApplicationContext(), "File is not proper format", Toast.LENGTH_SHORT).show();
        }

    }


    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).trim();
    }
}
