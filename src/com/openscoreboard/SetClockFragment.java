package com.openscoreboard;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SetClockFragment extends DialogFragment 
{
	@Override
	public View onCreateView(
	  LayoutInflater inflater, 
	  ViewGroup container, 
	  Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.set_clock_fragment, container, false);
	}
	

}
