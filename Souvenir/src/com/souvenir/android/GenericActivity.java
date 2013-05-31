package com.souvenir.android;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class GenericActivity extends SherlockFragmentActivity
{
  ViewPager mViewPager;
  TextView tabCenter;
  TextView tabxt;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    Object mNoteFragment = null;
    try
    {
      mNoteFragment = Class.forName(getIntent().getStringExtra("fragment"))
          .newInstance();
      Toast.makeText(this, "Worked", Toast.LENGTH_SHORT).show();
    }
    catch (InstantiationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    setContentView(R.layout.fragment_standard);

    NoteFragment mNoteFragment2 = new NoteFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(R.id.fragment, (NoteFragment) mNoteFragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.create_note_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

}