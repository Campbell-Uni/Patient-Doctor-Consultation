package com.group4.patientdoctorconsultation.ui.dialogfragment;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.PacketItemDialog;
import com.group4.patientdoctorconsultation.data.adapter.ProfileAdapter;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.databinding.FragmentProfileBinding;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.ProfileViewModel;

import java.io.Serializable;
import java.util.Objects;

import androidx.navigation.Navigation;

public class ViewPatientProfileDialogFragment extends PacketItemDialog {

    private static final String EXTRA_PATIENT_PROFILE = "patient_profile_extra";

    private Profile patient;
    private FragmentProfileBinding binding;
    private AlertDialog alertDialog;


    public static ViewPatientProfileDialogFragment newInstance(Profile patientProfile){

        ViewPatientProfileDialogFragment patientProfileDialogFragment = new ViewPatientProfileDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PATIENT_PROFILE, patientProfile);
        patientProfileDialogFragment.setArguments(args);

        return patientProfileDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getTargetFragment() == null) {
            throw new NullPointerException("PacketItemDialog must implement fragment");
        }

        alertDialog = new AlertDialog.Builder(requireActivity())
                .setTitle(getTitle().replace("_", " "))
                .setView(getView(getTargetFragment().getLayoutInflater()))
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.cancel();
                    (getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                })
                .create();

        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(cancelEnabledByDefault());
        });

        return alertDialog;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(LayoutInflater inflater) {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, null, false);
        patient = (Profile) Objects.requireNonNull(getArguments()).getSerializable(EXTRA_PATIENT_PROFILE);
        binding.setProfile(patient);

        Button saveButton = binding.getRoot().findViewById(R.id.edit_save);
        Button cancelButton = binding.getRoot().findViewById(R.id.sign_out_button);
        saveButton.setVisibility(binding.getRoot().GONE);
        cancelButton.setVisibility(binding.getRoot().GONE);



        return binding.getRoot();
    }

    @Override
    protected String getTitle(){ return "Patient Profile"; }

    @Override
    public String getDialogResult(){
        return patient != null ? patient.getId() : "";
    }

    @Override
    public DataPacketItem.DataPacketItemType getPacketItemType() {
        return DataPacketItem.DataPacketItemType.NOTE;
    }

    private boolean cancelEnabledByDefault() {
        return true;
    }
}