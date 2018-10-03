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

    //doctor profile
   private String doctorname;
   private  String specialty;
   private String location;
   private  String email;
   private  String description;

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
        return profileType.toString();
    }

    public void setProfileTypeString(String profileType){
        this.profileType = ProfileType.valueOf(profileType);
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

    public String getDoctorname() {
        return doctorname;
    }

    public void setDoctorname(String doctorname) {
        this.doctorname = doctorname;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getLinkedProfiles() {
        return linkedProfiles;
    }

    public void setLinkedProfiles(Map<String, Boolean> linkedProfiles) {
        this.linkedProfiles = linkedProfiles;
    }
}
