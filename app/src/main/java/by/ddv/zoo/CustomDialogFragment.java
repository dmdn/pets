package by.ddv.zoo;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CustomDialogFragment extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        String numberPETs = getArguments().getString("number_pets");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Information on PETs")
                .setIcon(R.drawable.no_image)
                .setMessage("Number of PETs: " + numberPETs)
                //.setView(R.layout.dialog)
                .setPositiveButton("OK", null)
                //.setNegativeButton("Cancel", null)
                .create();

    }
}
