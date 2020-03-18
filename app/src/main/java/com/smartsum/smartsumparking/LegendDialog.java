package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.smartsum.smartsumparking.databinding.DialogFragmentLoginBinding;
import com.smartsum.smartsumparking.databinding.DialogLegendBinding;

public class LegendDialog extends DialogFragment {

    //Vew binding
    private DialogLegendBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_legend, null, false);
        builder.setView(binding.getRoot());

        builder.setTitle(getString(R.string.legTitle))
                .setNegativeButton(getString(R.string.legDiagCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Leave
                    }
                });

        return builder.create();
    }
}
