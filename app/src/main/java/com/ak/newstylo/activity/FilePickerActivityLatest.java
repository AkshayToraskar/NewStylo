package com.ak.newstylo.activity;

/**
 * pick file from the device
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.ak.newstylo.R;
import com.ak.newstylo.adapter.FilePickerAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilePickerActivityLatest extends AppCompatActivity {

    @BindView(R.id.ll_empty_directory)
    LinearLayout llEmptyDirectory;

    @BindView(R.id.lst_files)
    ListView lvFiles;

    public final static String EXTRA_FILE_PATH = "file_path";
    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/sdcard/";

    protected File Directory;
    protected ArrayList<File> Files;
    protected FilePickerAdapter Adapter;
    protected boolean ShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker_latest);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Import .csv file");

        // Set initial directory
        Directory = new File(DEFAULT_INITIAL_DIRECTORY);

        // Initialize the ArrayList
        Files = new ArrayList<File>();

        // Set the ListAdapter
        Adapter = new FilePickerAdapter(this, Files);
        lvFiles.setAdapter(Adapter);

        // Initialize the extensions array to allow any file extensions
        acceptedFileExtensions = new String[]{"csv"};

        // Get intent extras
        if (getIntent().hasExtra(EXTRA_FILE_PATH))
            Directory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));

        if (getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES))
            ShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);

        if (getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {

            ArrayList<String> collection =
                    getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);

            acceptedFileExtensions = (String[])
                    collection.toArray(new String[collection.size()]);
        }


        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File newFile = Files.get(i);

                if (newFile.isFile()) {

                    Intent extra = new Intent();
                    extra.putExtra(EXTRA_FILE_PATH, newFile.getAbsolutePath());
                    setResult(RESULT_OK, extra);
                    finish();
                } else {

                    Directory = newFile;
                    refreshFilesList();
                }
            }
        });

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

    @Override
    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    protected void refreshFilesList() {

        Files.clear();
        ExtensionFilenameFilter filter =
                new ExtensionFilenameFilter(acceptedFileExtensions);

        File[] files = Directory.listFiles(filter);

        if (files != null && files.length > 0) {

            for (File f : files) {

                if (f.isHidden() && !ShowHiddenFiles) {

                    continue;
                }

                Files.add(f);
            }

            Collections.sort(Files, new FileComparator());
        }

        Adapter.notifyDataSetChanged();

        if (Files.size() == 0) {
            llEmptyDirectory.setVisibility(View.VISIBLE);
        } else {
            llEmptyDirectory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        if (Directory.getParentFile() != null) {

            Directory = Directory.getParentFile();
            refreshFilesList();
            return;
        }

        super.onBackPressed();
    }

    private class FileComparator implements Comparator<File> {

        public int compare(File f1, File f2) {

            if (f1 == f2)
                return 0;

            if (f1.isDirectory() && f2.isFile())
                // Show directories above files
                return -1;

            if (f1.isFile() && f2.isDirectory())
                // Show files below directories
                return 1;

            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {

        private String[] Extensions;

        public ExtensionFilenameFilter(String[] extensions) {

            super();
            Extensions = extensions;
        }

        public boolean accept(File dir, String filename) {

            if (new File(dir, filename).isDirectory()) {

                // Accept all directory names
                return true;
            }

            if (Extensions != null && Extensions.length > 0) {

                for (int i = 0; i < Extensions.length; i++) {

                    if (filename.endsWith(Extensions[i])) {

                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }
}
