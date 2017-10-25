package com.ak.newstylo.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.ak.newstylo.R;
import com.ak.newstylo.adapter.CustomerAdapter;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.model.Customer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private List<Customer> patientList;
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
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    SessionManager sessionManager;
    public static int spnPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
