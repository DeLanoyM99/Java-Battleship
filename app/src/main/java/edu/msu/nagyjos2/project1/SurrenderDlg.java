package edu.msu.nagyjos2.project1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class SurrenderDlg extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder checkDlg = new AlertDialog.Builder(getActivity());

        // Set the title
        checkDlg.setTitle(getString(R.string.surrender_check_title));

        // Set the body text
        checkDlg.setMessage(getString(R.string.surrender_check_body));

        checkDlg.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do not surrender
            }
        });

        checkDlg.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // surrender
                GameActivity act = (GameActivity)getActivity();
                Intent intent = new Intent(act, EndActivity.class);
                if (act.getGameView().getCurrPlayer() == 1) {
                    intent.putExtra("WinnerName", act.getPlayer2Name());
                    intent.putExtra("LoserName", act.getPlayer1Name());
                } else if (act.getGameView().getCurrPlayer() == 2) {
                    intent.putExtra("WinnerName", act.getPlayer1Name());
                    intent.putExtra("LoserName", act.getPlayer2Name());
                }
                startActivity(intent);
            }
        });

        return checkDlg.create();
    }
}