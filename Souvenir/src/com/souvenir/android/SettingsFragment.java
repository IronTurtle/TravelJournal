package com.souvenir.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class SettingsFragment extends ParentFragment implements OnClickListener
{
  TextView username;
  LoginButton fbLoginBtn;
  Switch autoSyncSwitch;
  Button auth_button;

  SharedPreferences pref;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    ActionBar abs = getSherlockActivity().getSupportActionBar();
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    pref = getActivity().getSharedPreferences(
        getActivity().getApplicationContext().getPackageName(),
        Context.MODE_PRIVATE);

    auth_button = (Button) view.findViewById(R.id.auth_button);
    auth_button.setOnClickListener(this);

    username = (TextView) view.findViewById(R.id.settings_username);
    username.setText(pref.getString("settings_username", null));

    autoSyncSwitch = (Switch) view.findViewById(R.id.settings_switch_autosync);
    autoSyncSwitch.setChecked(((DrawerActivity) getActivity()).isAutosync());

    autoSyncSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
    {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        ((DrawerActivity) getActivity()).setAutosync(isChecked);
        saveAutoSyncPref(isChecked);
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
      Button b = (Button) this.getView().findViewById(R.id.auth_button);
      b.setText(R.string.label_log_out);
      // b.setVisibility(View.GONE);

      // checkForTravelNotebook();
      // syncCheck();
      // EvernoteSyncService ess = new EvernoteSyncService();
      // ess.getUserAccountInfo();
    }
    else
    {
      Button b = (Button) this.getView().findViewById(R.id.auth_button);
      b.setText(R.string.label_log_in);
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

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub
    startAuth(v);
  }

  public void saveAutoSyncPref(boolean isChecked)
  {

    pref.edit().putBoolean("autosync", isChecked).commit();
  }

}
