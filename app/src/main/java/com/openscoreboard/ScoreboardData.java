package com.openscoreboard;

import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

public class ScoreboardData implements Parcelable
{
  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public ScoreboardData()
	{
		mHomeScore = 0;
		mAwayScore = 0;
		mGameClock = 0;
		mShotClock = 0;
		mDefaultShotClockTime = 35000;
        mPeriodTime = 300000;
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public int GetHomeScore()
	{
		return mHomeScore;
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
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

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public int GetAwayScore()
	{
		return mAwayScore;
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
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

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public long GetGameClock() { return mGameClock; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void SetGameClock(long GameClock)
  {
    mGameClock = GameClock;
    if (mGameTimer != null)
    {
      mGameTimer.cancel();
    }
    mGameTimer = new CountDownTimer(mGameClock, 100)
    {
      public void onTick(long millisUntilTimerFinished)
      {
        mGameClock = millisUntilTimerFinished;
        ScoreboardActivity.UpdateScoreboard();
      }

      public void onFinish()
      {
        mGameClock = 0;
        mIsGameClockRunning = false;
        ScoreboardActivity.UpdateScoreboard();
      }
    };

    if (IsGameClockRunning())
    {
      mGameTimer.start();
    }
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public boolean IsGameClockRunning() { return mIsGameClockRunning; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public void SetGameClockRunning(Boolean ClockBool)
  {
    if ((mGameTimer == null) || (mGameClock == 0))
    {
      ResetGameClock();
    }

    if (ClockBool)
    {
      mIsGameClockRunning = true;
      mGameTimer.start();
    }
    else
    {
      mIsGameClockRunning = false;
      SetGameClock(mGameClock);
    }
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void ResetGameClock()
	{
		SetGameClock(mPeriodTime);
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void SetPeriodTime(long PeriodTime){ this.mPeriodTime = PeriodTime; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public long GetPeriodTime() { return mPeriodTime; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public long GetShotClock() { return mShotClock; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void SetShotClock(long ShotClock)
  {
		mShotClock = ShotClock;
		if (mShotClockTimer != null)
		{
			mShotClockTimer.cancel();
		}
		mShotClockTimer = new CountDownTimer(mShotClock, 100)
    {
			public void onTick(long millisUntilTimerFinished)
      {
				mShotClock = millisUntilTimerFinished;
				ShotClockActivity.UpdateShotClock();
			}

			public void onFinish()
      {
				mShotClock = 0;
				mIsShotClockRunning = false;
				ShotClockActivity.UpdateShotClock();
			}
		};

		if (IsShotClockRunning())
    {
			mShotClockTimer.start();
		}
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public boolean IsShotClockRunning() { return mIsShotClockRunning; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void SetShotClockRunning(Boolean ClockBool)
	{

		if ((mShotClockTimer == null) || (mShotClock == 0)) {
			ResetShotClock();
		}

		if (ClockBool) {
			mIsShotClockRunning = true;
			mShotClockTimer.start();
		}
		else {
			mIsShotClockRunning = false;
			SetShotClock(mShotClock);
		}
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void ResetShotClock()
	{
		SetShotClock(mDefaultShotClockTime + 999);
	}


  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public void SetDefaultShotClockTime(long DefaultShotClockTime)
	{
		mDefaultShotClockTime = DefaultShotClockTime;
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public long GetDefaultShotClockTime() { return mDefaultShotClockTime; }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public void SetQuarter(int Quarter)
  {
    if (Quarter >= 0)
    {
      mQuarter = Quarter;
    }
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public int GetQuarter() { return mQuarter;}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	@Override
	public void writeToParcel(Parcel arg0, int arg1)
	{
		arg0.writeInt(mHomeScore);
		arg0.writeInt(mAwayScore);
        arg0.writeInt(mQuarter);
		arg0.writeLong(mGameClock);
		arg0.writeLong(mShotClock);
        arg0.writeLong(mPeriodTime);
		arg0.writeLong(mDefaultShotClockTime);
		arg0.writeValue(mIsGameClockRunning);
		arg0.writeValue(mIsShotClockRunning);
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	public static final Parcelable.Creator<ScoreboardData> CREATOR =
    new Parcelable.Creator<ScoreboardData>()
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

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
	private ScoreboardData(Parcel arg0)
	{
		mHomeScore = arg0.readInt();
		mAwayScore = arg0.readInt();
        mQuarter = arg0.readInt();
		mGameClock = arg0.readLong();
		mShotClock = arg0.readLong();
        mPeriodTime = arg0.readLong();
		mDefaultShotClockTime = arg0.readLong();
		mIsGameClockRunning = (Boolean) arg0.readValue(null);
		mIsShotClockRunning = (Boolean) arg0.readValue(null);
	}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  private void makeRequest(final String url, final String field)
  {
    Thread requestThread =
      new Thread(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 2000);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 3000);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
          }
          catch (Exception e)
          {
            int a = 69;
          }
        }
      });
    requestThread.start();
  }

  private int mHomeScore;
  private int mAwayScore;
  private int mQuarter;

  private long mGameClock;
  private long mPeriodTime;
  private long mShotClock;
  private long mDefaultShotClockTime;

  private boolean mIsGameClockRunning;
  private boolean mIsShotClockRunning;
  private CountDownTimer mGameTimer;
  private CountDownTimer mShotClockTimer;
}
