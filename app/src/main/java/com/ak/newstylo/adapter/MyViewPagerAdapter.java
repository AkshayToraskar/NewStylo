package com.ak.newstylo.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;


import com.ak.newstylo.R;
import com.ak.newstylo.model.ImageData;
import com.ak.newstylo.views.TouchImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 02-06-2017.
 */

public class MyViewPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    Activity activity;
    private ArrayList<ImageData> images;

    public MyViewPagerAdapter(Activity activity, ArrayList<ImageData> images) {
        this.activity = activity;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

        final TouchImageView imageViewPreview = (TouchImageView) view.findViewById(R.id.image_preview);


        final ImageData image = images.get(position);


        if (image.getMediaType() == 1) {

            Glide.with(activity)//.load(image.getByteArrayImage())
                    .load(Uri.fromFile(new File(image.getPath())))
                    //.thumbnail(0.5f)
                    //.crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewPreview);

            //rlVideo.setVisibility(GONE);

            //btnEdit.setText("Edit");

        } else if (image.getMediaType() == 2) {
            //imageViewPreview.setVisibility(View.GONE);
            //videoView.setVisibility(View.VISIBLE);
            //  btnEdit.setText("Play");

            Glide.with(activity)
                    .load(Uri.fromFile(new File(image.getPath())))
                    .into(imageViewPreview);

            //videoView.setVideoPath(image.getPath());

            //btnPlayVideo.setVisibility(View.VISIBLE);
        }

        /*rlVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoPath(image.getPath());
                videoView.start();

                btnPlayVideo.setVisibility(GONE);
                imageViewPreview.setVisibility(GONE);


            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.setVisibility(GONE);
                btnPlayVideo.setVisibility(View.VISIBLE);
                imageViewPreview.setVisibility(View.VISIBLE);
            }
        });*/

        container.addView(view);

        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((View) obj);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}