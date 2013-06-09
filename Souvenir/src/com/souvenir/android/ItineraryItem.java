package com.souvenir.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class ItineraryItem extends SherlockFragmentActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fragment_standard);
    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    ItineraryItemFragment mItineraryItemFragment = new ItineraryItemFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(R.id.fragment, mItineraryItemFragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.itinerary_item_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  public void createNewItineraryItem(String itemTitle, boolean itemIsTrip,
      String itemType, String itemLocation, String itemStartDate,
      String itemEndDate, String itemStartTime, String itemEndTime,
      double itemDistance)
  {
    Intent resultIntent = new Intent();
    resultIntent.putExtra("ITINERARY_TITLE", itemTitle);
    resultIntent.putExtra("ITINERARY_ISTRIP", itemIsTrip);
    resultIntent.putExtra("ITINERARY_TYPE", itemType);
    resultIntent.putExtra("ITINERARY_LOCATION", itemLocation);
    resultIntent.putExtra("ITINERARY_STARTDATE", itemStartDate);
    resultIntent.putExtra("ITINERARY_ENDDATE", itemEndDate);
    resultIntent.putExtra("ITINERARY_STARTTIME", itemStartTime);
    resultIntent.putExtra("ITINERARY_ENDTIME", itemEndTime);
    resultIntent.putExtra("ITINERARY_DISTANCE", itemDistance);
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }

}
