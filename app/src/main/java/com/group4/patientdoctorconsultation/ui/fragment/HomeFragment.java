package com.group4.patientdoctorconsultation.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.FirestoreFragment;
import com.group4.patientdoctorconsultation.data.adapter.PacketAdapter;
import com.group4.patientdoctorconsultation.data.adapter.ProfileAdapter;
import com.group4.patientdoctorconsultation.data.model.DataPacket;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.ui.NavigationActivity;
import com.group4.patientdoctorconsultation.ui.dialogfragment.NewPacketDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.ProfileDialogFragment;
import com.group4.patientdoctorconsultation.ui.dialogfragment.TextDialogFragment;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.DataPacketViewModel;
import com.group4.patientdoctorconsultation.viewmodel.ProfileViewModel;

import java.util.Objects;

import androidx.navigation.Navigation;

public class HomeFragment extends FirestoreFragment
        implements View.OnClickListener {

    private static final int RC_TITLE = 1;
    private static final int RC_PROFILE = 2;
    private static final String TAG = HomeFragment.class.getSimpleName();

    private DataPacketViewModel dataPacketViewModel;
    private Profile selectedDoctor;
    private ProfileAdapter profileAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dataPacketViewModel = DependencyInjector.provideDataPacketViewModel(requireActivity());
        ProfileViewModel profileViewModel = DependencyInjector.provideProfileViewModel(requireActivity());

        profileAdapter = new ProfileAdapter(item -> {
            selectedDoctor = item;
            createDataPacket();
        });
        RecyclerView profileList = view.findViewById(R.id.profile_list);
        profileList.setLayoutManager(new LinearLayoutManager(requireContext()));
        profileList.setAdapter(profileAdapter);
        profileViewModel.getLinkedProfiles().observe(this, profiles -> {
            if (profiles != null && handleFirestoreResult(profiles)) {
                profileAdapter.replaceListItems(profiles.getResource());
            }
        });

        PacketAdapter packetAdapter = new PacketAdapter(packet -> openDataPacket(view, packet.getId()));
        RecyclerView packetList = view.findViewById(R.id.data_packet_list);
        packetList.setLayoutManager(new LinearLayoutManager(requireContext()));
        packetList.setAdapter(packetAdapter);

        dataPacketViewModel.getDataPackets().observe(this, dataPackets -> {
            if (dataPackets != null && handleFirestoreResult(dataPackets)) {
                packetAdapter.replaceListItems(dataPackets.getResource());
            }
        });

        view.findViewById(R.id.new_packet_card).setOnClickListener(this);

        if (((NavigationActivity) requireActivity()).getProfileType() == Profile.ProfileType.DOCTOR) {
            TextView title = view.findViewById(R.id.linked_profile_title);
            CardView newPatientCard = view.findViewById(R.id.new_patient_card);
            title.setText(R.string.home_card_title_patients);
            newPatientCard.setVisibility(View.VISIBLE);
            newPatientCard.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.new_packet_card) {
            createDataPacket();
        } else if (view.getId() == R.id.new_patient_card) {
            assignLinkedProfile();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        try {
            DataPacketItem result = Objects.requireNonNull(
                    (DataPacketItem) data.getSerializableExtra(TextDialogFragment.EXTRA_RESULT)
            );

            if (requestCode == RC_TITLE) {
                DataPacket dataPacket = new DataPacket();
                dataPacket.setTitle(result.getValue());
                if (selectedDoctor != null) {
                    dataPacket.setDoctorId(selectedDoctor.getId());
                    dataPacket.setDoctorName(selectedDoctor.getUserName());
                }

                dataPacketViewModel
                        .addDataPacket(dataPacket)
                        .observe(HomeFragment.this, additionResult -> {
                            if (additionResult != null && handleFirestoreResult(additionResult)) {
                                openDataPacket(getView(), additionResult.getResource().getId());
                            }
                        });
            } else if (requestCode == RC_PROFILE){
                DependencyInjector
                        .provideProfileViewModel(requireActivity())
                        .addLinkedProfile(result.getValue())
                        .observe(HomeFragment.this, updateResult -> {
                            if(updateResult != null && handleFirestoreResult(updateResult)){
                                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_LONG).show();
                            }
                        });
            }

        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }

        profileAdapter.notifyDataSetChanged();
    }

    private void openDataPacket(View view, String packetId) {
        dataPacketViewModel.setActivePacketId(packetId);
        Navigation.findNavController(view).navigate(R.id.action_home_to_data_packet);
    }

    @SuppressLint("CommitTransaction")
    private void createDataPacket() {
        NewPacketDialogFragment newPacketDialogFragment = new NewPacketDialogFragment();
        newPacketDialogFragment.setTargetFragment(this, RC_TITLE);
        newPacketDialogFragment.show(Objects.requireNonNull(getFragmentManager()).beginTransaction(), TAG);
    }

    @SuppressLint("CommitTransaction")
    private void assignLinkedProfile() {
        ProfileDialogFragment profileDialogFragment = ProfileDialogFragment.newInstance(ProfileDialogFragment.EXTRA_PROFILE_LIST_TYPE_FULL);
        profileDialogFragment.setTargetFragment(this, RC_PROFILE);
        profileDialogFragment.show(Objects.requireNonNull(getFragmentManager()).beginTransaction(), TAG);
    }

}
