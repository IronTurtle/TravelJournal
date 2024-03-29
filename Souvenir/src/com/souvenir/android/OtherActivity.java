package com.souvenir.android;

import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.facebook.widget.UserSettingsFragment;

public class OtherActivity extends SherlockFragmentActivity
{

  private UserSettingsFragment userSettingsFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // TODO: Currently fb activity & fragment. Refactor name later
    setContentView(R.layout.fblogin_fragment_activity);

    // Facebook fragment
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    userSettingsFragment = (UserSettingsFragment) fragmentManager
        .findFragmentById(R.id.login_fragment);
    if (userSettingsFragment == null)
    {
      userSettingsFragment = new UserSettingsFragment();
      transaction.add(R.id.login_fragment, userSettingsFragment);
      userSettingsFragment.setReadPermissions(Arrays.asList("publish_actions"));
    }

    transaction.commit();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    // getSupportMenuInflater().inflate(R.menu.create_note_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

}
