package com.souvenir.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
//import com.actionbarsherlock.sample.styled.R;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NoteActivity extends SherlockFragmentActivity
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

    NoteFragment mEntryFragment = new NoteFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(R.id.fragment, mEntryFragment);
    fragmentTransaction.commit();
}

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.create_note_menu, menu);
    //System.out.println("Action bar is showing: " + getSupportActionBar().isShowing());
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
  
  
  public void finishNote(String data) {
	  
	  if(data != null) {
	    String id_data = data;

	    Intent resultIntent = new Intent();
	    resultIntent.putExtra("ITINERARY_SELECT", id_data);
	    setResult(Activity.RESULT_OK, resultIntent);
	  }
	  finish();
  }

}