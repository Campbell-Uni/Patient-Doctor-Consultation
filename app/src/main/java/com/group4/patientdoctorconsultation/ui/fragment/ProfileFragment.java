package com.group4.patientdoctorconsultation.ui.fragment;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
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
import com.group4.patientdoctorconsultation.databinding.FragmentProfileBinding;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.ProfileViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileFragment extends FirestoreFragment {

    private ProfileViewModel viewModel;
    private FragmentProfileBinding binding;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
         binding.setProfileHandler(this);
         binding.signOutButton.setOnClickListener(view -> logout(null));
         bindAge(binding.editAge);
         observeProfile();
         return binding.getRoot();
    }

    private void observeProfile(){
        viewModel = DependencyInjector.provideProfileViewModel(requireActivity());
        viewModel.getProfile().observe(this, profile -> {
            if(profile != null && handleFirestoreResult(profile)){
                binding.setProfile(profile.getResource());
            }
        });
    }

    private void bindAge(EditText ageField){
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

    public void submit(View view){ //Do not remove parameter, required for data binding
        Profile profile = binding.getProfile();
        profile.setProfileType(Profile.ProfileType.DOCTOR);
        viewModel.updateProfile(profile).observe(this, isComplete -> {
            if(isComplete != null && handleFirestoreResult(isComplete) && isComplete.getResource()){
                Toast.makeText(requireContext(), "Saved",Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean logout(View view){ //Do not remove parameter, required for data binding
        FirebaseAuth.getInstance().signOut();
        return true;
    }
}
