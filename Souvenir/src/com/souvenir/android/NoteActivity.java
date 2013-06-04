package com.souvenir.android;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

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