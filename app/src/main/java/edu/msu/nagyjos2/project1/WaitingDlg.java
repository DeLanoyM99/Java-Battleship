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

            final Cloud cloud = new Cloud();

            @Override
            public void run() {

                int guestid;
                while (true) {

                    guestid = getGuestId();
                    if (guestid == -2) {
                        // error occurred, stop
                        return;
                    }
                    else if (guestid == -1) {
                        // no guest joined yet
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    // guest found, start game
                    final String guestStrId = Integer.toString(guestid);
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), ShipPlacement.class);
                            intent.putExtra("Player1Name", hostid);
                            intent.putExtra("Player2Name", guestStrId);
                            startActivity(intent);
                        }
                    });
                    return;
                }
            }

            /**
             * Polling function, gets the guest id every 1 second. Returns guest id if
             * guest joined, -2 on error, -1 on guest not found
             * @return int representing result of the query
             */
            public int getGuestId() {

                // start polling
                final String guestid = cloud.LobbyWaitForGuest(hostid);

                if (cancel) {
                    return -2;
                }
                if (guestid.equals("")) {
                    // error occurred
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

                    return -2;
                }
                else {
                    // poll success, return the guest id if someone joined
                    int guestIntId = Integer.parseInt(guestid);
                    return Math.max(guestIntId, -1);
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
