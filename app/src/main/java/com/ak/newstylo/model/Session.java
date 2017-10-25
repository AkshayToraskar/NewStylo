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
    // Customer patients;

    int age;
    String problems, date, comments;

    long patientId;
    boolean isUploaded;
    RealmList<ImageData> byteArrayImageData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /*public Customer getPatients() {
        return patients;
    }

    public void setPatients(Customer patients) {
        this.patients = patients;
    }
*/
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProblems() {
        return problems;
    }

    public void setProblems(String problems) {
        this.problems = problems;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public RealmList<ImageData> getByteArrayImageData() {
        return byteArrayImageData;
    }

    public void setByteArrayImageData(RealmList<ImageData> byteArrayImageData) {
        this.byteArrayImageData = byteArrayImageData;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}
