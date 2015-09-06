package com.openscoreboard;

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

public class ScoreboardActivity extends Fragment implements OnClickListener, View.OnLongClickListener
{
    ScoreboardActivity(ScoreboardData ScoreboardData)
    {
        mScoreboardData = ScoreboardData;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mScoreboardActivity = (ActionBarActivity) getActivity();
        View ScoreboardView = inflater.inflate(R.layout.scoreboard_layout, container, false);

		mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_top);
		mAnimation.setDuration(400);

		mHomeScore = (TextView) ScoreboardView.findViewById(R.id.home_score_value);
		mAwayScore = (TextView) ScoreboardView.findViewById(R.id.away_score_value);
        mHomeScore.setOnLongClickListener(this);
        mAwayScore.setOnLongClickListener(this);


		mGameClockSeconds = (TextView) ScoreboardView.findViewById(R.id.game_clock_seconds_value);
		mGameClockMinutes = (TextView) ScoreboardView.findViewById(R.id.game_clock_minutes_value);

        mGameClockSeconds.setOnLongClickListener(this);
        mGameClockMinutes.setOnLongClickListener(this);

		(ScoreboardView.findViewById(R.id.home_score_up)).setOnClickListener(this);
        (ScoreboardView.findViewById(R.id.home_score_down)).setOnClickListener(this);
        (ScoreboardView.findViewById(R.id.away_score_up)).setOnClickListener(this);
        (ScoreboardView.findViewById(R.id.away_score_down)).setOnClickListener(this);
        (ScoreboardView.findViewById(R.id.reset_away_score)).setOnClickListener(this);
        (ScoreboardView.findViewById(R.id.reset_home_score)).setOnClickListener(this);

        mClockStartStopButton = ((Button)ScoreboardView.findViewById(R.id.start_game_clock));
        mClockStartStopButton.setOnClickListener(this);
        
        
        mClockResetButton = ((Button)ScoreboardView.findViewById(R.id.reset_game_clock));
        mClockResetButton.setOnClickListener(this);
        mClockResetButton.setOnLongClickListener(this);
        UpdateScoreboard();
        
        return ScoreboardView;
	}

    @Override
	public void onClick(View v)
	{
		int ViewId = v.getId();
		switch(ViewId)
		{
		case R.id.home_score_up:
			mScoreboardData.SetHomeScore(mScoreboardData.GetHomeScore() + 1);
			break;			
		case R.id.home_score_down:
			mScoreboardData.SetHomeScore(mScoreboardData.GetHomeScore() - 1);
			break;
		case R.id.away_score_up:
			mScoreboardData.SetAwayScore(mScoreboardData.GetAwayScore() + 1);
			break;			
		case R.id.away_score_down:
			mScoreboardData.SetAwayScore(mScoreboardData.GetAwayScore() - 1);
			break;
		case R.id.reset_home_score:
			mScoreboardData.SetHomeScore(0);
			break;
		case R.id.reset_away_score:
			mScoreboardData.SetAwayScore(0);
			break;
		case R.id.start_game_clock:
			if (mScoreboardData.IsGameClockRunning()) {
                mScoreboardData.SetGameClockRunning(false);
            }
            else {
                mScoreboardData.SetGameClockRunning(true);
            }
			break;
		case R.id.reset_game_clock:
			mScoreboardData.ResetGameClock();
			mScoreboardData.SetGameClockRunning(false);
            break;
		}
		UpdateScoreboard();
	}

    @Override
    public boolean onLongClick(View v)
    {
        int ViewId = v.getId();
        switch (ViewId) {
            case R.id.game_clock_seconds_value:
            case R.id.game_clock_minutes_value:
                ResetClock(false);
                mScoreboardData.SetGameClockRunning(false);
                break;
            case R.id.reset_game_clock:
                ResetClock(true);
                break;
            case R.id.home_score_value:
                ResetScore(true);
                break;
            case R.id.away_score_value:
                ResetScore(false);
                break;
        }
        return true;
    }

	public static void UpdateScoreboard()
	{
        NumberFormat numberFormat = new DecimalFormat("00");
		if (Integer.parseInt((String) mHomeScore.getText()) != mScoreboardData.GetHomeScore())
		{
		  mHomeScore.setText(String.valueOf(numberFormat.format(mScoreboardData.GetHomeScore())));
		  mHomeScore.startAnimation(mAnimation);
		}
		if (Integer.parseInt((String) mAwayScore.getText()) != mScoreboardData.GetAwayScore())
		{
		  mAwayScore.setText(String.valueOf(numberFormat.format(mScoreboardData.GetAwayScore())));
		  mAwayScore.startAnimation(mAnimation);
		}

		long GameTime = mScoreboardData.GetGameClock();
		long GameTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(GameTime);
		GameTime -= TimeUnit.MINUTES.toMillis(GameTimeMinutes);
		long GameTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(GameTime);

		if (Integer.parseInt((String) mGameClockSeconds.getText()) != GameTimeSeconds)
		{
		  mGameClockSeconds.setText(String.valueOf(numberFormat.format(GameTimeSeconds)));
		  mGameClockSeconds.startAnimation(mAnimation);
		}

		if (Integer.parseInt((String) mGameClockMinutes.getText()) != GameTimeMinutes)
		{
		  mGameClockMinutes.setText(String.valueOf(numberFormat.format(GameTimeMinutes)));
		  mGameClockMinutes.startAnimation(mAnimation);
		}

		if (mScoreboardData.IsGameClockRunning() && mScoreboardData.GetGameClock() != 0)
		{
	      mClockStartStopButton.setText("Stop Clock");
		}
		else
		{
		  mClockStartStopButton.setText("Start Clock");
		}
		Scoreboard.sendScoreboardPacket(getScoreString());
	}

    private static String getScoreString()
    {
        return mHomeScore.getText() + "," + mAwayScore.getText() + "," + mGameClockMinutes.getText() + "," + mGameClockSeconds.getText();


    }

	private void ResetClock(boolean resetPeriodTime)
    {
        String title = "Set Game Time";
        if (resetPeriodTime) {
            title = "Set Period Time";
        }

        Long initialTimePickerValue = mScoreboardData.GetGameClock();
        if (resetPeriodTime) {
            initialTimePickerValue = mScoreboardData.GetPeriodTime();
        }
        DialogFragment dialogFragment =
                GameTimePickerDialog.createInstance(title, initialTimePickerValue, resetPeriodTime);
        dialogFragment.show(mScoreboardActivity.getSupportFragmentManager(), "TimeEditor");
    }

    private void ResetScore(boolean home)
    {
        String title = "Set Away Score";
        NumberPickerDialog.NumberPickerReasons numberPickerReasons=
                NumberPickerDialog.NumberPickerReasons.eSetAwayScore;
        long initialValue = mScoreboardData.GetAwayScore();
        if (home)
        {
            title = "Set Home Score";
            numberPickerReasons = NumberPickerDialog.NumberPickerReasons.eSetHomeScore;
            initialValue = mScoreboardData.GetHomeScore();
        }

        DialogFragment dialogFragment =
                NumberPickerDialog.createInstance(title, "Score", initialValue, numberPickerReasons);
        dialogFragment.show(mScoreboardActivity.getSupportFragmentManager(), "NumberEditor");
    }
    private static ActionBarActivity mScoreboardActivity;
	private static ScoreboardData mScoreboardData;

	private static Animation mAnimation;

	private static TextView mHomeScore;

	private static TextView mAwayScore;
	
	private static Button mClockStartStopButton;
	private static Button mClockResetButton;

	private static TextView mGameClockSeconds;
	private static TextView mGameClockMinutes;
}
