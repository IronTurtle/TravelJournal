package com.souvenir.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.evernote.client.android.InvalidAuthenticationException;
import com.facebook.widget.LoginButton;

public class SettingsFragment extends ParentFragment
{
  TextView username;
  LoginButton fbLoginBtn;
  Switch autoSyncSwitch;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    ActionBar abs = getSherlockActivity().getSupportActionBar();
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    SharedPreferences pref = getActivity().getSharedPreferences(
        getActivity().getApplicationContext().getPackageName(),
        Context.MODE_PRIVATE);

    username = (TextView) view.findViewById(R.id.settings_username);
    username.setText(pref.getString("settings_username", null));

    autoSyncSwitch = (Switch) view.findViewById(R.id.settings_switch_autosync);

    autoSyncSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
    {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        ((DrawerActivity) getActivity()).setAutosync(isChecked);
      }

    });

    fbLoginBtn = (LoginButton) view.findViewById(R.id.settings_btn_facebook);
    fbLoginBtn.setOnClickListener(new FacebookOnClickListener());

    return view;
  }

  private void updateUi()
  {
    if (mEvernoteSession.isLoggedIn())
    {
      // mBtnAuth.setText(R.string.label_log_out);
      View b = this.getView().findViewById(R.id.auth_button);
      // b.setVisibility(View.GONE);

      // checkForTravelNotebook();
      // syncCheck();
      // EvernoteSyncService ess = new EvernoteSyncService();
      // ess.getUserAccountInfo();
    }
    else
    {
      View b = this.getView().findViewById(R.id.auth_button);
      // b.setVisibility(View.VISIBLE);
      // mBtnAuth.setText(R.string.label_log_in);
    }
  }

  public void startAuth(View view)
  {
    if (mEvernoteSession.isLoggedIn())
    {
      try
      {
        mEvernoteSession.logOut(this.getActivity().getApplicationContext());
      }
      catch (InvalidAuthenticationException e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      mEvernoteSession.authenticate(this.getActivity());
    }
    updateUi();
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
    updateUi();
  }

  private class FacebookOnClickListener implements Button.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      FacebookLoginFragment fragment = new FacebookLoginFragment();
      FragmentTransaction transaction = getFragmentManager().beginTransaction();
      transaction.replace(R.id.content_frame, fragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

}
