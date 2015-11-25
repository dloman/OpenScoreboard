package com.openscoreboard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class ShotClockActivity extends Fragment implements OnClickListener, View.OnLongClickListener
{

    public ShotClockActivity() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        mScoreboardData = bundle.getParcelable("ScoreboardData");
        mShotClockActivity = (ActionBarActivity) getActivity();
        View ShotClockView = inflater.inflate(R.layout.shotclock_layout, container, false);

		mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_top);
		mAnimation.setDuration(400);

		mShotClock = (TextView) ShotClockView.findViewById(R.id.shotclock_value);

        mShotClock.setOnLongClickListener(this);

		ShotClockView.findViewById(R.id.shotclock_start_stop).setOnClickListener(this);
        ShotClockView.findViewById(R.id.shotclock_reset).setOnClickListener(this);


        mClockStartStopButton = ((Button)ShotClockView.findViewById(R.id.shotclock_start_stop));
        mClockStartStopButton.setOnClickListener(this);


        mClockResetButton = ((Button)ShotClockView.findViewById(R.id.shotclock_reset));
        mClockResetButton.setOnClickListener(this);
        mClockResetButton.setOnLongClickListener(this);
        UpdateShotClock();

        return ShotClockView;
	}

    @Override
	public void onClick(View v)
	{
		int ViewId = v.getId();
		switch(ViewId)
		{
		case R.id.shotclock_start_stop:
			if (mScoreboardData.IsShotClockRunning()) {
                mScoreboardData.SetShotClockRunning(false);
            }
            else {
                mScoreboardData.SetShotClockRunning(true);
            }
			break;
		case R.id.shotclock_reset:
			mScoreboardData.ResetShotClock();
            break;
		}
		UpdateShotClock();
	}

    @Override
    public boolean onLongClick(View v)
    {
        int ViewId = v.getId();
        switch (ViewId) {
            case R.id.shotclock_value:
                ResetClock(false);
                mScoreboardData.SetShotClockRunning(false);
                break;
            case R.id.shotclock_reset:
                ResetClock(true);
                break;
        }
        return true;
    }

	public static void UpdateShotClock()
	{
        NumberFormat numberFormat = new DecimalFormat("00");

		long shotClockTime = mScoreboardData.GetShotClock();
		shotClockTime = TimeUnit.MILLISECONDS.toSeconds(shotClockTime);

		if (Integer.parseInt((String) mShotClock.getText()) != shotClockTime)
		{
		    mShotClock.setText(String.valueOf(numberFormat.format(shotClockTime)));
            mShotClock.startAnimation(mAnimation);
            Scoreboard.sendPacket(getShotClockString(), Scoreboard.PacketType.eShotClock);
		}
		if (mScoreboardData.IsShotClockRunning() && mScoreboardData.GetShotClock() != 0)
		{
	      mClockStartStopButton.setText("Stop Clock");
		}
		else
		{
		  mClockStartStopButton.setText("Start Clock");
		}
	}

    private static String getShotClockString()
    {
        return mShotClock.getText().toString();
    }
	
	private void ResetClock(boolean resetDefaultTime) {
        String title = "Set Current Shot Clock Time";
        NumberPickerDialog.NumberPickerReasons numberPickerReasons =
                NumberPickerDialog.NumberPickerReasons.eSetCurrentShotClock;
        int maxValue = (int)mScoreboardData.GetDefaultShotClockTime()/1000;
        Long initialTimePickerValue = mScoreboardData.GetShotClock();
        if (resetDefaultTime) {
            title = "Set Default Shot Clock Time";
            initialTimePickerValue = mScoreboardData.GetDefaultShotClockTime();
            numberPickerReasons = NumberPickerDialog.NumberPickerReasons.eSetDefaultShotClock;
            maxValue = 99;
        }

        DialogFragment dialogFragment =
                NumberPickerDialog.createInstance(title, "Time", initialTimePickerValue, numberPickerReasons, maxValue);
        dialogFragment.show(mShotClockActivity.getSupportFragmentManager(), "NumberEditor");
    }

    private static ActionBarActivity mShotClockActivity;
	private static ScoreboardData mScoreboardData;
	
	private static Animation mAnimation;
	
	private static Button mClockStartStopButton;
	private static Button mClockResetButton;

	private static TextView mShotClock;
}
