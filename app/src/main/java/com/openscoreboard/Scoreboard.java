package com.openscoreboard;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class Scoreboard extends ActionBarActivity implements
        GameTimePickerDialog.NumberPickerDialogListener
{
	private ScoreboardData mScoreboardData;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard);
        if (savedInstanceState == null) 
        {
            mScoreboardData = new ScoreboardData();
        }
        else
        {
            mScoreboardData = savedInstanceState.getParcelable("mScoreboardData");
        }

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mViewPager = (ViewPager) findViewById(R.id.scoreboard);
        mActionBar = getActionBar();
        // Specify that tabs should be displayed in the action bar.
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener()
                {
                    @Override
                    public void onPageSelected(int position)
                    {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        String[] tabs = { "Score Board", "Shot Clock" };

        for (String tab_name : tabs) {
            mActionBar.addTab(mActionBar.newTab().setText(tab_name).setTabListener(tabListener));
        }
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

    public static void sendScoreboardPacket(final String ScoreboardData)
    {
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

    public static void sendShotClockPacket(final String ScoreboardData)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket datagramPacket =
                            new DatagramPacket(ScoreboardData.getBytes(), ScoreboardData.length(), getBroadcastAddress(), 11111);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 1) {
                return new ShotClockActivity(mScoreboardData);
            } else {
                return new ScoreboardActivity(mScoreboardData);
            }
        }


        @Override
        public int getCount() {
            return 2;
        }
    }

    static WifiManager mWifiManager;
    ViewPager mViewPager;
    ActionBar mActionBar;

}
