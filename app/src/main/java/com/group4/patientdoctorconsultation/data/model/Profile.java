package com.group4.patientdoctorconsultation.data.model;

import com.google.firebase.firestore.Exclude;
import com.group4.patientdoctorconsultation.common.IndexedFirestoreResource;

import java.util.Date;
import java.util.Map;

public class Profile extends IndexedFirestoreResource {

    public enum ProfileType{DOCTOR, PATIENT}

    public static final String COLLECTION_NAME = "profiles";

    public static final String FIELD_LINKED_PROFILES = "linkedProfiles";

    private ProfileType profileType;
    private String userName;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String heightInCentimetres;
    private String weightInKg;
    private String medicalConditions;
    private Map<String, Boolean> linkedProfiles;

    /*
     * Exclude getters and setters from firebase as it can't handle Enums
     */

    @Exclude
    public ProfileType getProfileType() {
        return profileType;
    }

    @Exclude
    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public String getProfileTypeString(){
        return profileType != null ? profileType.toString() : "";
    }

    public void setProfileTypeString(String profileType){
        try{
            this.profileType = ProfileType.valueOf(profileType);
        }catch (Exception e){
            this.profileType = ProfileType.PATIENT;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHeightInCentimetres() {
        return heightInCentimetres;
    }

    public void setHeightInCentimetres(String heightInCentimetres) {
        this.heightInCentimetres = heightInCentimetres;
    }

    public String getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(String weightInKg) {
        this.weightInKg = weightInKg;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public Map<String, Boolean> getLinkedProfiles() {
        return linkedProfiles;
    }

    public void setLinkedProfiles(Map<String, Boolean> linkedProfiles) {
        this.linkedProfiles = linkedProfiles;
    }
}
