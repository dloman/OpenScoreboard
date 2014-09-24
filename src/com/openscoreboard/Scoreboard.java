package com.openscoreboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class Scoreboard extends ActionBarActivity 
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
        	mScoreboardData = 
        	  (ScoreboardData) savedInstanceState.getParcelable("mScoreboardData");
        }
        
        ScoreboardActivity Activity = new ScoreboardActivity(mScoreboardData);
        setContentView(Activity.loadView(this));
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment 
    {

        public PlaceholderFragment() 
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.scoreboard_layout, container, false);
            return rootView;
        }
    }   
}
