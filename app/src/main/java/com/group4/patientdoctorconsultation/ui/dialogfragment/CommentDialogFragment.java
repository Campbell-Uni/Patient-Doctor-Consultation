package com.group4.patientdoctorconsultation.ui.dialogfragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import com.group4.patientdoctorconsultation.R;
import com.group4.patientdoctorconsultation.common.PacketItemDialog;
import com.group4.patientdoctorconsultation.data.model.DataPacketItem;
import com.group4.patientdoctorconsultation.databinding.FragmentDialogCommentBinding;

import java.util.Objects;

public class CommentDialogFragment extends PacketItemDialog {


    private static final String EXTRA_DATA_PACKET = "data_packet_extra";
    private DataPacketItem dataPacketItem;
    private FragmentDialogCommentBinding binding;

    public static CommentDialogFragment newInstance(DataPacketItem dataPacketItem){

        CommentDialogFragment initialSyncDialog = new CommentDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATA_PACKET, dataPacketItem);
        initialSyncDialog.setArguments(args);

        return initialSyncDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dataPacketItem = (DataPacketItem) Objects.requireNonNull(getArguments()).getSerializable(EXTRA_DATA_PACKET);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_comment, null, false);
        binding.setDataPacketItem(dataPacketItem);
        return binding.getRoot();
    }

    @Override
    public DataPacketItem getDataPacketItem() {
        return binding.getDataPacketItem() != null ? binding.getDataPacketItem() : new DataPacketItem();
    }

    @Override
    public DataPacketItem.DataPacketItemType getPacketItemType() {
        return dataPacketItem != null ? dataPacketItem.getDataPacketItemType() : DataPacketItem.DataPacketItemType.NOTE;
    }

    @Override
    public String getDialogResult(){
        return binding.getDataPacketItem() != null ? binding.getDataPacketItem().getValue() : "";
    }

    @Override
    protected String getDialogDisplayResult() {
        return binding.getDataPacketItem() != null ? binding.getDataPacketItem().getDisplayValue() : super.getDialogDisplayResult();
    }

}
