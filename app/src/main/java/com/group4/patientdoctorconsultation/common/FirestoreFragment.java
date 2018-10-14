package com.group4.patientdoctorconsultation.common;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.ui.NavigationActivity;

public abstract class FirestoreFragment extends Fragment {

    public boolean handleFirestoreResult(FailableResource resource){

        if(resource.isSuccessful() && resource.getResource() != null){
            return true;
        }else if(resource.getError() != null) {
            Log.w("TAG", resource.getError());
            Toast.makeText(
                    requireContext(), resource.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        return false;
    }

    public void hideLoadingIcon(){
        ((NavigationActivity) requireActivity()).hideLoadingIcon();
    }

    public void showLoadingIcon(){
        ((NavigationActivity) requireActivity()).showLoadingIcon();
    }

    public boolean isPatient(){
        return ((NavigationActivity) requireActivity()).getProfileType() == Profile.ProfileType.PATIENT;
    }

}
