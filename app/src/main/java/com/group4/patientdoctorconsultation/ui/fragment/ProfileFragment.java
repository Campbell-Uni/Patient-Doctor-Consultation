package com.group4.patientdoctorconsultation.ui.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.FirestoreFragment;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.databinding.FragmentDoctorProfileBinding;
import com.group4.patientdoctorconsultation.databinding.FragmentProfileBinding;
import com.group4.patientdoctorconsultation.ui.NavigationActivity;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.ProfileViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends FirestoreFragment {

    private ProfileViewModel viewModel;
    private FragmentProfileBinding patientBinding;
    private FragmentDoctorProfileBinding doctorBinding;
    private Boolean isPatient;
    private Drawable editTextBackground;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        isPatient = isPatient();

        if (isPatient) {
            patientBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
            editTextBackground = patientBinding.editFirstName.getBackground();
            patientBinding.setProfileHandler(this);
            patientBinding.setLocked(true);
            patientBinding.setEditable(true);
            patientBinding.signOutButton.setOnClickListener(this::logout);
            bindAge(patientBinding.editAge);
            view = patientBinding.getRoot();
        } else {
            doctorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_doctor_profile, container, false);
            editTextBackground = doctorBinding.editFirstName.getBackground();
            doctorBinding.setProfileHandler(this);
            doctorBinding.setLocked(true);
            doctorBinding.setEditable(true);
            doctorBinding.editSave.setOnClickListener(this::submit);
            doctorBinding.signOutButton.setOnClickListener(this::logout);
            view = doctorBinding.getRoot();
        }

        observeProfile();
        return view;
    }

    private void observeProfile() {
        viewModel = DependencyInjector.provideProfileViewModel(requireActivity());
        viewModel.getProfile().observe(this, profile -> {
            if (profile != null && handleFirestoreResult(profile)) {
                if (isPatient) {
                    patientBinding.setProfile(profile.getResource());
                } else {
                    doctorBinding.setProfile(profile.getResource());
                }
            }
        });
    }

    private void bindAge(EditText ageField) {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        DatePickerDialog.OnDateSetListener datePicker = (datePicker1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            ageField.setText(dateFormat.format(calendar.getTime()));
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(today.getTime());

        ageField.setOnClickListener(view -> datePickerDialog.show());
    }

    public void submit(View view) { //Do not remove parameter, required for data patientBinding
        Profile profile;
        lockUnlockLayout(null);
        showLoadingIcon();

        if (isPatient) {
            profile = patientBinding.getProfile();
        } else {
            profile = doctorBinding.getProfile();
        }

        if (!validateProfile(profile)) {
            hideLoadingIcon();
            return;
        }

        viewModel.updateProfile(profile).observe(this, isComplete -> {
            if (isComplete != null && handleFirestoreResult(isComplete) && isComplete.getResource()) {
                hideLoadingIcon();
            }
        });
    }

    private boolean validateProfile(Profile profile) {
        String errorMessage = "";
        String gender = profile.getGender().toLowerCase();

        if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
            errorMessage += "\n Gender must be: Male, Female or Other";
        }

        try {
            double height = Double.parseDouble(profile.getHeightInCentimetres().replaceAll("[^\\d.]", ""));
            if (height > 200 || height < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            errorMessage += "\n Height not valid";
        }

        try {
            double weight = Double.parseDouble(profile.getWeightInKg().replaceAll("[^\\d.]", ""));
            if (weight > 200 || weight < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            errorMessage += "\n Weight not valid";
        }

        if (!errorMessage.isEmpty()) {
            new AlertDialog
                    .Builder(requireActivity())
                    .setTitle("Invalid Profile")
                    .setMessage("Please correct the following: " + errorMessage)
                    .setNegativeButton("OK", (dialog, id) -> {
                    })
                    .show();

            return false;
        }

        return true;
    }

    public boolean logout(View view) { //Do not remove parameter, required for data patientBinding
        FirebaseAuth.getInstance().signOut();
        return true;
    }

    public void lockUnlockLayout(View view) {
        if (isPatient) {
            boolean locked = patientBinding.getLocked();
            if (!locked) {
                editTextBackground = patientBinding.editFirstName.getBackground();
            }
            patientBinding.setLocked(!locked);
        } else {
            boolean locked = doctorBinding.getLocked();
            if (!locked) {
                editTextBackground = doctorBinding.editFirstName.getBackground();
            }
            doctorBinding.setLocked(!locked);

        }
    }

    public Drawable getEditTextBackground(boolean isLocked) {
        if (isLocked) {
            return getResources().getDrawable(R.drawable.transparent);
        } else {
            return editTextBackground;
        }
    }
}
