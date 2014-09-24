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
		this.mHomeScore = HomeScore;
	}

	public int GetAwayScore()
	{
		return mAwayScore;
	}
	
	public void SetAwayScore(int AwayScore)
	{
		this.mAwayScore = AwayScore;
	}
	
	public int GetGameClock()
	{
		return mGameClock;
	}
	
	public void SetGameClock(int GameClock)
	{
		this.mGameClock = GameClock;
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
		arg0.writeInt(mGameClock);
	}

	private int mHomeScore;
	private int mAwayScore;
	
	private int mGameClock; //will probably change to a timer in the future
	
}