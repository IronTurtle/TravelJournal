package com.souvenir.android;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.widget.LoginButton;

public class SettingsFragment extends ParentFragment
{
  LoginButton fbLoginBtn;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    ActionBar abs = getSherlockActivity().getSupportActionBar();
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    fbLoginBtn = (LoginButton) view.findViewById(R.id.settings_btn_facebook);
    fbLoginBtn.setOnClickListener(new FacebookOnClickListener());

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    // inflater.inflate(R.menu.trips_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume()
  {
    super.onResume();
  }

  private class FacebookOnClickListener implements Button.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      FacebookLoginFragment fragment = new FacebookLoginFragment();
      FragmentManager fragmentManager = getFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
          .commit();
    }
  }

}
