package com.group4.patientdoctorconsultation.data.model;

import com.google.type.Date;
import com.group4.patientdoctorconsultation.common.IndexedFirestoreResource;

public class DataPacket extends IndexedFirestoreResource {

    public static final String COLLECTION_NAME = "data_packets";
    public static final String FIELD_PATIENT_ID = "patientId";
    public static final String FIELD_ITEMS = "items";

    private String doctorId;
    private String doctorName;
    private String patientId;
    private String patientName;
    private String title;
    private Date createDate;

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getDoctorId() {
        return doctorId;
    }


    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName != null ? doctorName : "No doctor";
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}