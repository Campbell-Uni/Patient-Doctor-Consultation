package com.group4.patientdoctorconsultation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageMetadata;
import com.group4.patientdoctorconsultation.common.FailableResource;
import com.group4.patientdoctorconsultation.common.LiveResultListener;
import com.group4.patientdoctorconsultation.data.model.DataPacket;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.data.repository.DataPacketRepository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class DataPacketViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final DataPacketRepository dataPacketRepository;

    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<String> profileId = new MutableLiveData<>();

    private final LiveData<FailableResource<List<DataPacket>>> dataPackets;
    private final MutableLiveData<String> activePacketId;
    private final LiveData<FailableResource<DataPacket>> activePacket;
    private final LiveData<FailableResource<List<DataPacketItem>>> activePacketItems;

    DataPacketViewModel(DataPacketRepository dataPacketRepository, FirebaseAuth firebaseAuth) {
        this.dataPacketRepository = dataPacketRepository;
        this.firebaseAuth = firebaseAuth;
        firebaseAuth.addAuthStateListener(this);

        activePacketId = new MutableLiveData<>();
        dataPackets = Transformations.switchMap(profileId, dataPacketRepository::getDataPacketsByPatientId);
        activePacket = Transformations.switchMap(activePacketId, dataPacketRepository::getDataPacketById);
        activePacketItems = Transformations.switchMap(activePacketId, dataPacketRepository::getDataPacketItemsByPacketId);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        this.profileId.setValue(
                firebaseAuth.getCurrentUser() == null ? "" : firebaseAuth.getUid()
        );
    }

    @Override
    protected void onCleared() {
        firebaseAuth.removeAuthStateListener(this);
        super.onCleared();
    }

    public LiveResultListener<Boolean> updateDataPacket(DataPacket dataPacket){
        return dataPacketRepository.updateDataPacket(dataPacket);
    }

    public LiveResultListener<Boolean> updatePacketItem(DataPacket dataPacket, DataPacketItem packetItems){
        return dataPacketRepository.updateDataPacketItem(dataPacket, packetItems);
    }

    public LiveData<FailableResource<List<DataPacket>>> getDataPackets(){
        return dataPackets;
    }

    public LiveData<FailableResource<DataPacket>> getActivePacket() {
        return activePacket;
    }

    public LiveData<FailableResource<List<DataPacketItem>>> getActivePacketItems(){
        return activePacketItems;
    }

    public LiveResultListener<DocumentReference> addDataPacket(DataPacket dataPacket){
        Map<String, Boolean> linkedProfiles = dataPacket.getLinkedProfiles();
        linkedProfiles.put(profileId.getValue(), true);
        dataPacket.setLinkedProfiles(linkedProfiles);
        return dataPacketRepository.addDataPacket(dataPacket);
    }

    public LiveResultListener<DocumentReference> addDataPacketItem(DataPacket dataPacket, DataPacketItem dataPacketItem){
        return dataPacketRepository.addDataPacketItem(dataPacket, dataPacketItem);
    }

    public LiveResultListener<Boolean> deleteDataPacketItem(DataPacket dataPacket, DataPacketItem dataPacketItem){
        return dataPacketRepository.deleteDataPacketItem(dataPacket, dataPacketItem);
    }

    public void setActivePacketId(String activePacketId){
        this.activePacketId.setValue(activePacketId);
    }

    public LiveResultListener<Uri> uploadAttachment(String fileName, InputStream inputStream){
        return dataPacketRepository.uploadAttachment(activePacketId.getValue(), fileName, inputStream);
    }

    public LiveResultListener<StorageMetadata> getFileMetaData(String fileReference){
        return dataPacketRepository.getFileMetadata(fileReference);
    }
}
