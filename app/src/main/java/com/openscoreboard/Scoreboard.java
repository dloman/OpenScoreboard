package com.openscoreboard;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class Scoreboard extends ActionBarActivity implements
        GameTimePickerDialog.NumberPickerDialogListener,
        WifiSettingsDialog.WifiSettingsDialogListener,
        AdapterView.OnItemSelectedListener
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
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        setContentView(Activity.loadView(this));
    }

    @Override
    public void onDialogPositiveClick(int Minutes, int Seconds, boolean resetPeriodTime)
    {
        mScoreboardData.SetGameClock(((Minutes * 60) + Seconds) * 1000);

        if (resetPeriodTime)
        {
            mScoreboardData.SetPeriodTime(((Minutes * 60) + Seconds) * 1000);
        }
        ScoreboardActivity.UpdateScoreboard();
    }

    @Override
    public void onDialogNegativeClick()
    {
        // User touched the dialog's negative button
    }

    @Override
    public void onDialogSetWifiSettings(String ssid, String password)
        {

        }

    @Override
    public void onDialogCancelWifiSettings(){
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
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.wifisettings:
                String currentSsid = mWifiManager.getConnectionInfo().getSSID();
                List<ScanResult> wifiScanList = mWifiManager.getScanResults();
                ArrayList<String> ssidList = new ArrayList<String>();
                Set<String> ssidSet = new LinkedHashSet<String>(ssidList);
                ssidList.clear();
                ssidList.addAll(ssidSet);
                for (int i = 0; i < wifiScanList.size(); i++) {
                    ssidList.add(wifiScanList.get(i).SSID);
                }
                ssidList.add("Enter Additional Network");
                DialogFragment dialogFragment =
                        WifiSettingsDialog.createInstance(ssidList, currentSsid);
                dialogFragment.show(this.getSupportFragmentManager(), "TimeEditor");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void sendScoreboardPacket(final String ScoreboardData) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket datagramPacket =
                            new DatagramPacket(ScoreboardData.getBytes(), ScoreboardData.length(), getBroadcastAddress(), 33333);
                    socket.setBroadcast(true);
                    socket.send(datagramPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private static InetAddress getBroadcastAddress() throws IOException
    {
        DhcpInfo dhcp = mWifiManager.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        return InetAddress.getByAddress(quads);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, final View view, int pos, long id)
    {

        int a = 69;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        // Another interface callback
    }

    static WifiManager mWifiManager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

}
