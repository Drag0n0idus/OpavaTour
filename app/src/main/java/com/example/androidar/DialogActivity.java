package com.example.androidar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;

    public class DialogActivity extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_connect)
                    .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setMultiChoiceItems("Don't show again.", null, new DialogInterface.OnMultiChoiceClickListener(){
                        public void onClick(DialogInterface dialog, boolean isChecked) {
                            if (isChecked) {
                                return;
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
