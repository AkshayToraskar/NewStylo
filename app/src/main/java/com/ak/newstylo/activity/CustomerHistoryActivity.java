package com.ak.newstylo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.newstylo.R;
import com.ak.newstylo.adapter.SessionHistoryAdapter;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.model.Customer;
import com.ak.newstylo.model.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class CustomerHistoryActivity extends AppCompatActivity {


    @BindView(R.id.tvMobile)
    TextView tvMobile;
    @BindView(R.id.tvLocality)
    TextView tvLocality;
    @BindView(R.id.rvSessionHistory)
    RecyclerView rvSessionHistory;
    @BindView(R.id.llNoData)
    LinearLayout llNoData;
    @BindView(R.id.etSearchMeasurment)
    EditText etSearchMeasurment;

    private List<Session> sessionList;
    public SessionHistoryAdapter mAdapter;

    Customer customer;
    Long customerId;
    Realm realm;
    public static int pos;
    SessionManager sessionManager;


    String sessionId;

    ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sessionList = new ArrayList<>();


        if (getIntent().getExtras() != null) {
            customerId = getIntent().getExtras().getLong("customerId");
            pos = getIntent().getExtras().getInt("pos");

            //Doctor doctor = realm.where(Doctor.class).equalTo("username", sessionManager.getUsername()).findFirst();

            //patients = MainActivity.doctor.getPatients().get(pos);


            customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

            Log.v("aa"," cu"+customerId);

            if (customer != null) {

                getSupportActionBar().setTitle(customer.getFullname());

                tvMobile.setText("Mobile: " + ((customer.getMobile().equals("")) ? "-" : customer.getMobile()));
                tvLocality.setText("Locality: " + ((customer.getLocality().equals("")) ? "-" : customer.getLocality()));


                //List<Session> aa = patients.getSessions(); //realm.where(Session.class).equalTo("patients.id",patientId).findAll();
                sessionList.clear();
                sessionList.addAll(realm.where(Session.class).equalTo("customerId", customerId).findAll());
                Collections.reverse(sessionList);

                mAdapter = new SessionHistoryAdapter(this, sessionList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvSessionHistory.setLayoutManager(mLayoutManager);
                rvSessionHistory.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.ItemDecoration itemDecoration = new
                        DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                rvSessionHistory.addItemDecoration(itemDecoration);
                rvSessionHistory.setAdapter(mAdapter);

            }

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent i = new Intent(CustomerHistoryActivity.this, NewSessionActivity.class);
                i.putExtra("customerId", customerId);
                startActivity(i);
            }
        });

        etSearchMeasurment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                sessionList.clear();
                if (editable == null) {
                    RealmResults<Session> results = realm.where(Session.class).findAll();
                    sessionList.addAll(results);
                    Collections.reverse(sessionList);
                    mAdapter.notifyDataSetChanged();
                } else {

                    sessionList.addAll(realm.where(Session.class).beginsWith("billNo", String.valueOf(editable)).findAll());
                    Collections.reverse(sessionList);
                    mAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_edit_customer:
                Intent i = new Intent(this, AddCustomerActivity.class);
                i.putExtra("customerId", customerId);
                startActivity(i);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (customerId != null) {

            //patients = realm.where(Patients.class).equalTo("id", patientId).findFirst();


            sessionList.clear();
            //sessionList.addAll(MainActivity.doctor.getPatients().get(pos).getSessions());
            sessionList.addAll(realm.where(Session.class).equalTo("customerId", customerId).findAll());
            Collections.reverse(sessionList);
            mAdapter.notifyDataSetChanged();

            if (sessionList.size() > 0) {
                rvSessionHistory.setVisibility(View.VISIBLE);
                llNoData.setVisibility(View.GONE);
            } else {
                rvSessionHistory.setVisibility(View.GONE);
                llNoData.setVisibility(View.VISIBLE);
            }

        }
    }


}
