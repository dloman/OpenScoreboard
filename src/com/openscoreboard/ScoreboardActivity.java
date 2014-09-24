package com.openscoreboard;

import android.app.Activity;
import android.content.Context;
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
		
		mHomeScore = (TextView) ScoreboardView.findViewById(R.id.home_score_value);
		mAwayScore = (TextView) ScoreboardView.findViewById(R.id.away_score_value);
		
		mGameClockDeciSeconds = (TextView) ScoreboardView.findViewById(R.id.game_clock_deci_seconds_value);
		mGameClockSeconds = (TextView) ScoreboardView.findViewById(R.id.game_clock_seconds_value);
		mGameClockMinutes = (TextView) ScoreboardView.findViewById(R.id.game_clock_minutes_value);
		mGameClockDecaMinutes = (TextView) ScoreboardView.findViewById(R.id.game_clock_deca_minutes_value);
		
		UpdateScoreboard();
		
		
		((Button)ScoreboardView.findViewById(R.id.home_score_up)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.home_score_down)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.away_score_up)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.away_score_down)).setOnClickListener((android.view.View.OnClickListener) this);
        ((Button)ScoreboardView.findViewById(R.id.reset_score)).setOnClickListener((android.view.View.OnClickListener) this);
        
        //((Button)ScoreboardView.findViewById(R.id.reset_game_clock)).setOnClickListener(this);
        mClockStartStopButton = ((Button)ScoreboardView.findViewById(R.id.start_game_clock));
        mClockStartStopButton.setOnClickListener((android.view.View.OnClickListener) this);
        
        ((Button)ScoreboardView.findViewById(R.id.reset_game_clock)).setOnClickListener((android.view.View.OnClickListener) this);
        //((Button)ScoreboardView.findViewById(R.id.reset_game_clock)).setOnLongClickListener();
        
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
		case R.id.reset_score:
			mScoreboardData.SetAwayScore(0);
			mScoreboardData.SetHomeScore(0);
			break;
		//case R.id.reset_game_clock:
		//	mScoreboardData.SetGameClock(0.0);
		//	break;
		case R.id.start_game_clock:
			mScoreboardData.SetGameClock(mScoreboardData.GetGameClock() + 1 );
			break;
		case R.id.reset_game_clock:
			mScoreboardData.SetGameClock(0);
            break;
		}
		UpdateScoreboard();
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
		
		int GameTime = mScoreboardData.GetGameClock();
		if (Integer.parseInt((String) mGameClockDeciSeconds.getText()) != GameTime % 10)
		{
		  mGameClockDeciSeconds.setText(String.valueOf(GameTime % 10));
		  mGameClockDeciSeconds.startAnimation(mAnimation);
		}
		
		GameTime/=10;
		if (Integer.parseInt((String) mGameClockSeconds.getText()) != GameTime % 10)
		{
	      mGameClockSeconds.setText(String.valueOf(GameTime % 10));
		  mGameClockSeconds.startAnimation(mAnimation);
		}
		GameTime/=10;
		if (Integer.parseInt((String) mGameClockMinutes.getText()) != GameTime % 10)
		{
		  mGameClockMinutes.setText(String.valueOf(GameTime % 10));
		  mGameClockMinutes.startAnimation(mAnimation);
		}
		GameTime/=10;
		if (Integer.parseInt((String) mGameClockDecaMinutes.getText()) != GameTime % 10)
		{
		  mGameClockDecaMinutes.setText(String.valueOf(GameTime % 10));
		  mGameClockDecaMinutes.startAnimation(mAnimation);
		}
	}

	private ScoreboardData mScoreboardData;
	
	private Animation mAnimation;
	
	private TextView mHomeScore;
	
	private TextView mAwayScore;
	
	private Button mClockStartStopButton;
	
	private TextView mGameClockDeciSeconds;
	private TextView mGameClockSeconds;
	private TextView mGameClockMinutes;
	private TextView mGameClockDecaMinutes;

}
