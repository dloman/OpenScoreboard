package com.openscoreboard;

import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

public class ScoreboardData implements Parcelable
{
	public ScoreboardData()
	{
		mHomeScore = 0;
		mAwayScore = 0;
		mGameClock = 0;
        mPeriodTime = 300000;
	}
	
	public int GetHomeScore()
	{
		return mHomeScore;
	}
	
	public void SetHomeScore(int HomeScore)
	{
		if(HomeScore < 0 || HomeScore > 99 )
		{
			this.mHomeScore = 0;
			
		}
		else
		{
			this.mHomeScore = HomeScore;
		}
	}

	public int GetAwayScore()
	{
		return mAwayScore;
	}
	
	public void SetAwayScore(int AwayScore)
	{

        if(AwayScore < 0 || AwayScore > 99 )
        {
            this.mAwayScore = 0;

        }
        else
        {
            this.mAwayScore = AwayScore;
        }
	}
	
	public long GetGameClock()
	{
		return this.mGameClock;
	}
	
	public void SetGameClock(long GameClock) {
        mGameClock = GameClock;
        if (mGameTimer != null) {
            mGameTimer.cancel();
        }
        mGameTimer = new CountDownTimer(mGameClock, 100) {
            public void onTick(long secondsUntilTimerFinished) {
                mGameClock = secondsUntilTimerFinished;
                ScoreboardActivity.UpdateScoreboard();
            }

            public void onFinish() {
                mGameClock = 0;
                ScoreboardActivity.UpdateScoreboard();
            }
        };

        if (IsClockRunning()) {
            mGameTimer.start();
        }
    }

	public boolean IsClockRunning()
	{
		return mIsClockRunning;
	}
	
	public void SetClockRunning(Boolean ClockBool)
	{
		if (ClockBool) {
            mIsClockRunning = true;
            mGameTimer.start();
        }
        else {
            mIsClockRunning = false;
            SetGameClock(mGameClock);
        }
	}

    public void ResetGameClock()
    {
        SetGameClock(mPeriodTime);
    }
	
	public void SetPeriodTime(long PeriodTime){ this.mPeriodTime = PeriodTime; }

    public long GetPeriodTime() { return mPeriodTime; }

    @Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) 
	{
		arg0.writeInt(mHomeScore);
		arg0.writeInt(mAwayScore);
		arg0.writeLong(mGameClock);
        arg0.writeLong(mPeriodTime);
		arg0.writeValue(mIsClockRunning);
	}

	public static final Parcelable.Creator<ScoreboardData> CREATOR
       = new Parcelable.Creator<ScoreboardData>() 
     {
		public ScoreboardData createFromParcel(Parcel in) 
		{
			return new ScoreboardData(in);
		}

		public ScoreboardData[] newArray(int size) 
		{
			return new ScoreboardData[size];
		}
     };

	private ScoreboardData(Parcel arg0) 
	{
		mHomeScore = arg0.readInt();
		mAwayScore = arg0.readInt();
		mGameClock = arg0.readLong();
        mPeriodTime = arg0.readLong();
		mIsClockRunning = (Boolean) arg0.readValue(null);
	}

	private int mHomeScore;
	private int mAwayScore;
	
	private long mGameClock;
	private long mPeriodTime;

	private boolean mIsClockRunning;
    private CountDownTimer mGameTimer;
}