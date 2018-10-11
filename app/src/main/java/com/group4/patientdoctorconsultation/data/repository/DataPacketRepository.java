package com.group4.patientdoctorconsultation.data.repository;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.patientdoctorconsultation.common.LiveDocument;
import com.group4.patientdoctorconsultation.common.LiveQuery;
import com.group4.patientdoctorconsultation.common.LiveResultListener;
import com.group4.patientdoctorconsultation.data.model.DataPacket;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;

import java.io.InputStream;
import java.util.Objects;

public class DataPacketRepository {

    private static DataPacketRepository instance;
    private final CollectionReference dataPacketCollection;
    private final FirebaseStorage firebaseStorage;

    private DataPacketRepository(FirebaseFirestore firestore, FirebaseStorage firebaseStorage) {
        dataPacketCollection = firestore.collection(DataPacket.COLLECTION_NAME);
        this.firebaseStorage = firebaseStorage;
    }

    public LiveQuery<DataPacket> getDataPacketsByPatientId(String patientId) {
        if(patientId == null || patientId.isEmpty()){
            patientId = "0";
        }
        return new LiveQuery<>(
                dataPacketCollection.whereEqualTo(DataPacket.FIELD_LINKED_PROFILES + "." + patientId , true),
                DataPacket.class);
    }

    public LiveDocument<DataPacket> getDataPacketById(String patientId) {
        return new LiveDocument<>(
                dataPacketCollection.document(patientId),
                DataPacket.class
        );
    }

    public LiveResultListener<DocumentReference> addDataPacket(DataPacket dataPacket) {
        LiveResultListener<DocumentReference> liveAdditionListener = new LiveResultListener<>();
        dataPacketCollection
                .add(dataPacket)
                .addOnSuccessListener(liveAdditionListener)
                .addOnFailureListener(liveAdditionListener);

        return liveAdditionListener;
    }

    public LiveResultListener<Boolean> updateDataPacket(DataPacket dataPacket) {
        LiveResultListener<Boolean> liveCompleteListener = new LiveResultListener<>();
        dataPacketCollection
                .document(dataPacket.getId())
                .set(dataPacket)
                .addOnSuccessListener(runnable -> liveCompleteListener.onSuccess(true))
                .addOnFailureListener(liveCompleteListener);

        return liveCompleteListener;
    }

    public LiveQuery<DataPacketItem> getDataPacketItemsByPacketId(String packetId) {
        return new LiveQuery<>(
                dataPacketCollection
                        .document(packetId)
                        .collection(DataPacket.FIELD_ITEMS),
                DataPacketItem.class
        );
    }

    public LiveResultListener<Boolean> updateDataPacketItem(DataPacket dataPacket, DataPacketItem dataPacketItem) {
        LiveResultListener<Boolean> liveCompleteListener = new LiveResultListener<>();
        dataPacketCollection
                .document(dataPacket.getId())
                .collection(DataPacket.FIELD_ITEMS)
                .document(dataPacketItem.getId())
                .set(dataPacketItem)
                .addOnSuccessListener(runnable -> liveCompleteListener.onSuccess(true))
                .addOnFailureListener(liveCompleteListener);

        return liveCompleteListener;
    }

    public LiveResultListener<Boolean> deleteDataPacketItem(DataPacket dataPacket, DataPacketItem dataPacketItem) {
        LiveResultListener<Boolean> liveDeletionListener = new LiveResultListener<>();
        dataPacketCollection
                .document(dataPacket.getId())
                .collection(DataPacket.FIELD_ITEMS)
                .document(dataPacketItem.getId())
                .delete()
                .addOnSuccessListener(runnable -> liveDeletionListener.onSuccess(true))
                .addOnFailureListener(liveDeletionListener);

        return liveDeletionListener;
    }

    public LiveResultListener<DocumentReference> addDataPacketItem(DataPacket dataPacket, DataPacketItem dataPacketItem) {
        LiveResultListener<DocumentReference> liveAdditionListener = new LiveResultListener<>();
        dataPacketCollection
                .document(dataPacket.getId())
                .collection(DataPacket.FIELD_ITEMS)
                .add(dataPacketItem)
                .addOnSuccessListener(liveAdditionListener)
                .addOnFailureListener(liveAdditionListener);

        return liveAdditionListener;
    }

    public LiveResultListener<Uri> uploadAttachment(String packetId, String fileName, InputStream inputStream) {
        StorageReference fileReference = firebaseStorage.getReference().child(packetId + "/" + fileName);
        UploadTask uploadTask = fileReference.putStream(inputStream);
        LiveResultListener<Uri> uploadListener = new LiveResultListener<>();

        uploadTask
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                })
                .addOnSuccessListener(uploadListener)
                .addOnFailureListener(uploadListener);

        return uploadListener;
    }

    public LiveResultListener<StorageMetadata> getFileMetadata(String fileReferenceUrl) {
        StorageReference fileReference = firebaseStorage.getReferenceFromUrl(fileReferenceUrl);
        LiveResultListener<StorageMetadata> metaDataListener = new LiveResultListener<>();

        fileReference.getMetadata()
                .addOnSuccessListener(metaDataListener)
                .addOnFailureListener(metaDataListener);

        return metaDataListener;
    }

    //Singleton instantiation - probably should be thread safe
    public static synchronized DataPacketRepository getInstance(FirebaseFirestore firestore, FirebaseStorage firebaseStorage) {
        if (instance == null) {
            instance = new DataPacketRepository(firestore, firebaseStorage);
        }

        return instance;
    }


}
