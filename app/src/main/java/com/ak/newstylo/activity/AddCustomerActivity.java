package com.ak.newstylo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.ak.newstylo.R;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.app.Validate;
import com.ak.newstylo.model.Customer;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddCustomerActivity extends AppCompatActivity {

    @BindView(R.id.etId)
    EditText etId;
    @BindView(R.id.etFirstName)
    EditText etFirstName;
    @BindView(R.id.etLastName)
    EditText etLastName;
    @BindView(R.id.etLocality)
    EditText etLocality;
    @BindView(R.id.etMobile)
    EditText etMobile;

    @BindView(R.id.btnAddPatient)
    Button btnAddPatient;
    @BindView(R.id.btnRemovePatient)
    Button btnRemovePatient;

    Realm realm;
    Validate validate;
    SessionManager session;
    long patientId = 0;


    Customer patients;
    boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        validate = new Validate();
        session = new SessionManager(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            patientId = getIntent().getExtras().getLong("patientId");

            patients = realm.where(Customer.class).equalTo("id", patientId).findFirst();

            if (patients != null) {
                etId.setText(String.valueOf(patients.getPatientId()));
                etMobile.setText(patients.getMobile());
                etLocality.setText(patients.getLocality());
                etFirstName.setText(patients.getFirstname());
                etLastName.setText(patients.getLastname());
            }

            update = true;

        }

        if (update) {
            getSupportActionBar().setTitle("Update Patient");
            btnAddPatient.setText("Update");
            btnRemovePatient.setVisibility(View.VISIBLE);
        } else {
            btnAddPatient.setText("Add");
            btnRemovePatient.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.btnAddPatient:

                final long pid;
                final String lastname, locality;

                if (validate.validateString(etId.getText().toString())) {
                    // etId.setError("Enter Unique Id");
                    // return;
                    pid = 0;
                } else {
                    // etId.setError(null);
                    pid = Long.parseLong(etId.getText().toString());
                }

                if (validate.validateString(etLastName.getText().toString())) {
                    // etLastName.setError("Enter Last Name");
                    // return;
                    lastname = "";
                } else {
                    // etLastName.setError(null);
                    lastname = etLastName.getText().toString();
                }

                if (validate.validateString(etFirstName.getText().toString())) {
                    etFirstName.setError("Enter First Name");
                    return;
                } else {
                    etFirstName.setError(null);
                }

                if (validate.validateString(etMobile.getText().toString())) {
                    etMobile.setError("Enter Mobile No");
                    return;
                }
                else if(!validate.isValidMobile(etMobile.getText())){
                    etMobile.setError("Enter Valid Mobile");
                    return;
                }else {
                    etMobile.setError(null);
                }

                if (validate.validateString(etLocality.getText().toString())) {
                    locality = "";

                    //etLocality.setError("Enter Locality");
                    // return;
                } else {
                    locality = etLocality.getText().toString();
                    //etLocality.setError(null);
                }

                Customer pats = realm.where(Customer.class).equalTo("mobile", etMobile.getText().toString()).findFirst();
                if (!update && pats != null) {
                    Toast.makeText(this, "Patient mobile already register", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if (pats == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {


                        //Doctor doctor=realm.where(Doctor.class).equalTo("username",session.getUsername()).findFirst();

                        // Patients patients = new Patients();
                        if (!update) {
                            patients = realm.createObject(Customer.class, Long.parseLong(String.valueOf(new Date().getTime())));
                        }
                        patients.setPatientId(pid);
                        patients.setFirstname(etFirstName.getText().toString());
                        patients.setLastname(lastname);
                        patients.setMobile(etMobile.getText().toString());
                        patients.setLocality(locality);
                        patients.setUsername(session.getUsername());

                        //MainActivity.doctor.getPatients().add(patients);

                        //  realm.copyToRealmOrUpdate(patients);

                        realm.copyToRealmOrUpdate(patients);

                        session.setLastUpdateTime(String.valueOf(new Date().getTime()));

                        finish();
                    }
                });

                break;


            case R.id.btnRemovePatient:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Delete patient data?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        patients.deleteFromRealm();

                                        /*List<com.ak.gynaecam.realm_model.Session> ses = realm.where(com.ak.gynaecam.realm_model.Session.class)
                                                .equalTo("patientId", patientId).findAll();
                                        ses.*/

                                    }
                                });

                                Intent i = new Intent(AddCustomerActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                                session.setLastUpdateTime(String.valueOf(new Date().getTime()));
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
        }
    }


}
