package com.openscoreboard;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.util.List;


public class Scoreboard extends ActionBarActivity implements
        GameTimePickerDialog.GameTimePickerDialogListener,
        NumberPickerDialog.NumberPickerDialogListener,
        WifiSettingsDialog.WifiSettingsDialogListener
{

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
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

    mViewPager = (ViewPager) findViewById(R.id.scoreboard);
    mActionBar = getActionBar();
    // Specify that tabs should be displayed in the action bar.
    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

    ActionBar.TabListener tabListener = new ActionBar.TabListener()
    {
      //------------------------------------------------------------------------
      //------------------------------------------------------------------------
      public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
      {
        // When the tab is selected, switch to the
        // corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
      }

      //------------------------------------------------------------------------
      //------------------------------------------------------------------------
      @Override
      public void onTabUnselected(
        ActionBar.Tab tab,
        FragmentTransaction fragmentTransaction)
      {
      }

      @Override
      public void onTabReselected(
          ActionBar.Tab tab,
          FragmentTransaction fragmentTransaction)
      {
      }
    };

    mViewPager.setOnPageChangeListener(
        new ViewPager.SimpleOnPageChangeListener()
        {
          //--------------------------------------------------------------------
          //--------------------------------------------------------------------
          @Override
          public void onPageSelected(int position)
          {
            // When swiping between pages, select the
            // corresponding tab.
            getActionBar().setSelectedNavigationItem(position);
          }
        });

    String[] tabs = { "Score Board", "Shot Clock" };

    for (String tabName : tabs)
    {
      mActionBar.addTab(
          mActionBar.newTab().setText(tabName).setTabListener(tabListener));
    }

    mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (mWifiManager != null)
    {
      mWifiLock =
        mWifiManager.createWifiLock(
            WifiManager.WIFI_MODE_FULL_HIGH_PERF,
            "ScoreboardWifiLock");
      mWifiLock.acquire();
      mMulticastLock =
        mWifiManager.createMulticastLock("ScoreboardMultiCastLock");
    }
    mUpdaterThread =
      new Thread(new Runnable()
      {
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        @Override
        public void run()
        {
          while (true)
          {
            try
            {
              Scoreboard.sendPacket(
                ScoreboardActivity.getScoreString(),
                Scoreboard.PacketType.eScoreboard);
              SystemClock.sleep(500);
            }
            catch (Exception e)
            {
              e.printStackTrace();
              int a = 69;
            }
          }
        }
    });
    mUpdaterThread.start();

  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  protected void onResume()
  {
    super.onResume();
    mWifiLock.acquire();
    mMulticastLock.acquire();
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  protected  void onDestroy()
  {
    super.onDestroy();
    if (mWifiLock.isHeld())
    {
      mWifiLock.release();
    }
    if (mMulticastLock.isHeld())
    {
      mMulticastLock.release();
    }

  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  protected void onPause()
  {
    super.onPause();
    if (mWifiLock.isHeld())
    {
      mWifiLock.release();
    }
    if (mMulticastLock.isHeld())
    {
      mMulticastLock.release();
    }
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  public void onDialogPositiveClick(
    int Minutes,
    int Seconds,
    boolean resetPeriodTime)
  {
    mScoreboardData.SetGameClock(((Minutes * 60) + Seconds) * 1000);

    if (resetPeriodTime)
    {
      mScoreboardData.SetPeriodTime(((Minutes * 60) + Seconds) * 1000);
    }
    ScoreboardActivity.UpdateScoreboard();
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  public void onDialogPositiveClick(
    int value,
    NumberPickerDialog.NumberPickerReasons reason)
  {
    switch (reason)
    {
      case eSetDefaultShotClock:
        mScoreboardData.SetDefaultShotClockTime(value * 1000);
        ShotClockActivity.UpdateShotClock();
        break;
      case eSetCurrentShotClock:
        mScoreboardData.SetShotClock(value * 1000);
        ShotClockActivity.UpdateShotClock();
        break;
      case eSetAwayScore:
        mScoreboardData.SetAwayScore(value);
        ScoreboardActivity.UpdateScoreboard();
        break;
      case eSetHomeScore:
        mScoreboardData.SetHomeScore(value);
        ScoreboardActivity.UpdateScoreboard();
        break;
    }
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  public void onDialogNegativeClick()
  {
    // User touched the dialog's negative button
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    savedInstanceState.putParcelable("mScoreboardData", mScoreboardData);
  }


  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.settings, menu);
    return true;
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
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
        for (int i = 0; i < wifiScanList.size(); i++)
        {
          if (!ssidList.contains(wifiScanList.get(i).SSID))
          {
            ssidList.add(wifiScanList.get(i).SSID);
          }
        }
        ssidList.add("Enter Additional Network");
        DialogFragment dialogFragment =
          WifiSettingsDialog.createInstance(ssidList, currentSsid);
        dialogFragment.show(getSupportFragmentManager(), "WifiSettings");
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public static void sendPacket(final String data, PacketType packetType)
  {
    final int port = packetType.portNumber;

    Thread thread =
      new Thread(new Runnable()
      {
        private DatagramSocket mSocket;
        private DatagramPacket mDatagramPacket;

        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        @Override
        public void run()
        {
          try
          {
            mSocket = new DatagramSocket();
            mDatagramPacket = new DatagramPacket(
              data.getBytes(),
              data.length(),
              getBroadcastAddress(),
              port);

            mSocket.setBroadcast(true);
            mSocket.send(mDatagramPacket);
            SystemClock.sleep(15);
            mSocket.send(mDatagramPacket);
            mSocket.setBroadcast(false);
            for (int k = 0; k< 6; k++)
            {
              for (int i =2; i < 5; i ++)
            {
                mDatagramPacket.setAddress(
                  InetAddress.getByName("192.168.4." + Integer.toString(i)));
                mSocket.send(mDatagramPacket);
                SystemClock.sleep(15);
                mSocket.send(mDatagramPacket);
                SystemClock.sleep(15);
              }
            }
            mSocket.close();
          }
          catch (Exception e)
          {
            e.printStackTrace();
          int a = 56;
          }
        }
    });
    thread.start();
  }

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  private static InetAddress getBroadcastAddress() throws IOException
  {
    DhcpInfo dhcp = mWifiManager.getDhcpInfo();
    // handle null somehow

    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
    byte[] quads = new byte[4];
    for (int k = 0; k < 4; k++)
    {
      quads[k] = (byte) (broadcast >> (k * 8));
    }
    return InetAddress.getByAddress(quads);
  }

  @Override
  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public void onDialogSetWifiSettings(String ssid, String password)
  {
    String data = ssid + '|' + password;
    sendPacket(data, PacketType.eWifiSettings );
  }

  @Override
  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public void onDialogCancelWifiSettings() {}

  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  public class SectionsPagerAdapter extends FragmentPagerAdapter
  {

    public SectionsPagerAdapter(FragmentManager fm)
    {
      super(fm);
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    @Override
    public Fragment getItem(int position)
    {
      Bundle bundle = new Bundle();
      bundle.putParcelable("ScoreboardData", mScoreboardData);
      if (position == 1) {
        ShotClockActivity shotClockActivity = new ShotClockActivity();
        shotClockActivity.setArguments(bundle);
        return shotClockActivity;
      } else {
        ScoreboardActivity scoreboardActivity = new ScoreboardActivity();
        scoreboardActivity.setArguments(bundle);
        return scoreboardActivity;
      }
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    @Override
    public int getCount()
    {
      return 2;
    }
  }


  private ScoreboardData mScoreboardData;
  private Thread mUpdaterThread;
  private static WifiManager mWifiManager;
  private WifiManager.MulticastLock mMulticastLock;
  private WifiManager.WifiLock mWifiLock;
  private ViewPager mViewPager;
  private ActionBar mActionBar;
  public enum PacketType {
    eScoreboard (33333),
    eShotClock (11111),
    eWifiSettings (22222);

    private int portNumber;

    PacketType(int portNumber) {
      this.portNumber = portNumber;
    }
  }
}
