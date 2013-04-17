package com.evernote.android.sample;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
//import com.actionbarsherlock.sample.styled.R;
import com.actionbarsherlock.view.Menu;

public class EntryActivity extends SherlockFragmentActivity
{

  ViewPager mViewPager;
  TextView tabCenter;
  TextView tabxt;

  @SuppressWarnings("unused")
  private final Handler handler = new Handler();

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    // System.out.println("Launched");
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fragment_standard);
    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    EntryFragment mEntryFragment = new EntryFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(R.id.fragment, mEntryFragment);
    fragmentTransaction.commit();
}

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.main_menu, menu);
    /*
     * // set up a listener for the refresh item final MenuItem refresh =
     * (MenuItem) menu.findItem(R.id.menu_refresh);
     * refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() { // on
     * selecting show progress spinner for 1s public boolean
     * onMenuItemClick(MenuItem item) { //
     * item.setActionView(R.layout.progress_action); handler.postDelayed(new
     * Runnable() { public void run() { refresh.setActionView(null); } }, 1000);
     * return false; } });
     */
    return super.onCreateOptionsMenu(menu);
  }

}