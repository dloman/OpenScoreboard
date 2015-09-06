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

import java.util.concurrent.TimeUnit;

public class GameTimePickerDialog extends DialogFragment {

    public interface GameTimePickerDialogListener {
        public void onDialogPositiveClick(int Minutes, int Seconds, boolean resetPeriodTime);
        public void onDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    GameTimePickerDialogListener mListener;

    public static GameTimePickerDialog createInstance(String title, long currentPeriodTime, boolean resetPeriodTime)
    {
        GameTimePickerDialog dialog = new GameTimePickerDialog();
        Bundle args = new Bundle();
        args.putBoolean("resetPeriodTime", resetPeriodTime);
        args.putString("title", title);
        args.putLong("currentPeriodTime", currentPeriodTime);
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
            mListener = (GameTimePickerDialogListener) activity;
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

        View view = inflater.inflate(R.layout.set_clock_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title"));

        //View.OnClickListener dismiss = new View.OnClickListener();
        builder.setPositiveButton(R.string.setTime, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
              mListener.onDialogPositiveClick(
                      mMinutesPicker.getValue(),
                      mSecondsPicker.getValue(),
                      getArguments().getBoolean("resetPeriodTime"));
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
              // Send the positive button event back to the host activity
              mListener.onDialogNegativeClick();
            }
        });
        long periodTime = getArguments().getLong("currentPeriodTime");
        int periodTimeMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(periodTime);
        periodTime -= TimeUnit.MINUTES.toMillis(periodTimeMinutes);
        int periodTimeSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(periodTime);
        mSecondsPicker = (NumberPicker) view.findViewById(R.id.SetSecondsNumberPicker);
        mSecondsPicker.setValue(periodTimeSeconds);
        mSecondsPicker.setMaxValue(59);
        mSecondsPicker.setMinValue(0);
        mSecondsPicker.setWrapSelectorWheel(true);

        mMinutesPicker = (NumberPicker) view.findViewById(R.id.SetMinutesNumberPicker);
        mMinutesPicker.setMaxValue(10);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setValue(periodTimeMinutes);
        mMinutesPicker.setWrapSelectorWheel(true);

        builder.setView(view);
        return builder.create();
    }

    private NumberPicker mSecondsPicker;
    private NumberPicker mMinutesPicker;
}
