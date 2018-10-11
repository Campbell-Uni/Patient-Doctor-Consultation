package com.group4.patientdoctorconsultation.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.group4.patientdoctorconsultation.data.model.DataPacketItem;

public abstract class PacketItemDialog extends DialogFragment {

    public static final String EXTRA_RESULT = "extra_result";
    private AlertDialog alertDialog;

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
                .setPositiveButton("SAVE", (dialogInterface, i) -> {
                    Intent result = new Intent();
                    DataPacketItem dataPacketItem = getDataPacketItem();
                    dataPacketItem.setDataPacketItemType(getPacketItemType());
                    dataPacketItem.setValue(getDialogResult());
                    dataPacketItem.setDisplayValue(getDialogDisplayResult());
                    result.putExtra(EXTRA_RESULT, dataPacketItem);
                    (getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                })
                .create();

            alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(saveEnabledByDefault());
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(cancelEnabledByDefault());
        });

        return alertDialog;
    }

    protected AlertDialog getAlertDialog() {
        return alertDialog;
    }

    protected boolean saveEnabledByDefault() {
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    private boolean cancelEnabledByDefault() {
        return true;
    }

    protected String getDialogDisplayResult(){
        return getDialogResult();
    }

    protected String getTitle(){
        return getPacketItemType().toString();
    }

    protected DataPacketItem getDataPacketItem(){
        return new DataPacketItem();
    }

    protected abstract String getDialogResult();

    protected abstract DataPacketItem.DataPacketItemType getPacketItemType();

    protected abstract View getView(LayoutInflater inflater);
}
