package dev.pool2go;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class DisplayFileList extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String files = "";
        for (String s : this.getActivity().fileList()) {
            files += s + ", ";
        }

        builder.setMessage(files)
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
