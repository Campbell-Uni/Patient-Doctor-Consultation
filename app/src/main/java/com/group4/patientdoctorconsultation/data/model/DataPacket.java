package com.group4.patientdoctorconsultation.data.model;

import com.google.type.Date;
import com.group4.patientdoctorconsultation.common.IndexedFirestoreResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPacket extends IndexedFirestoreResource {

    public static final String COLLECTION_NAME = "data_packets";
    public static final String FIELD_ITEMS = "items";
    public static final String FIELD_LINKED_PROFILES = "linkedProfiles";

    private String doctorName;
    private String patientName;
    private String title;
    private Date createDate;
    private Map<String, Boolean> linkedProfiles = new HashMap<>();
    private List<String> locations = new ArrayList<>();

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getDoctorName() {
        return doctorName != null ? doctorName : "No doctor";
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
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

    public Map<String, Boolean> getLinkedProfiles() {
        return linkedProfiles;
    }

    public void setLinkedProfiles(Map<String, Boolean> linkedProfiles) {
        this.linkedProfiles = linkedProfiles;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}