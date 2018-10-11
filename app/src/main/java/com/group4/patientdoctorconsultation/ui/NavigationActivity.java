package com.group4.patientdoctorconsultation.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.ProfileViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class NavigationActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_PERMISSION = 2;
    private Profile.ProfileType profileType = Profile.ProfileType.PATIENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        initialiseViewModel();
        initialiseNavigationController();
        requestAllPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            startSignIn();
        }else if(requestCode == RC_PERMISSION && resultCode != Activity.RESULT_OK){
            requestAllPermissions();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Navigation.findNavController(this, R.id.navigation_fragment).navigateUp();
        return super.onSupportNavigateUp();
    }

    private void initialiseNavigationController() {
        NavController navController = Navigation.findNavController(this, R.id.navigation_fragment);
        setSupportActionBar(findViewById(R.id.toolbar));
        NavigationUI.setupActionBarWithNavController(this, navController);
        NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.navigation_view), navController);
    }

    private void initialiseViewModel() {
        ProfileViewModel viewModel = DependencyInjector.provideProfileViewModel(this);
        int duration = Toast.LENGTH_LONG;
        Context context = getApplicationContext();
        viewModel.getIsSignedIn().observe(this, isSignedIn -> {
            if (isSignedIn != null && !isSignedIn) {
                startSignIn();
            }
        });
        viewModel.getProfile().observe(this, profile -> {
           if(profile != null && profile.getResource() != null){
               profileType = profile.getResource().getProfileType();
            }
        });

    }

    private void startSignIn() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN);
    }

    private void requestAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_PERMISSION
            );
        }
    }

    public Profile.ProfileType getProfileType() {
        return profileType;
    }
}
