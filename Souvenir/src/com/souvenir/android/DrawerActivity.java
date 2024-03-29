/*
` * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.souvenir.android;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * This example illustrates a common usage of the DrawerLayout widget in the
 * Android support library.
 * <p/>
 * <p>
 * When a navigation (left) drawer is present, the host activity should detect
 * presses of the action bar's Up affordance as a signal to open and close the
 * navigation drawer. The ActionBarDrawerToggle facilitates this behavior. Items
 * within the drawer should fall into one of two categories:
 * </p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic
 * policies as list or tab navigation in that a view switch does not create
 * navigation history. This pattern should only be used at the root activity of
 * a task, leaving some form of Up navigation active for activities further down
 * the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an
 * alternate parent for Up navigation. This allows a user to jump across an
 * app's navigation hierarchy at will. The application should treat this as it
 * treats Up navigation from a different task, replacing the current task stack
 * using TaskStackBuilder or similar. This is the only form of navigation drawer
 * that should be used outside of the root activity of a task.</li>
 * </ul>
 * <p/>
 * <p>
 * Right side drawers should be used for actions, not navigation. This follows
 * the pattern established by the Action Bar that navigation should be to the
 * left and actions to the right. An action should be an operation performed on
 * the current contents of the window, for example enabling or disabling a data
 * overlay on top of the current content.
 * </p>
 */
public class DrawerActivity extends SherlockFragmentActivity
{
  private static final int TRIPS_POSITION = 0;
  private static final int COMPLETED_POSITION = 1;
  private static final int SETTINGS_POSITION = 2;
  boolean autosync = false;

  public boolean isAutosync()
  {
    return autosync;
  }

  public void setAutosync(boolean autosync)
  {
    this.autosync = autosync;
  }

  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;

  private CharSequence mDrawerTitle;
  private CharSequence mTitle;
  private String[] mDrawerTitles;

  SharedPreferences prefs;

  public class aa extends ArrayAdapter<String>
  {

    int resource;
    String[] objects;

    public aa(Context context, int resource, String[] objects)
    {
      super(context, resource, objects);
      this.resource = resource;
      this.objects = objects;
      // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

      if (convertView == null)
      {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resource, null, false);
      }
      else
      {
      }
      TextView tv = (TextView) convertView;

      switch (position)
      {
      case 0:
        tv.setBackgroundColor(getResources().getColor(R.color.primaryRed2));
        break;
      case 1:
        tv.setBackgroundColor(getResources().getColor(R.color.primaryRed3));
        break;
      case 2:
        tv.setBackgroundColor(getResources().getColor(R.color.primaryRed4));
        break;
      }
      tv.setText(objects[position]);
      tv.setHeight(mDrawerLayout.getHeight() / objects.length);
      return convertView;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);

    STrip strip = new STrip("uncategorized");
    strip.setDirty(true);
    strip.insert(this);

    if (!ImageLoader.getInstance().isInited())
    {
      ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
          this.getApplicationContext())
          .threadPriority(Thread.NORM_PRIORITY - 2)
          .denyCacheImageMultipleSizesInMemory()/* .enableLogging() */.build();
      ImageLoader.getInstance().init(config);
    }
    mTitle = mDrawerTitle = getTitle();
    mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerList = (ListView) findViewById(R.id.left_drawer);
    // set a custom shadow that overlays the main content when the drawer opens
    mDrawerLayout
        .setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener
    mDrawerList.setAdapter(new aa(this, R.layout.drawer_list_item,
        mDrawerTitles));
    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    // enable ActionBar app icon to behave as action to toggle nav drawer
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id",
        "android");
    TextView yourTextView = (TextView) findViewById(titleId);
    yourTextView.setTextColor(getResources().getColor(android.R.color.white));

    prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    if (prefs.contains("autosync"))
    {
      autosync = prefs.getBoolean("autosync", false);
    }

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the sliding drawer and the action bar app icon
    mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
    mDrawerLayout, /* DrawerLayout object */
    R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
    R.string.drawer_open, /* "open drawer" description for accessibility */
    R.string.drawer_close /* "close drawer" description for accessibility */
    )
    {
      public void onDrawerClosed(View view)
      {
        // String curTitle = (String) getSupportActionBar().getTitle();
        // if (curTitle.toString().contains(":"))
        // {
        // setTitle(curTitle);
        // }
        // else
        // {
        //
        // getSupportActionBar().setTitle(mTitle);
        // }
        getSupportActionBar().setTitle(mTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }

      public void onDrawerOpened(View drawerView)
      {
        getSupportActionBar().setTitle(mDrawerTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }
    };
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    if (savedInstanceState == null)
    {
      selectItem(COMPLETED_POSITION);
    }
  }

  // @Override
  // public boolean onCreateOptionsMenu(Menu menu)
  // {
  // MenuInflater inflater = getSupportMenuInflater();
  // // inflater.inflate(R.menu.main_menu, menu);
  // return super.onCreateOptionsMenu(menu);
  // }

  /* Called whenever we call invalidateOptionsMenu() */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    // If the nav drawer is open, hide action items related to the content view
    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // The action bar home/up action should open or close the drawer.
    // ActionBarDrawerToggle will take care of this.
    // if (mDrawerToggle.onOptionsItemSelected(item))
    // {
    // return true;
    // }
    if (item.getItemId() == android.R.id.home)
    {
      if (mDrawerLayout.isDrawerOpen(mDrawerList))
      {
        mDrawerLayout.closeDrawer(mDrawerList);
      }
      else
      {
        mDrawerLayout.openDrawer(mDrawerList);
      }
      return true;
    }

    // Handle action buttons
    switch (item.getItemId())
    {
    // case R.id.action_websearch:
    // // create intent to perform web search for this planet
    // Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
    // intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
    // // catch event that there's no activity to handle intent
    // if (intent.resolveActivity(getPackageManager()) != null) {
    // startActivity(intent);
    // } else {
    // Toast.makeText(this, R.string.app_not_available,
    // Toast.LENGTH_LONG).show();
    // }
    // return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  /* The click listner for ListView in the navigation drawer */
  private class DrawerItemClickListener implements ListView.OnItemClickListener
  {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id)
    {
      selectItem(position);
    }
  }

  private void selectItem(int position)
  {
    Fragment fragment = null;

    switch (position)
    {
    // case 0:
    // fragment = new PlanetFragment();
    // Bundle args = new Bundle();
    // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
    // fragment.setArguments(args);
    // break;
    case TRIPS_POSITION:
      fragment = new TripsFragment();
      Bundle args2 = new Bundle();
      // args2.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
      // fragment.setArguments(args2);
      break;
    case COMPLETED_POSITION:
      // update the main content by replacing fragments
      fragment = new SnippetFragment();
      break;
    case SETTINGS_POSITION:
      fragment = new SettingsFragment();
      break;
    }

    // FragmentTransaction transaction = getSupportFragmentManager()
    // .beginTransaction();
    // transaction.replace(R.id.content_frame, fragment);
    // transaction.addToBackStack(null);
    // transaction.commit();

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
        .commit();
    invalidateOptionsMenu();
    // update selected item and title, then close the drawer
    mDrawerList.setItemChecked(position, true);
    setTitle(mDrawerTitles[position]);
    mDrawerLayout.closeDrawer(mDrawerList);

  }

  @Override
  public void setTitle(CharSequence title)
  {
    mTitle = title;
    getSupportActionBar().setTitle(mTitle);
  }

  /**
   * When using the ActionBarDrawerToggle, you must call it during
   * onPostCreate() and onConfigurationChanged()...
   */

  @Override
  protected void onPostCreate(Bundle savedInstanceState)
  {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggls
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  /**
   * Fragment that appears in the "content_frame", shows a planet
   */
  public static class PlanetFragment extends Fragment
  {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    public PlanetFragment()
    {
      // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
      View rootView = inflater.inflate(R.layout.fragment_planet, container,
          false);
      int i = getArguments().getInt(ARG_PLANET_NUMBER);
      String planet = getResources().getStringArray(R.array.drawer_array)[i];

      int imageId = getResources().getIdentifier(
          planet.toLowerCase(Locale.getDefault()), "drawable",
          getActivity().getPackageName());
      ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
      getActivity().setTitle(planet);
      return rootView;
    }
  }

  public interface TripDialogListener
  {
    public void onDialogPositiveClick(DialogFragment dialog);

    public void onDialogNegativeClick(DialogFragment dialog);
  }

  @Override
  public void onBackPressed()
  {
    invalidateOptionsMenu();

    System.out.println("Back Button Pressed");
    System.out.println(getSupportFragmentManager().getBackStackEntryCount());
    if (getSupportFragmentManager().getBackStackEntryCount() > 0)
    {
      getSupportFragmentManager().popBackStack();
    }
    else if (!mDrawerLayout.isDrawerOpen(mDrawerList))
    {
      mDrawerLayout.openDrawer(mDrawerList);
    }
    else
    {
      super.onBackPressed();
    }
    // if (mDrawerLayout.isDrawerOpen(mDrawerList))
    // {
    // super.onBackPressed();
    // }
    // else
    // {
    // mDrawerLayout.openDrawer(mDrawerList);
    // }
  }
}