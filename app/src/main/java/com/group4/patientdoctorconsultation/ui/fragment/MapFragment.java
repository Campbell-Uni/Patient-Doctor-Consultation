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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.data.model.DataPacket;
import com.group4.patientdoctorconsultation.utilities.DependencyInjector;
import com.group4.patientdoctorconsultation.viewmodel.DataPacketViewModel;

import java.util.*;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapFragment.class.getSimpleName();
    private Map<String, LatLng> packetPlaces = new HashMap<>();

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment map = Objects.requireNonNull((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        DataPacketViewModel viewModel = DependencyInjector.provideDataPacketViewModel(requireActivity());
        viewModel.getDataPackets().observe(this, packets -> {
            if (packets != null && packets.isSuccessful() && packets.getResource() != null) {
                for (DataPacket dataPacket : packets.getResource()) {
                    if(!dataPacket.getLocations().isEmpty()){
                        for (String location : dataPacket.getLocations()) {
                            try {
                                String[] commentParts = location.split("\n");
                                StringBuilder name = new StringBuilder("Suggested Location");
                                double latitude = 0;
                                double longitude = 0;

                                int locationIndex = -1;

                                for (int i = 0; i < commentParts.length; i++) {
                                    String commentPart = commentParts[i];
                                    if(commentPart.contains("http://maps.google.com/?q=")){
                                        String[] locationParts = commentPart
                                                .replace("http://maps.google.com/?q=", "")
                                                .split(",");
                                        latitude = Double.parseDouble(locationParts[0]);
                                        longitude = Double.parseDouble(locationParts[1]);

                                        locationIndex = i;
                                        break;
                                    }
                                }

                                if(locationIndex == -1){
                                    return;
                                }

                                name = new StringBuilder();
                                for(int i = 0; i < locationIndex; i++){
                                    name.append(commentParts[i]);
                                }

                                packetPlaces.put(name.toString(), new LatLng(latitude, longitude));
                            }catch (Exception e){
                                Log.w(TAG, "Failed to parse location string: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        });
        map.getMapAsync(this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMinZoomPreference(15);

        for (Map.Entry<String, LatLng> packetPlace : packetPlaces.entrySet()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(packetPlace.getValue())
                .title(packetPlace.getKey())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(packetPlace.getValue()));
        }

        Places.getPlaceDetectionClient(requireActivity(), null)
                .getCurrentPlace(null)
                .addOnCompleteListener(task -> {
                    try {
                        MarkerOptions markerOptions;

                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Place currentPlace = placeLikelihood.getPlace().freeze();
                            for (int type : currentPlace.getPlaceTypes()) {
                                if (type == Place.TYPE_HEALTH || type == Place.TYPE_DOCTOR || type == Place.TYPE_HOSPITAL  ){
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
