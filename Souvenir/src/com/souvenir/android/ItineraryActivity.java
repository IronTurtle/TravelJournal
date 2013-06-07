package com.souvenir.android;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class ItineraryActivity extends SherlockFragmentActivity
{

	private boolean refresh = true;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.fragment_standard);
	  final ActionBar bar = getSupportActionBar();
	  bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	  
	  ItineraryFragment mItineraryFragment = new ItineraryFragment();
	  FragmentTransaction fragmentTransaction = getSupportFragmentManager()
	      .beginTransaction();
	  fragmentTransaction.replace(R.id.fragment, mItineraryFragment);
	  fragmentTransaction.commit();
	 
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu)
	  {
	    getSupportMenuInflater().inflate(R.menu.itinerary_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	  }
	 
	 public boolean getRefresh()
	 {
		 boolean returnVal = refresh;
		 refresh = false;
		 return returnVal;
	 }
}
