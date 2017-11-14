package com.ak.newstylo.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;


import com.ak.newstylo.R;
import com.ak.newstylo.adapter.MyViewPagerAdapter;
import com.ak.newstylo.model.ImageData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<ImageData> images;

    private MyViewPagerAdapter myViewPagerAdapter;

    @BindView(R.id.lbl_count)
    TextView lblCount;//, lblTitle, lblDate;
    @BindView(R.id.btnDelete)
    ImageButton btnDelete;
    /*@BindView(R.id.btnEdit)
    ImageButton btnEdit;
    @BindView(R.id.cbBookmark)
    CheckBoxWithoutText cbBookmark;*/
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private int selectedPosition = 0;
    Realm realm;


    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);

        ButterKnife.bind(this, v);


        realm = Realm.getDefaultInstance();

        images = NewSessionActivity.imageData; //(ArrayList<ImageData>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");


        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter(getActivity(), images);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);


       /* cbBookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int pos = viewPager.getCurrentItem();
                        ImageData image = realm.where(ImageData.class).equalTo("id", images.get(pos).getId()).findFirst();
                        image.setBookmark(isChecked);
                        realm.copyToRealmOrUpdate(image);
                    }
                });


            }
        });*/


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pos = viewPager.getCurrentItem();

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete")
                        .setMessage("Would you like to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        File file = new File(images.get(pos).getPath());
                                        boolean delete = file.delete();

                                        Log.v("DELETE?", "" + delete);

                                        ImageData image = realm.where(ImageData.class).equalTo("id", images.get(pos).getId()).findFirst();
                                        image.deleteFromRealm();
                                        images.remove(pos);

                                        NewSessionActivity.previewData.updateData();
                                        myViewPagerAdapter.notifyDataSetChanged();


                                        displayMetaInfo(viewPager.getCurrentItem());


                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }

        });

       /* btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pos = viewPager.getCurrentItem();
               *//* NewSessionActivity.editImageId = images.get(pos).getId();
                byte bytes[] = images.get(pos).getByteArrayImage();
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                new SavePhotoTask().execute(bmp);*//*


                //ImageView iv = (ImageView) findViewById(R.id.iv);
                //iv.setImageBitmap(bitmap);


                NewSessionActivity.editImageId = images.get(viewPager.getCurrentItem()).getId();

                *//*Uri uri = Uri.fromFile(new File(images.get(viewPager.getCurrentItem()).getPath()));

                String[] tools = new String[]{"CROP", "STICKERS", "DRAW"};
                Intent newIntent = new Intent(getActivity(), FeatherActivity.class);
                newIntent.setData(uri);
                newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, tools);

                newIntent.putExtra(Constants.EXTRA_IN_HIRES_MEGAPIXELS, MegaPixels.Mp5.ordinal());
                newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, "c5baf4d6-ddbe-435d-a40c-04bef9d7ed29");

                getActivity().startActivityForResult(newIntent, NewSessionActivity.EDITIMAGE);

                Log.v("Edit image code"," "+NewSessionActivity.EDITIMAGE);*//*


                Intent i = new Intent(getActivity(), ImageEditActivity.class);
                i.putExtra("img_path", images.get(pos).getPath());
                i.putExtra("file_name", images.get(pos).getFilename());
                getActivity().startActivityForResult(i, NewSessionActivity.EDITIMAGE);


            }
        });*/

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        if (images.size() > 0) {
            ImageData image = images.get(position);
        }
        /*//IMAGE
        if (image.getMediaType() == 1) {
            btnEdit.setVisibility(View.VISIBLE);
            cbBookmark.setVisibility(View.VISIBLE);

            if (image.getBookmark()) {
                cbBookmark.setChecked(true);
            } else {
                cbBookmark.setChecked(false);
            }
        }
        // VIDEO
        else if (image.getMediaType() == 2) {
            btnEdit.setVisibility(View.INVISIBLE);
            cbBookmark.setVisibility(View.INVISIBLE);

        }*/


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    class SavePhotoTask extends AsyncTask<Bitmap, String, String> {

        Uri uri;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uri = Uri.parse(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GYNAECAM" + File.separator + "PatientData" + ".png").toString());


            pd = new ProgressDialog(getActivity());
            pd.setMessage("Preaparing Image");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

/*            String[] tools = new String[] { "CROP", "ENHANCE", "EFFECTS",
                    "BORDERS", "STICKERS", "TILT_SHIFT", "ADJUST",
                    "BRIGHTNESS", "CONTRAST", "SATURATION", "COLORTEMP",
                    "SHARPNESS", "COLOR_SPLASH", "DRAWING", "TEXT",
                    "RED_EYE", "WHITEN", "BLEMISH", "MEME", };*/

            String[] tools = new String[]{"CROP", "STICKERS", "DRAW"};


          /*  Intent newIntent = new Intent(getActivity(), FeatherActivity.class);
            newIntent.setData(uri);
            newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, tools);

            newIntent.putExtra(Constants.EXTRA_IN_HIRES_MEGAPIXELS, MegaPixels.Mp5.ordinal());
            newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, "c5baf4d6-ddbe-435d-a40c-04bef9d7ed29");
            pd.cancel();
            getActivity().startActivityForResult(newIntent, NewSessionActivity.EDITIMAGE);*/
        }

        @Override
        protected String doInBackground(Bitmap... inte) {

            //int pos=inte[0];

            Bitmap bitmap = inte[0];

            File myDirectory = new File(Environment.getExternalStorageDirectory(), "GYNAECAM");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            File photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GYNAECAM" + File.separator + "PatientData" + ".png");
            if (photo.exists()) {
                photo.delete();
            }

            try {

              /*  int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                int size = bitmap.getRowBytes() * bitmap.getHeight();
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                bitmap.copyPixelsToBuffer(byteBuffer);
                byte byteArray[] = byteBuffer.array();*/


              /*  Bitmap bmp = Bitmap.createBitmap(sessionList.get(pos).getWidth(), sessionList.get(pos).getHeight(), Bitmap.Config.ARGB_8888);
                ByteBuffer buffer = ByteBuffer.wrap(sessionList.get(pos).getByteArrayImage());
                bmp.copyPixelsFromBuffer(buffer);*/


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);


                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(bytes.toByteArray());

                //  fos.write(byteArray);
                fos.close();


            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            return (null);
        }
    }

}
