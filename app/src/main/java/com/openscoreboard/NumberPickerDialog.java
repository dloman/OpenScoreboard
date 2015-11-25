package com.openscoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class NumberPickerDialog extends DialogFragment {

    public interface NumberPickerDialogListener {
        public void onDialogPositiveClick(int Value, NumberPickerReasons reasons);
        public void onDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    NumberPickerDialogListener mListener;

    public static NumberPickerDialog createInstance(String title, String simpleTitle, long currentValue, NumberPickerReasons reasons, int maxValue)
    {
        NumberPickerDialog dialog = new NumberPickerDialog();
        Bundle args = new Bundle();
        args.putSerializable("reasons", reasons);
        args.putString("title", title);
        args.putString("simpleTitle", simpleTitle);
        args.putLong("currentValue", currentValue);
        args.putInt("maxValue", maxValue);
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
            mListener = (NumberPickerDialogListener) activity;
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

        View view = inflater.inflate(R.layout.set_number_fragment, null);

        TextView pickerTextView = (TextView) view.findViewById(R.id.Value);
        pickerTextView.setText("Enter New " + getArguments().getString("simpleTitle"));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title"));

        //View.OnClickListener dismiss = new View.OnClickListener();
        builder.setPositiveButton("Set " + getArguments().getString("simpleTitle"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(
                        mPicker.getValue(),
                        (NumberPickerReasons) getArguments().getSerializable("reasons"));
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogNegativeClick();
            }
        });
        long currentValue = getArguments().getLong("currentValue");
        int maxValue = getArguments().getInt("maxValue");
        int value = 0;
        switch ((NumberPickerReasons)getArguments().getSerializable("reasons"))
        {
            case eSetDefaultShotClock:
            case eSetCurrentShotClock:
                value = (int)currentValue/1000;
                break;
            case eSetAwayScore:
            case eSetHomeScore:
                value = (int)currentValue;
                break;
        }
        mPicker = (NumberPicker) view.findViewById(R.id.SetValueNumberPicker);
        mPicker.setMaxValue(maxValue);
        mPicker.setMinValue(0);
        mPicker.setWrapSelectorWheel(true);
        mPicker.setValue(value);

        builder.setView(view);
        return builder.create();
    }


    public enum NumberPickerReasons
        {eSetDefaultShotClock, eSetCurrentShotClock, eSetHomeScore, eSetAwayScore}


    private NumberPicker mPicker;
}
