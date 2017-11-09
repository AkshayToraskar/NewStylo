package com.ak.newstylo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ak.newstylo.R;

import java.io.File;
import java.util.List;

/**
 * Created by dg hdghfd on 27-03-2017.
 *
 * display file list
 *
 */

public class FilePickerAdapter extends ArrayAdapter<File> {

    private List<File> mObjects;

    public FilePickerAdapter(Context context, List<File> objects) {

        super(context, R.layout.list_item, android.R.id.text1, objects);
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.list_item, parent, false);
        } else
            row = convertView;

        File object = mObjects.get(position);

        ImageView imageView = (ImageView) row.findViewById(R.id.file_picker_image);
        TextView textView = (TextView) row.findViewById(R.id.file_picker_text);
        textView.setSingleLine(true);
        textView.setText(object.getName());

        if (object.isFile())
            imageView.setImageResource(R.drawable.spreadsheet);

        else
            imageView.setImageResource(R.drawable.folders);

        return row;
    }
}