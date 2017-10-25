package com.ak.newstylo.app;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class Validate {

    public boolean validateString(String name) {
        if (name.equals(""))
            return true;
        else
            return false;
    }

    public boolean validateRB(int id) {
        if (id == -1)
            return true;
        else
            return false;
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidMobile(CharSequence target) {
        if (target.length() < 10 ) {
            return false;
        } else {
            return true;
        }
    }
}
