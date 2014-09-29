package com.openscoreboard;

import android.os.Parcel;
import android.os.Parcelable;

public class ScoreboardData implements Parcelable
{
	public ScoreboardData()
	{
		mHomeScore = 0;
		mAwayScore = 0;
		mGameClock = 0;
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
		return mGameClock;
	}
	
	public void SetGameClock(long GameClock)
	{
		this.mGameClock = GameClock;
	}

	public boolean IsClockRunning()
	{
		return mIsClockRunning;
	}
	
	public void SetClockRunning(Boolean ClockBool)
	{
		mIsClockRunning = ClockBool;
	}
	
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
		arg0.writeValue(mIsClockRunning);
	}
	

	private int mHomeScore;
	private int mAwayScore;
	
	private long mGameClock; //will probably change to a timer in the future
	
	private boolean mIsClockRunning;
	
}