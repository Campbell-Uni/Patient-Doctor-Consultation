package com.group4.patientdoctorconsultation.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.internal.PlaceEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group4.patientdoctorconsultation.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapFragment.class.getSimpleName();

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment map = Objects.requireNonNull((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        map.getMapAsync(this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMinZoomPreference(15);

        Places.getPlaceDetectionClient(requireActivity(), null)
                .getCurrentPlace(null)
                .addOnCompleteListener(task -> {
                    try {
                        MarkerOptions markerOptions;

                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Place currentPlace = placeLikelihood.getPlace().freeze();
                            for (int type : currentPlace.getPlaceTypes()) {
                                if (true) {//type == Place.TYPE_HEALTH || type == Place.TYPE_DOCTOR || type == Place.TYPE_HOSPITAL  ){
                                    markerOptions = new MarkerOptions();

                                    markerOptions.position(currentPlace.getLatLng())
                                            .title(currentPlace.getName().toString())
                                            .snippet(Objects.requireNonNull(currentPlace.getAddress()).toString())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    googleMap.addMarker(markerOptions);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace.getLatLng()));
                                }
                            }
                        }
                        likelyPlaces.release();
                    }catch (Exception e){
                        Log.w(TAG, e.getMessage());
                    }

                });

    }
}
