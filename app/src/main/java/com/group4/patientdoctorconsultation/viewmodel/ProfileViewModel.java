package com.group4.patientdoctorconsultation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.group4.patientdoctorconsultation.common.FailableResource;
import com.group4.patientdoctorconsultation.common.LiveResultListener;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.data.repository.ProfileRepository;

import java.util.List;

public class ProfileViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final ProfileRepository profileRepository;
    private final MutableLiveData<Boolean> isSignedIn = new MutableLiveData<>();
    private final MutableLiveData<String> profileId = new MutableLiveData<>();

    private final LiveData<FailableResource<Profile>> profile;
    private final LiveData<FailableResource<List<Profile>>> linkedProfiles;
    private final FirebaseAuth firebaseAuth;

    ProfileViewModel(ProfileRepository profileRepository, FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.profileRepository = profileRepository;

        firebaseAuth.addAuthStateListener(ProfileViewModel.this);

        profile = Transformations.switchMap(profileId, profileRepository::profileFromUserId);
        linkedProfiles = Transformations.switchMap(profileId, profileRepository::getLinkedProfiles);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        firebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        isSignedIn.setValue(firebaseAuth.getCurrentUser() != null);
        profileId.setValue((isSignedIn.getValue() != null && isSignedIn.getValue()) ? firebaseAuth.getUid() : "");
    }

    public LiveResultListener<Boolean> updateProfile(Profile profile) {
        return profileRepository.updateProfile(profile);
    }

    public LiveData<Boolean> getIsSignedIn() {
        return isSignedIn;
    }

    public LiveData<FailableResource<Profile>> getProfile() {
        return profile;
    }

    public LiveData<FailableResource<List<Profile>>> getLinkedProfiles(){
        return linkedProfiles;
    }

    public LiveData<FailableResource<List<Profile>>> getAllProfiles(){
        return profileRepository.getAllProfiles();
    }

    public LiveResultListener<Boolean> addLinkedProfile(String linkedProfileId){
        return profileRepository.addLinkedProfile(profileId.getValue(), linkedProfileId);
    }

    public void logout(){
        if(profile != null){
            firebaseAuth.signOut();
        }
    }
}
