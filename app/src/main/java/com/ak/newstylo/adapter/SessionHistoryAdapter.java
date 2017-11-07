package com.ak.newstylo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ak.newstylo.R;
import com.ak.newstylo.activity.NewSessionActivity;
import com.ak.newstylo.app.SessionManager;
import com.ak.newstylo.model.ImageData;
import com.ak.newstylo.model.Session;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by dg hdghfd on 12-04-2017.
 */

public class SessionHistoryAdapter extends RecyclerView.Adapter<SessionHistoryAdapter.MyViewHolder> {

    private List<Session> sessionList;
    private Context context;

    Realm realm;
    SessionManager sessionManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvBillno)
        TextView tvBillno;

        @BindView(R.id.tvNote)
        TextView tvComment;
        @BindView(R.id.tvImgCount)
        TextView tvImgCount;



        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, NewSessionActivity.class);
                    //Intent i = new Intent(context, SessionActivity.class);
                    i.putExtra("sessionId", sessionList.get(getPosition()).getId());
                    i.putExtra("pos", getPosition());
                    i.putExtra("customerId", sessionList.get(getPosition()).getCustomerId());
                    context.startActivity(i);
                }
            });



        }
    }


    public SessionHistoryAdapter(Context context, List<Session> sessionList) {
        this.sessionList = sessionList;
        this.context = context;

        realm = Realm.getDefaultInstance();
        sessionManager = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        List<ImageData> imgD = realm.where(ImageData.class).equalTo("sessionId", sessionList.get(position).getId()).findAll();
        long totalData = imgD.size();




        holder.tvDate.setText("Date : " + sessionList.get(position).getDate());
        holder.tvBillno.setText("Billno : " + sessionList.get(position).getBillNo());

        holder.tvComment.setText("Note : " + sessionList.get(position).getNote());
        holder.tvImgCount.setText("Images : " + totalData);




    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

}