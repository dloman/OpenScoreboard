package com.openscoreboard;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class ScoreboardActivity extends Activity implements OnClickListener
{
	ScoreboardActivity(ScoreboardData ScoreboardData)
	{
		this.mScoreboardData = ScoreboardData;
	}


	public View loadView(Context context)
	{
		View ScoreboardView = View.inflate(context, R.layout.scoreboard_layout, null);

		mAnimation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_top);
		//mAnimation.restrictDuration(100);
		mAnimation.setDuration(200);

		mHomeScore = (TextView) ScoreboardView.findViewById(R.id.home_score_value);
		mAwayScore = (TextView) ScoreboardView.findViewById(R.id.away_score_value);

		mGameClockSeconds = (TextView) ScoreboardView.findViewById(R.id.game_clock_seconds_value);
		mGameClockMinutes = (TextView) ScoreboardView.findViewById(R.id.game_clock_minutes_value);

		((Button)ScoreboardView.findViewById(R.id.home_score_up)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.home_score_down)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.away_score_up)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.away_score_down)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.reset_home_score)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.reset_away_score)).setOnClickListener((android.view.View.OnClickListener) this);

        //((Button)ScoreboardView.findViewById(R.id.reset_game_clock)).setOnClickListener(this);
        mClockStartStopButton = ((Button)ScoreboardView.findViewById(R.id.start_game_clock));
        mClockStartStopButton.setOnClickListener((android.view.View.OnClickListener) this);


        mClockResetButton = ((Button)ScoreboardView.findViewById(R.id.reset_game_clock));
        mClockResetButton.setOnClickListener((android.view.View.OnClickListener) this);
        mClockResetButton.setOnLongClickListener(ResetClockLongClickListener);
        //((Button)ScoreboardView.findViewById(R.id.reset_game_clock)).setOnLongClickListener();
        UpdateScoreboard();

        return ScoreboardView;
	}

	@Override
	public void onClick(View v)
	{
		int ViewId = ((Button)v).getId();
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
			if (!mScoreboardData.IsClockRunning())
			{
				StartTimer();
			}
			else
			{
				mGameTimer.cancel();
				mScoreboardData.SetClockRunning(false);
			}
			break;
		case R.id.reset_game_clock:
			mScoreboardData.SetGameClock(300000);
			mScoreboardData.SetClockRunning(false);
			if (mGameTimer != null)
			{
				mGameTimer.cancel();
			}
            break;
		}
		UpdateScoreboard();
	}

	private void StartTimer()
	{
		mGameTimer = new CountDownTimer(mScoreboardData.GetGameClock(), 1000)

		  {
			public void onTick(long millisecondsUntilTimerFinished)
			{
				mScoreboardData.SetGameClock(millisecondsUntilTimerFinished);
				UpdateScoreboard();
			}

			public void onFinish()
			{
				mScoreboardData.SetGameClock(0);
				UpdateScoreboard();
			}
		  }.start();
		mScoreboardData.SetClockRunning(true);
	}


	private void UpdateScoreboard()
	{
		if (Integer.parseInt((String) mHomeScore.getText()) != mScoreboardData.GetHomeScore())
		{
		  mHomeScore.setText(String.valueOf(mScoreboardData.GetHomeScore()));
		  mHomeScore.startAnimation(mAnimation);
		}
		if (Integer.parseInt((String) mAwayScore.getText()) != mScoreboardData.GetAwayScore())
		{
		  mAwayScore.setText(String.valueOf(mScoreboardData.GetAwayScore()));
		  mAwayScore.setAnimation(mAnimation);
		}
		NumberFormat numberFormat = new DecimalFormat("00");

		long GameTimeMilliseconds = mScoreboardData.GetGameClock();
		long GameTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(GameTimeMilliseconds);
		GameTimeMilliseconds -= TimeUnit.MINUTES.toMillis(GameTimeMinutes);
		long GameTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(GameTimeMilliseconds);

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

		if (mScoreboardData.IsClockRunning() && mClockStartStopButton.getText() != "Stop Clock")
		{
	      mClockStartStopButton.setText("Stop Clock");
		}
		else if (!mScoreboardData.IsClockRunning() && mClockStartStopButton.getText() == "Stop Clock")
		{
		  mClockStartStopButton.setText("Start Clock");
		}
	}

	private View.OnLongClickListener ResetClockLongClickListener = new View.OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View v)
		{
			LaunchFragment();
			return false;
		}
	};

    private void LaunchFragment()
    {
    	try
    	{
    	  //SetClockFragment fragment = new SetClockFragment();
    	  //FragmentManager fragmentManager = getFragmentManager();
    	  //fragment.setRetainInstance(false);
          //fragment.show(fragmentManager, "Get Clock");
    	}
    	catch (Exception e)
    	{
    		int a = 5;
    	}
    }

	private ScoreboardData mScoreboardData;

	private Animation mAnimation;

	private TextView mHomeScore;

	private TextView mAwayScore;

	private Button mClockStartStopButton;
	private Button mClockResetButton;

	private TextView mGameClockSeconds;
	private TextView mGameClockMinutes;

	private CountDownTimer mGameTimer;
}
