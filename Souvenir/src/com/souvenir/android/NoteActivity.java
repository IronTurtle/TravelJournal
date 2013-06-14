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

public class NoteActivity extends SherlockFragmentActivity implements
    NoteFragment.OnHeadlineSelectedListener
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
    fragmentTransaction.add(android.R.id.content, mEntryFragment);
    fragmentTransaction.commit();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.create_note_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  public void finishNote(String data)
  {

    if (data != null)
    {
      String id_data = data;

      Intent resultIntent = new Intent();
      resultIntent.putExtra("ITINERARY_SELECT", id_data);
      setResult(Activity.RESULT_OK, resultIntent);
    }
    finish();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy(); // Always call the superclass

    // Stop method tracing that the activity started during onCreate()
    android.os.Debug.stopMethodTracing();
  }

  @Override
  public void onArticleSelected(String location)
  {
    PlacesFragment newFragment = new PlacesFragment();
    Bundle args = new Bundle();
    args.putString("PREV_LOC_DATA", location);
    newFragment.setArguments(args);
    FragmentTransaction transaction = getSupportFragmentManager()
        .beginTransaction();

    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
    transaction.replace(android.R.id.content, newFragment);
    transaction.addToBackStack(null);
    transaction.commit();

  }
}