package com.group4.patientdoctorconsultation.common;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public abstract class IndexedFirestoreResource implements Serializable {

    private String id;

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getId() {
        return id;
    }
}
