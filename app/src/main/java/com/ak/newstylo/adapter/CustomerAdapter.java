package com.ak.newstylo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.ak.newstylo.R;
import com.ak.newstylo.activity.CustomerHistoryActivity;
import com.ak.newstylo.activity.NewSessionActivity;
import com.ak.newstylo.model.Customer;
import com.ak.newstylo.model.Session;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by dg hdghfd on 12-04-2017.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    private List<Customer> customerList;
    private Context context;
    private Realm realm;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvMobile)
        TextView tvMobile;
        @BindView(R.id.tvLocality)
        TextView tvLocality;
        @BindView(R.id.btnStartSession)
        Button btnStartSession;
        @BindView(R.id.tv_session_no)
        TextView tvSessionNo;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, CustomerHistoryActivity.class);
                    i.putExtra("customerId", customerList.get(getPosition()).getId());
                    i.putExtra("pos", getPosition());
                    context.startActivity(i);
                }
            });

            btnStartSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, NewSessionActivity.class);
                    i.putExtra("customerId", customerList.get(getPosition()).getId());
                    context.startActivity(i);
                }
            });
        }
    }


    public CustomerAdapter(Context context, List<Customer> patientsList) {
        this.customerList = patientsList;
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvName.setText("Name : " + customerList.get(position).getFullname());
        holder.tvMobile.setText("Mobile : " + customerList.get(position).getMobile());

        String loc = (customerList.get(position).getLocality().equals(null)) || (customerList.get(position).getLocality().equals("")) ? "-" : customerList.get(position).getLocality();
        holder.tvLocality.setText("Locality : " + loc);

        long sessionSize = realm.where(Session.class).equalTo("customerId", customerList.get(position).getId()).count();
        holder.tvSessionNo.setText(String.valueOf(sessionSize));


    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

}