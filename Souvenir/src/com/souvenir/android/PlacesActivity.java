package com.souvenir.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;

public class PlacesActivity extends SherlockFragmentActivity
{

  private String loc_data;

  @SuppressWarnings("unused")
  private final Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fragment_standard);
    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    PlacesFragment mPlacesFragment = new PlacesFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(R.id.fragment, mPlacesFragment);
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

  public void setLocationData(String data)
  {
    loc_data = data;

    Intent resultIntent = new Intent();
    resultIntent.putExtra("LOCATION_DATA", loc_data);
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }
}
