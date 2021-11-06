package edu.msu.nagyjos2.project1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class BackButtonDlg extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder checkDlg = new AlertDialog.Builder(getActivity());

        // Set the title
        checkDlg.setTitle(getString(R.string.back_check_title));

        // Set the body text
        checkDlg.setMessage(getString(R.string.back_check_body));

        checkDlg.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do not go back to main
            }
        });

        checkDlg.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // go back to main activity
                Intent main_act = new Intent(getActivity(), MainActivity.class);
                main_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main_act);
            }
        });

        return checkDlg.create();
    }
}
