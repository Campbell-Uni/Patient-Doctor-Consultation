package com.group4.patientdoctorconsultation.ui.fragment;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
                if(isPatient){
                    patientBinding.setProfile(profile.getResource());
                }else{
                    doctorBinding.setProfile(profile.getResource());
                }
            }
        });
    }

    private void bindAge(EditText ageField) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        DatePickerDialog.OnDateSetListener datePicker = (datePicker1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            ageField.setText(dateFormat.format(calendar.getTime()));
        };

        ageField.setOnClickListener(view -> new DatePickerDialog(
                requireContext(),
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        );
    }

    public void submit(View view) { //Do not remove parameter, required for data patientBinding
        Profile profile;
        lockUnlockLayout(null);
        showLoadingIcon();

        if(isPatient){
            profile = patientBinding.getProfile();
        }else{
            profile = doctorBinding.getProfile();
        }

        viewModel.updateProfile(profile).observe(this, isComplete -> {
            if (isComplete != null && handleFirestoreResult(isComplete) && isComplete.getResource()) {
                hideLoadingIcon();
            }
        });
    }

    public boolean logout(View view) { //Do not remove parameter, required for data patientBinding
        FirebaseAuth.getInstance().signOut();
        return true;
    }

    public void lockUnlockLayout(View view){
        if(isPatient){
            patientBinding.setLocked(!patientBinding.getLocked());
        }else{
            doctorBinding.setLocked(!doctorBinding.getLocked());
        }
    }

    public Drawable getEditTextBackground(boolean isLocked){
        if(isLocked){
            return getResources().getDrawable(R.drawable.transparent);
        }else{
            return editTextBackground;
        }
    }
}
