package com.ak.newstylo.app;

import android.net.Uri;

/**
 * Created by dg hdghfd on 24-04-2017.
 */

public interface SaveCapturedData {
    void onPictureTaken(byte[] data, Uri uri);

    void onVideoCaptured(Uri uri);
}
