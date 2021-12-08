package edu.msu.nagyjos2.project1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

public class CreateDlg extends DialogFragment {

    private AlertDialog dlg;
    private String hostId;
    private String hostName;
    private View lobbyview = null;

    public void setHostId(String id) {
        this.hostId = id;
    }

    public void setHostName(String name) {
        this.hostName = name;
    }

    public void setLobbyview(View view) { this.lobbyview = view; }

    /**
     * Create the dialog box
     * @param savedInstanceState The saved instance bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.lobby_create);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.create_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText editName = (EditText)dlg.findViewById(R.id.editName);
                save(hostId, editName.getText().toString().trim());
                WaitingDlg waitDlg = new WaitingDlg();
                waitDlg.setHostid(hostId);
                waitDlg.setHostname(hostName);
                waitDlg.show(getActivity().getSupportFragmentManager(), "waiting");
            }
        });

        dlg = builder.create();

        return dlg;
    }

    /**
     * Actually create the lobby
     * @param name name to save it under
     */
    private void save(final String hostId, final String name) {

        final View view = this.lobbyview;
        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                boolean result = cloud.lobbyCreate(hostId, name);

                view.post(new Runnable() {

                    @Override
                    public void run() {
                        if (!result) { // lobby could not be created
                            Toast.makeText(view.getContext(),
                                    R.string.lobby_fail,
                                    Toast.LENGTH_SHORT).show();

                            dlg.dismiss();
                        }
                        else {
                            Toast.makeText(view.getContext(),
                                    R.string.lobby_success,
                                    Toast.LENGTH_SHORT).show();

                            dlg.dismiss();
                        }
                    }
                });
            }

        }).start();
    }
}
