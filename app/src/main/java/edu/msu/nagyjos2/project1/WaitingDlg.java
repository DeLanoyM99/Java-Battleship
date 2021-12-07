package edu.msu.nagyjos2.project1;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

public class WaitingDlg extends DialogFragment {

    /**
     * Id for the image we are loading
     */
    private String hostid;

    /**
     * Set true if we want to cancel
     */
    private volatile boolean cancel = false;

    public void setHostid(String hostid) {
        this.hostid = hostid;
    }

    private final static String ID = "id";


    /**
     * Save the instance state
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ID, hostid);
    }


    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        if (bundle != null) {
            hostid = bundle.getString(ID);
        }

        cancel = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.waiting);

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cancel = true;
                        delete(hostid);
                    }
                });


        // Create the dialog box
        final AlertDialog dlg = builder.create();

        // Create a thread to wait for the guest in
        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();

                // start polling
                final String guestid = cloud.LobbyWaitForGuest(hostid);

                if (cancel) {
                    return;
                }

                if (guestid.equals("")) {
                    //error
                    delete(hostid);
                    dlg.dismiss();
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else {

                    final int guestint = Integer.parseInt(guestid);
                    if (guestint > -1) {
                        // we have found a guest
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), ShipPlacement.class);
                                intent.putExtra("Player1Name", hostid);
                                intent.putExtra("Player2Name", guestid);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        // no guest joined yet
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        run();
                    }
                }
            }
        }).start();

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
