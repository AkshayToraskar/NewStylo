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

    /* @BindView(R.id.etId)
     EditText etId;*/
    @BindView(R.id.etFirstName)
    EditText etFirstName;
    /*@BindView(R.id.etLastName)
    EditText etLastName;*/
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
    long customerId = 0;


    Customer customer;
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
            customerId = getIntent().getExtras().getLong("customerId");

            customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

            if (customer != null) {
                etMobile.setText(customer.getMobile());
                etLocality.setText(customer.getLocality());
                etFirstName.setText(customer.getFullname());
            }

            update = true;

        }

        if (update) {
            getSupportActionBar().setTitle("Update Customer");
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

                final String locality;


                if (validate.validateString(etFirstName.getText().toString())) {
                    etFirstName.setError("Enter First Name");
                    return;
                } else {
                    etFirstName.setError(null);
                }

                /*if (validate.validateString(etMobile.getText().toString())) {
                    etMobile.setError("Enter Mobile No");
                    return;
                }
                else */
                if (!validate.isValidMobile(etMobile.getText()) && !validate.validateString(etMobile.getText().toString())) {
                    etMobile.setError("Enter Valid Mobile");

                    Customer pats = realm.where(Customer.class).equalTo("mobile", etMobile.getText().toString()).findFirst();
                    if (!update && pats != null) {
                        Toast.makeText(this, "Customer mobile already register", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    return;
                } else {
                    etMobile.setError(null);
                }

                if (validate.validateString(etLocality.getText().toString())) {
                    locality = "";
                } else {
                    locality = etLocality.getText().toString();
                }


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (!update) {
                            customer = realm.createObject(Customer.class, Long.parseLong(String.valueOf(new Date().getTime())));
                        }

                        customer.setFullname(etFirstName.getText().toString());
                        customer.setMobile(etMobile.getText().toString());
                        customer.setLocality(locality);
                        realm.copyToRealmOrUpdate(customer);

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
                                        customer.deleteFromRealm();

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
