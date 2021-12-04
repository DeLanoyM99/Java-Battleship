package edu.msu.nagyjos2.project1;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

public class WaitingDlg extends DialogFragment {

    /**
     * Id for the image we are loading
     */
    private String Id;

    /**
     * Set true if we want to cancel
     */
    private volatile boolean cancel = false;

    public void setId(String id) {
        this.Id = id;
    }

    private final static String ID = "id";

    /**
     * Save the instance state
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ID, Id);
    }


    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        if (bundle != null) {
            Id = bundle.getString(ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.waiting);

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        delete(Id);
                    }
                });


        // Create the dialog box
        final AlertDialog dlg = builder.create();

        return dlg;
    }

    /**
     * Delete the lobby
     * @param hostId id it will remove from table
     */
    private void delete(final String hostId) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                boolean ok = cloud.lobbyDelete(hostId);
            }

        }).start();
    }

}
