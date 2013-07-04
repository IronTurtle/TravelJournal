package com.souvenir.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

//import com.actionbarsherlock.app.SherlockFragmentActivity;

public class NoteActivity extends SherlockFragmentActivity implements
    NoteFragment.OnHeadlineSelectedListener
{

  ViewPager mViewPager;
  TextView tabCenter;
  TextView tabxt;

  String location = null;
  String generalLocation = null;
  boolean isEditMode = true;

  NoteFragment mNoteFragment;

  @SuppressWarnings("unused")
  private final Handler handler = new Handler();

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fragment_standard);
    if (!ImageLoader.getInstance().isInited())
    {
      ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
          this.getApplicationContext())
          .threadPriority(Thread.NORM_PRIORITY - 2)
          .denyCacheImageMultipleSizesInMemory()/* .enableLogging() */.build();
      ImageLoader.getInstance().init(config);
    }
    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    if (savedInstanceState == null)
    {
      mNoteFragment = new NoteFragment();
      FragmentTransaction fragmentTransaction = getSupportFragmentManager()
          .beginTransaction();
      fragmentTransaction.add(android.R.id.content, mNoteFragment);
      fragmentTransaction.commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.note_menu, menu);
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
  public void onArticleSelected(String location, boolean viewMode)
  {
    this.isEditMode = viewMode;
    PlacesFragment newFragment = new PlacesFragment();
    Bundle args = new Bundle();
    args.putString("PREV_LOC_DATA", location);
    newFragment.setArguments(args);
    FragmentTransaction transaction = getSupportFragmentManager()
        .beginTransaction();
    transaction.replace(android.R.id.content, newFragment);
    transaction.addToBackStack(null);
    transaction.commit();

  }

  public void sendLocationData(String selectedLocation, String generalLocation)
  {
    // ((NoteFragment) getSupportFragmentManager().getBackStackEntryAt(
    // getSupportFragmentManager().getBackStackEntryCount() - 1))
    // .setLocationData(data);
    location = selectedLocation;
    this.generalLocation = generalLocation;
    // mNoteFragment.setLocationData("tester");
    getSupportFragmentManager().popBackStack();
  }
}