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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.newstylo.R;
import com.ak.newstylo.adapter.CustomerAdapter;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.model.Customer;
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
    @BindView(R.id.rv_patient)
    RecyclerView recyclerView;
    public CustomerAdapter mAdapter;
    @BindView(R.id.etSearchPatient)
    EditText etSearchPatient;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spnFilter)
    Spinner spnFilter;
    @BindView(R.id.llNoData)
    LinearLayout llNoData;


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

        mAdapter = new CustomerAdapter(this, customerList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);


        spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    RealmResults<Customer> results = realm.where(Customer.class).findAll();
                    customerList.addAll(results);
                    Collections.reverse(customerList);
                    mAdapter.notifyDataSetChanged();
                } else {

                    spnPosition = spnFilter.getSelectedItemPosition();


                    switch (spnPosition) {

                        case 0:

                            customerList.addAll(realm.where(Customer.class).beginsWith("fullname", String.format(editable.toString()).toLowerCase()).findAll());
                            Collections.reverse(customerList);

                            mAdapter.notifyDataSetChanged();
                            break;

                        case 1:

                            customerList.addAll(realm.where(Customer.class).beginsWith("mobile", String.format(editable.toString()).toLowerCase()).findAll());
                            Collections.reverse(customerList);

                            mAdapter.notifyDataSetChanged();

                            break;


                    }
                }


            }
        });


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


        customerList.clear();
        customerList.addAll(realm.where(Customer.class).findAll());
        Collections.reverse(customerList);

        mAdapter.notifyDataSetChanged();

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

       /* try {

            File myDirectory = new File(Environment.getExternalStorageDirectory(), "NewStylo");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            List<String[]> data = new ArrayList<String[]>();

            CsvOperation csvOperation = new CsvOperation(surveyHistory, survId);
            List<String[]> strData = csvOperation.generateString();
            Survey survey = realm.where(Survey.class).equalTo("id", survId).findFirst();

            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SEARCH" + File.separator + survey.getName() + "_" + "Ans_" + currentDateTimeString + ".csv";
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csv));

            for (int k = 0; k < strData.size(); k++) {
                data.add(strData.get(k));
            }


            writer.writeAll(data);
            writer.close();
            Log.v("Export Data", "SUCCESS");

            Toast.makeText(getApplicationContext(), "Data Exported Successfully into " + survey.getName() + "_" + "Ans_" + currentDateTimeString + ".csv file", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Export Data", "FAIL");
        }*/
    }


    //import csv data
    private void parseCSVData() {


        /*CSVReader reader;
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

                            final String strId = row[0] + row[2] + row[3] + row[5];

                            final String[] finalRow = row;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    long id = Long.parseLong(finalRow[13]);
                                    Patients patients = realm.where(Patients.class).equalTo("id", id).findFirst();

                                    if (patients == null) {
                                        patients = realm.createObject(Patients.class, id);
                                    }

                                    patients.setHouseId(strId);
                                    patients.setPatientname(finalRow[7]);
                                    patients.setAge(Integer.parseInt(finalRow[8]));
                                    patients.setSex(Integer.parseInt(finalRow[9]));

                                    *//*DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    Date date = new Date();
                                    dateFormat.format(date);
                                    patients.setSaveDate(date);*//*

                                    realm.copyToRealmOrUpdate(patients);
                                }
                            });

                        } else {
                            rowCount = rowCount + 1;
                        }


                    }
                }

                patientList.addAll(realm.where(Patients.class).findAll());
                mAdapter.notifyDataSetChanged();

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
        }*/


    }


    public static String getFileExt(String fileName) {

        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).trim();
    }

}
