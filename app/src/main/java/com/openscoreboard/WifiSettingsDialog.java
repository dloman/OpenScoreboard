package com.openscoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class WifiSettingsDialog extends DialogFragment {

    public interface WifiSettingsDialogListener {
        public void onDialogSetWifiSettings(String ssid, String password);
        public void onDialogCancelWifiSettings();
    }

    // Use this instance of the interface to deliver action events
    WifiSettingsDialogListener mListener;

    public static WifiSettingsDialog createInstance(ArrayList<String> ssidList, String currentSsid)
    {
        WifiSettingsDialog dialog = new WifiSettingsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("ssidList", ssidList);
        args.putString("currentSsid", currentSsid);
        dialog.setArguments(args);
        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (WifiSettingsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.set_wifi_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Wifi Network Settings");

        ArrayList<String> ssidList = getArguments().getStringArrayList("ssidList");
        String currentSsid = getArguments().getString("currentSsid");
        mSsidPicker = (Spinner) view.findViewById(R.id.get_ssid);
        mSsidAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ssidList);
        mSsidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSsidPicker.setAdapter(mSsidAdapter);
        mSsidPicker.setOnItemSelectedListener(itemSelectedListener);

        mPasswordPicker = (EditText) view.findViewById(R.id.get_password);

        builder.setPositiveButton(R.string.setNetwork, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogSetWifiSettings(
                        mSsidPicker.getSelectedItem().toString(),
                        mPasswordPicker.getText().toString());
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogCancelWifiSettings();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    ListView.OnItemSelectedListener itemSelectedListener = new ListView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedChannel = mSsidAdapter.getItem(position).toString();
            if (selectedChannel.equals("Enter Additional Network"))
            {
                final EditText input = new EditText(getActivity());

                new AlertDialog.Builder(getActivity())
                        .setMessage("Enter SSID")
                        .setView(input)
                        .setPositiveButton("Set SSID", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable editable = input.getText();
                                mSsidAdapter.insert(editable.toString(), mSsidAdapter.getCount()-1);
                                mSsidPicker.setSelection(mSsidAdapter.getCount()-2);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private Spinner mSsidPicker;
    private EditText mPasswordPicker;
    private ArrayAdapter<String> mSsidAdapter;
}
