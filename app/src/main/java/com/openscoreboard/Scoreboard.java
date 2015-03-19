package com.openscoreboard;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class Scoreboard extends ActionBarActivity implements GameTimePickerDialog.NumberPickerDialogListener
{
	private ScoreboardData mScoreboardData;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);
        if (savedInstanceState == null) 
        {
           mScoreboardData = new ScoreboardData();
        }
        else
        {
        	mScoreboardData = savedInstanceState.getParcelable("mScoreboardData");
        }
        
        ScoreboardActivity Activity = new ScoreboardActivity(mScoreboardData);
        setContentView(Activity.loadView(this));
    }

    @Override
    public void onDialogPositiveClick(int Minutes, int Seconds, boolean resetPeriodTime) {
        mScoreboardData.SetGameClock(((Minutes * 60) + Seconds) * 1000);

        if (resetPeriodTime) {
            mScoreboardData.SetPeriodTime(((Minutes * 60) + Seconds) * 1000);
        }
        ScoreboardActivity.UpdateScoreboard();
    }

    @Override
    public void onDialogNegativeClick() {
        // User touched the dialog's negative button
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
      savedInstanceState.putParcelable("mScoreboardData", mScoreboardData);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.scoreboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
