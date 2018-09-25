package com.group4.patientdoctorconsultation.data.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.group4.patientdoctorconsultation.common.LiveDocument;
import com.group4.patientdoctorconsultation.common.LiveQuery;
import com.group4.patientdoctorconsultation.common.LiveResultListener;
import com.group4.patientdoctorconsultation.data.model.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileRepository {

    private static ProfileRepository instance;
    private final CollectionReference profileCollection;
    private final FirebaseFirestore firestore;

    private ProfileRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.profileCollection = firestore.collection(Profile.COLLECTION_NAME);
    }

    public LiveDocument<Profile> profileFromUserId(String userId){
        if(userId == null || userId.isEmpty()){
            userId = "1";
        }
        return new LiveDocument<>(
                profileCollection.document(userId),
                Profile.class
        );
    }

    public LiveResultListener<Boolean> updateProfile(Profile profile){
        LiveResultListener<Boolean> liveCompleteListener = new LiveResultListener<>();
        profileCollection.document(profile.getId())
                .set(profile)
                .addOnSuccessListener(runnable -> liveCompleteListener.onSuccess(true))
                .addOnFailureListener(liveCompleteListener);
        return liveCompleteListener;
    }

    public LiveQuery<Profile> getLinkedProfiles(String profileId){

        if(profileId == null || profileId.equals("")){
            profileId = "1";
        }

        return new LiveQuery<>(
                profileCollection.whereEqualTo(Profile.FIELD_LINKED_PROFILES + "." + profileId, true),
                Profile.class
        );
    }

    public LiveQuery<Profile> getAllProfiles(){
        return new LiveQuery<>(
                profileCollection,
                Profile.class
        );
    }

    public LiveResultListener<Boolean> addLinkedProfile(String currentProfileId, String linkedProfileId){
        LiveResultListener<Boolean> liveCompleteListener = new LiveResultListener<>();
        final DocumentReference linkedProfileReference = profileCollection.document(linkedProfileId);
        final DocumentReference profileReference = profileCollection.document(currentProfileId);

        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
            Profile linkedProfile = transaction.get(linkedProfileReference).toObject(Profile.class);
            Profile profile = transaction.get(profileReference).toObject(Profile.class);

            linkProfileId(linkedProfile, currentProfileId);
            linkProfileId(profile, linkedProfileId);

            transaction.set(linkedProfileReference, Objects.requireNonNull(linkedProfile));
            transaction.set(profileReference, Objects.requireNonNull(profile));


            return null;
        })
        .addOnSuccessListener(runnable -> liveCompleteListener.onSuccess(true))
        .addOnFailureListener(liveCompleteListener);

        return liveCompleteListener;
    }

    private void linkProfileId(Profile profile, String linkedProfileId){
        Map<String, Boolean> linkedProfiles = Objects.requireNonNull(profile).getLinkedProfiles();
        if (linkedProfiles == null){
            linkedProfiles = new HashMap<>();
        }
        linkedProfiles.put(linkedProfileId, true);
        profile.setLinkedProfiles(linkedProfiles);
    }

    public static synchronized ProfileRepository getInstance(FirebaseFirestore firestore){
        if(instance == null){
            instance = new ProfileRepository(firestore);
        }

        return instance;
    }


}
