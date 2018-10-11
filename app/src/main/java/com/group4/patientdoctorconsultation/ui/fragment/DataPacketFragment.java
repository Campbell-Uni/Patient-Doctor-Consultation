package com.group4.patientdoctorconsultation.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.FirestoreFragment;
import com.group4.patientdoctorconsultation.common.LiveResultListener;
import com.group4.patientdoctorconsultation.common.PacketItemDialog;
import com.group4.patientdoctorconsultation.common.SwipeDeleteAction;
import com.group4.patientdoctorconsultation.data.adapter.PacketItemAdapter;
import com.group4.patientdoctorconsultation.data.model.DataPacket;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.databinding.FragmentDataPacketBinding;
import com.group4.patientdoctorconsultation.ui.NavigationActivity;
import com.group4.patientdoctorconsultation.ui.dialogfragment.AttachmentDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.CommentDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.HeartRateDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.LocationDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.ProfileDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.TextDialogFragment;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.DataPacketViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DataPacketFragment extends FirestoreFragment implements View.OnClickListener {

    private static final int RC_ADD_PACKET_ITEM = 1;
    private static final int RC_UPDATE_PACKET_ITEM = 2;
    private static final int RC_SET_PACKET_DOCTOR = 3;
    private static final String TAG = DataPacketFragment.class.getSimpleName();

    private PacketItemAdapter packetItemAdapter;
    private DataPacketViewModel viewModel;
    private FragmentDataPacketBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_packet, container, false);
        viewModel = DependencyInjector.provideDataPacketViewModel(requireActivity());

        initialisePacketItemList(binding.packetItemList);

        viewModel.getActivePacket().observe(this, activePacket -> {
            if (activePacket != null && handleFirestoreResult(activePacket)) {
                binding.setDataPacket(activePacket.getResource());
            }
        });

        viewModel.getActivePacketItems().observe(this, packetItems -> {
            if(packetItems != null && handleFirestoreResult(packetItems)){
                updatePacketBinding(packetItems.getResource());
            }
        });

        binding.newAttachment.setOnClickListener(this);
        binding.newComment.setOnClickListener(this);
        binding.newHeartRate.setOnClickListener(this);
        binding.newLocation.setOnClickListener(this);
        binding.doctorIcon.setOnClickListener(this);

        return binding.getRoot();
    }


    @Override
    public void onClick(View view) {
        PacketItemDialog itemDialog;
        int requestCode = RC_ADD_PACKET_ITEM;

        switch (view.getId()){
            case R.id.new_attachment:
                itemDialog = new AttachmentDialogFragment();
                break;
            case R.id.new_comment:
                itemDialog = new TextDialogFragment();
                break;
            case R.id.new_heart_rate:
                itemDialog = new HeartRateDialogFragment();
                break;
            case R.id.new_location:
                itemDialog = new LocationDialogFragment();
                break;
            case R.id.doctor_icon:
                itemDialog = ProfileDialogFragment.newInstance(ProfileDialogFragment.EXTRA_PROFILE_LIST_TYPE_DOCTORS);
                requestCode = RC_SET_PACKET_DOCTOR;
                break;
            default:
                itemDialog = new TextDialogFragment();
                break;
        }

        openPacketItemDialog(itemDialog, requestCode);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Integer> requestCodes = Arrays.asList(RC_ADD_PACKET_ITEM, RC_UPDATE_PACKET_ITEM , RC_SET_PACKET_DOCTOR);
        if(resultCode != Activity.RESULT_OK || !requestCodes.contains(requestCode)) {
            return;
        }

        try{
            LiveResultListener<?> listener = null;
            DataPacket dataPacket = binding.getDataPacket();
            DataPacketItem result = Objects.requireNonNull(
                (DataPacketItem) data.getSerializableExtra(PacketItemDialog.EXTRA_RESULT)
            );

            switch (requestCode) {
                case RC_ADD_PACKET_ITEM:
                    listener = viewModel.addDataPacketItem(dataPacket, result);
                    break;
                case RC_UPDATE_PACKET_ITEM:
                    listener = viewModel.updatePacketItem(dataPacket, result);
                    break;
                case RC_SET_PACKET_DOCTOR:
                    dataPacket.setDoctorId(result.getValue());
                    dataPacket.setDoctorName(result.getDisplayValue());
                    listener = viewModel.updateDataPacket(dataPacket);
                    break;
            }

            listener.observe(this, actionResult -> {
                if (actionResult != null && handleFirestoreResult(actionResult)) {
                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }

    }

    private void initialisePacketItemList(RecyclerView packetItemList) {
        packetItemAdapter = new PacketItemAdapter(packetItem -> {
            if(((NavigationActivity) requireActivity()).getProfileType() == Profile.ProfileType.DOCTOR){
                openPacketItemDialog(CommentDialogFragment.newInstance(packetItem), RC_UPDATE_PACKET_ITEM);
            }
        });
        packetItemList.setLayoutManager(new LinearLayoutManager(requireContext()));
        packetItemList.setAdapter(packetItemAdapter);

        SwipeDeleteAction swipeDeleteAction = new SwipeDeleteAction(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                DataPacketItem packetItem = packetItemAdapter.getItem(viewHolder.getAdapterPosition());
                packetItemAdapter.removeAt(viewHolder.getAdapterPosition());
                viewModel.deleteDataPacketItem(binding.getDataPacket(), packetItem).observe( DataPacketFragment.this, result -> {
                    if(result != null && handleFirestoreResult(result) && result.getResource()){
                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeDeleteAction);
        itemTouchHelper.attachToRecyclerView(packetItemList);
    }

    private void updatePacketBinding(List<DataPacketItem> newPacketItems) {
        packetItemAdapter.replaceListItems(newPacketItems);
        updateAttachmentDisplayText(newPacketItems);
    }

    private void updateAttachmentDisplayText(List<DataPacketItem> packetItems) {
        if(packetItems == null || packetItems.isEmpty()){
            return;
        }

        for(DataPacketItem packetItem : packetItems){
            if(packetItem.getDataPacketItemType() != DataPacketItem.DataPacketItemType.DOCUMENT_REFERENCE){
                continue;
            }

            viewModel.getFileMetaData(packetItem.getValue()).observe(this, storageMetadata -> {
                if(storageMetadata != null && handleFirestoreResult(storageMetadata)){
                    List<DataPacketItem> newItems = packetItemAdapter.getListItems();

                    for(DataPacketItem dataPacketItem : newItems){
                        if(dataPacketItem.getValue().equals(packetItem.getValue())){
                            dataPacketItem.setDisplayValue(storageMetadata.getResource().getName());
                        }
                    }

                    packetItemAdapter.replaceListItems(newItems);
                }
            });
        }
    }

    @SuppressLint("CommitTransaction")
    private void openPacketItemDialog(PacketItemDialog packetItemDialog, int requestCode){
        packetItemDialog.setTargetFragment(this, requestCode);
        packetItemDialog.show(Objects.requireNonNull(getFragmentManager()).beginTransaction(), TAG);
    }
}
