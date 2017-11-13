package com.ak.newstylo.app;

/**
 * Created by dg hdghfd on 13-11-2017.
 */

import android.util.Log;

import com.ak.newstylo.model.Customer;
import com.ak.newstylo.model.ImageData;
import com.ak.newstylo.model.Session;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


/**
 * Created by dg hdghfd on 25-07-2017.
 * <p>
 * scan the survey and import and export the data into csv format
 */

public class CsvOperation {

    Realm realm;
    List<String> collectedData = new ArrayList<>();
    List<String[]> strData = new ArrayList<>();

    List<Customer> customerList = new ArrayList<>();
    List<Session> sessionList = new ArrayList<>();
    List<ImageData> imageDataList = new ArrayList<>();

    //for generating ans
    public CsvOperation() {
        realm = Realm.getDefaultInstance();
    }

    public List<String[]> generateExportedList() {


        List<String> st = scanHeader();
        String[] head = st.toArray(new String[st.size()]);
        strData.add(head);

        customerList.clear();
        customerList.addAll(realm.where(Customer.class).findAll());

        for (Customer customer : customerList) {
            boolean hvSessData = false, hvImgData = false;

            sessionList.clear();
            sessionList.addAll(realm.where(Session.class).equalTo("isExported", false).equalTo("customerId", customer.getId()).findAll());

            for (Session session : sessionList) {
                imageDataList.clear();
                imageDataList.addAll(realm.where(ImageData.class).equalTo("isExported", false).equalTo("sessionId", session.getId()).findAll());
                hvSessData = true;

                for (ImageData imgData : imageDataList) {
                    hvImgData = true;
                    collectedData.clear();
                    collectedData.addAll(addCustomerData(customer));//customer data
                    collectedData.addAll(addSessionData(session));//session data
                    collectedData.addAll(addImageData(imgData));//image data
                    String arr[] = collectedData.toArray(new String[collectedData.size()]); //.split(",");
                    strData.add(arr);

                }

                if (!hvImgData) {
                    collectedData.clear();
                    collectedData.addAll(addCustomerData(customer));
                    collectedData.addAll(addSessionData(session));
                    collectedData.addAll(addImageData(null));
                    String arr[] = collectedData.toArray(new String[collectedData.size()]); //.split(",");
                    strData.add(arr);
                }


            }

            if (!hvImgData && !hvSessData) {
                collectedData.clear();
                collectedData.addAll(addCustomerData(customer));
                collectedData.addAll(addSessionData(null));
                collectedData.addAll(addImageData(null));
                String arr[] = collectedData.toArray(new String[collectedData.size()]); //.split(",");
                strData.add(arr);
            }


        }

        return strData;
    }


    public List<String> addCustomerData(Customer customer) {
        List<String> s = new ArrayList<>();
        if (customer != null) {
            s.add(String.valueOf(customer.getId()));
            s.add(customer.getFullname());
            s.add(customer.getLocality());
            s.add(customer.getMobile());
        }
        return s;
    }

    public List<String> addSessionData(Session session) {
        List<String> s = new ArrayList<>();
        if (session != null) {
            s.add(String.valueOf(session.getId()));
            s.add(session.getDate());
            s.add(session.getBillNo());
            s.add(session.getNote());
            s.add(String.valueOf(session.getCustomerId()));
        }
        return s;
    }

    public List<String> addImageData(ImageData imgData) {
        List<String> s = new ArrayList<>();
        if (imgData != null) {
            s.add(String.valueOf(imgData.getId()));
            s.add(imgData.getDate());
            s.add(imgData.getFilename());
            s.add(imgData.getPath());
            s.add(String.valueOf(imgData.getSessionId()));
        }
        return s;
    }


    public List<String> scanHeader() {

        List<String> strH = new ArrayList<>();
        strH.add("customerid");
        strH.add("fullname");
        strH.add("locality");
        strH.add("mobile");
        strH.add("sessionid");
        strH.add("date");
        strH.add("billno");
        strH.add("note");
        strH.add("customerid");
        strH.add("imageid");
        strH.add("date");
        strH.add("filename");
        strH.add("path");
        strH.add("sessionid");
        return strH;
    }


}
