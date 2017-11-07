package com.ak.newstylo.model;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 12-04-2017.
 */


public class Session extends RealmObject {
    @PrimaryKey
    long id;

    String date, note;
    long billNo;

    long customerId;
    boolean isExported;
    RealmList<ImageData> byteArrayImageData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RealmList<ImageData> getByteArrayImageData() {
        return byteArrayImageData;
    }

    public void setByteArrayImageData(RealmList<ImageData> byteArrayImageData) {
        this.byteArrayImageData = byteArrayImageData;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public boolean getExported() {
        return isExported;
    }

    public void setUploaded(boolean uploaded) {
        isExported = uploaded;
    }

    public long getBillNo() {
        return billNo;
    }

    public void setBillNo(long billNo) {
        this.billNo = billNo;
    }


}
