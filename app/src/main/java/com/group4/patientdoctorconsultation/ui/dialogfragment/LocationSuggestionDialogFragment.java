package com.group4.patientdoctorconsultation.ui.dialogfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.PacketItemDialog;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;

public class LocationSuggestionDialogFragment extends PacketItemDialog implements View.OnClickListener {

    private static final Integer RC_LOCATION = 1;
    private EditText suggestionText;
    private TextView placeText;
    private String locationText = "";

    @Override
    public String getDialogResult() {
        return locationText;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.fragment_dialog_location_suggestion, null);
        suggestionText = view.findViewById(R.id.suggestion_input);
        placeText = view.findViewById(R.id.place_text);
        view.findViewById(R.id.select_location_button).setOnClickListener(this);
        return view;
    }

    @Override
    public DataPacketItem.DataPacketItemType getPacketItemType() {
        return DataPacketItem.DataPacketItemType.LOCATION;
    }

    @Override
    protected boolean saveEnabledByDefault() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCATION && resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(requireContext(), data);
            String currentComment = suggestionText.getText().toString();
            String locationString = getLocationString(place.getLatLng().latitude, place.getLatLng().longitude);

            placeText.setText(locationString);

            if (!currentComment.isEmpty()) {
                String newComment = currentComment + "\n[" + place.getName() + "]";
                suggestionText.setText(newComment);
            } else {
                String newComment = "[" + place.getName() + "]";
                suggestionText.setText(newComment);
            }

            locationText = suggestionText.getText().toString() + "\n" + locationString;
            getAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select_location_button){
            try {
                startActivityForResult(
                        new PlacePicker.IntentBuilder().build(requireActivity()),
                        RC_LOCATION
                );
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    private String getLocationString(Double latitude, Double longitude){
        return "http://maps.google.com/?q=" + latitude + "," + longitude;
    }
}
