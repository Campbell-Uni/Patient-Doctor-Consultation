package com.group4.patientdoctorconsultation;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class doctor_profile_Fragment extends Fragment {

private static final String TAG="doctor profile";
    //DECLARE THE FIELDS
EditText name;
EditText specialty;
EditText location;
EditText email;
EditText description;
Button save;
    //FIREBASE AUTHENTICATION FIELDS
FirebaseAuth mAuth;
FirebaseAuth.AuthStateListener mAuthListener;
    //FIREBASE DATABASE FIELDS
    DatabaseReference mUserDatabase;
    StorageReference mStorageRef;

@Nullable
@Override
    public View onCreateView (LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
    View view=inflater.inflate(R.layout.fragment_doctor_profile_,container,false);
    name=(EditText)view.findViewById(R.id.dname);
    specialty=(EditText)view.findViewById(R.id.specialty);
    location=(EditText)view.findViewById(R.id.location);
    email=(EditText)view.findViewById(R.id.email);
    description=(EditText)view.findViewById(R.id.description);
    save=(Button)view.findViewById(R.id.save);
    mAuth=FirebaseAuth.getInstance();
    mAuthListener=new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user=firebaseAuth.getCurrentUser();
            if (user!=null){
                Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            }
            else{
                Log.d(TAG, "onAuthStateChanged: signed_out");
            }

        }
    };

    mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
    mStorageRef= FirebaseStorage.getInstance().getReference();

    save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveUserProfile();

        }
    });
    return  view;
}
private void saveUserProfile(){
    String doctorname,doctorspecialty,doctorlocation,doctoremail,doctordescription;
    doctorname=name.getText().toString().trim();
    doctorspecialty=specialty.getText().toString().trim();
    doctorlocation=location.getText().toString().trim();
    doctoremail=email.getText().toString().trim();
    doctordescription=description.getText().toString().trim();

    if(!TextUtils.isEmpty(doctorname) && !TextUtils.isEmpty(doctorspecialty)&& !TextUtils.isEmpty(doctorlocation)&& !TextUtils.isEmpty(doctoremail)){
        mUserDatabase.child("Doctor name").setValue(doctorname);
        mUserDatabase.child("Doctor specialty").setValue(doctorspecialty);
        mUserDatabase.child("Doctor location").setValue(doctorlocation);
        mUserDatabase.child("Doctor email").setValue(doctoremail);
        mUserDatabase.child("Doctor decription").setValue(doctordescription);


    }
    else {
        Toast.makeText(getActivity(), "Please complete the form  ", Toast.LENGTH_LONG).show();
    }
}

}
