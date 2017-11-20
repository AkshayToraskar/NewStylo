package com.ak.newstylo.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ak.newstylo.R;
import com.ak.newstylo.model.Customer;
import com.ak.newstylo.model.Session;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 2000;
    Realm realm;
    Animation animBounce;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
        ivLogo.startAnimation(animBounce);


        //manageOldData();
        callhandler();

    }


    //check and delete old data
    public void manageOldData() {

        List<Session> dataCollectionList = realm.where(Session.class).findAll();
        //Log.v("no of days", " " + sessionManager.getInDays());
        for (final Session dataCollection : dataCollectionList) {
            if (isOldData(dataCollection.getDate())) {
                Log.v("Data is :", " Old");
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        dataCollection.deleteFromRealm();
                    }
                });
            }
        }


        callhandler();

    }

    public void callhandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);


                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public boolean isOldData(String timeStamp) {

        boolean isOld = false;
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Date dt1 = null;
        try {
            dt1 = df.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date dt2 = new Date();
        long diff = dt2.getTime() - dt1.getTime();
        //long diffSeconds = diff / 1000 % 60;
        //long diffMinutes = diff / (60 * 1000) % 60;
        //long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) (diff / (1000 * 60 * 60 * 24));

        // Log.v("no of days", " " + sessionManager.getInDays());
        if (diffInDays > 4) {
            isOld = true;
        }
        return isOld;
    }

}
