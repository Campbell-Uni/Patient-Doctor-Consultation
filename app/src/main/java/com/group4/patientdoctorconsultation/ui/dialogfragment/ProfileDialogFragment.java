package com.group4.patientdoctorconsultation.ui.dialogfragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.FirestoreFragment;
import com.group4.patientdoctorconsultation.common.PacketItemDialog;
import com.group4.patientdoctorconsultation.data.adapter.ProfileAdapter;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.ProfileViewModel;

import java.util.Objects;

public class ProfileDialogFragment extends PacketItemDialog {

    public final static String EXTRA_PROFILE_LIST_TYPE = "list_type";
    public final static String EXTRA_PROFILE_LIST_TYPE_DOCTORS = "doctors";
    public final static String EXTRA_PROFILE_LIST_TYPE_FULL = "full";

    private Profile profile;

    public static ProfileDialogFragment newInstance(String listType) {

        ProfileDialogFragment profileFragment = new ProfileDialogFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_PROFILE_LIST_TYPE, listType);
        profileFragment.setArguments(args);

        return profileFragment;
    }

    @Override
    public String getDialogResult() {
        return profile != null ? profile.getId() : "";
    }

    @Override
    protected String getDialogDisplayResult() {
        return profile != null ? profile.getUserName() : "";
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(LayoutInflater inflater) {

        if (!(getTargetFragment() instanceof FirestoreFragment)) {
            throw new IllegalStateException("Host fragment must extend FireStoreFragment");
        }

        String listType = Objects.requireNonNull(getArguments()).getString(EXTRA_PROFILE_LIST_TYPE);

        View view = inflater.inflate(R.layout.fragment_dialog_profile, null);
        ProfileViewModel profileViewModel = DependencyInjector.provideProfileViewModel(requireActivity());
        ProfileAdapter profileAdapter = new ProfileAdapter(item -> profile = item);
        RecyclerView profileList = view.findViewById(R.id.profile_list);
        profileList.setLayoutManager(new LinearLayoutManager(requireContext()));
        profileList.setAdapter(profileAdapter);

        if (Objects.equals(listType, EXTRA_PROFILE_LIST_TYPE_DOCTORS)) {
            profileViewModel.getLinkedProfiles().observe(this, profiles -> {
                if (profiles != null && ((FirestoreFragment) getTargetFragment()).handleFirestoreResult(profiles)) {
                    profileAdapter.replaceListItems(profiles.getResource());
                }
            });
        } else {
            profileViewModel.getAllProfiles().observe(this, profiles -> {
                if(profiles != null && ((FirestoreFragment) getTargetFragment()).handleFirestoreResult(profiles)){
                    profileAdapter.replaceListItems(profiles.getResource());
                }
            });
        }

        return view;
    }

    @Override
    public DataPacketItem.DataPacketItemType getPacketItemType() {
        return DataPacketItem.DataPacketItemType.NOTE;
    }

    @Override
    protected String getTitle() {
        return "Select a Profile";
    }
}
