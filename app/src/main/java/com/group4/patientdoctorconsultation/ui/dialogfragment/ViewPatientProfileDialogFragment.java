package com.group4.patientdoctorconsultation.ui.dialogfragment;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.PacketItemDialog;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.databinding.FragmentProfileBinding;

public class ViewPatientProfileDialogFragment extends PacketItemDialog {

    private FragmentProfileBinding binding;
    private Profile profile;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //profile = (Profile); - Unsure what to assign profile to
        return super.onCreateDialog(savedInstanceState);
    }

    @Override //Unsure about what to return
    public String getDialogResult(){
        return null;
    }

    @Override //Unsure about what to return
    public DataPacketItem.DataPacketItemType getPacketItemType() {
        return null;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, null, false);
        binding.setProfile(profile);
        return binding.getRoot();

    }

    protected String getTitle(){ return "Patient Profile"; }
    }
