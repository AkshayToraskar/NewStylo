package com.ak.newstylo.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.ak.newstylo.R;
import com.ak.newstylo.app.PreviewData;
import com.ak.newstylo.model.ImageData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


/**
 * Created by dg hdghfd on 12-04-2017.
 */

public class SessionImageAdapter extends RecyclerView.Adapter<SessionImageAdapter.MyViewHolder> {

    private List<ImageData> sessionList;
    private Activity context;
    Realm realm;
    PreviewData previewData;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivImg)
        ImageView ivImage;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    previewData.previewInfo(getPosition());
                }
            });


        }
    }


    public SessionImageAdapter(final Activity context, List<ImageData> sessionList, PreviewData previewData) {
        this.sessionList = sessionList;
        this.context = context;
        this.previewData = previewData;
        realm = Realm.getDefaultInstance();


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_image_list_row, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Log.v("path", " " + sessionList.get(position).getPath());

        if (sessionList.get(position).getPath() != null) {
            Glide.with(context)//.load(sessionList.get(position).getByteArrayImage())
                    .load(new File(sessionList.get(position).getPath()))
                    .placeholder(R.drawable.image_not_found)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.ivImage);

        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }


}