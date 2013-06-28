package com.souvenir.android;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.widget.UserSettingsFragment;

public class FacebookLoginFragment extends ParentFragment implements
    OnClickListener
{
  private UserSettingsFragment userSettingsFragment;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fblogin_fragment_activity, container, false);
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    userSettingsFragment = (UserSettingsFragment) getFragmentManager()
        .findFragmentById(R.id.login_fragment);
    if (userSettingsFragment == null)
    {
      userSettingsFragment = new UserSettingsFragment();
      userSettingsFragment.setReadPermissions(Arrays.asList("publish_actions"));
    }
    transaction.commit();
    /*
     * if (getLastNonConfigurationInstance() != null) { mImageData = (ImageData)
     * getLastNonConfigurationInstance();
     * mImageView.setImageBitmap(mImageData.imageBitmap); }
     */
    return view;
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.main);
  }

  @Override
  public void onResume()
  {
    super.onResume();
  }

  /*
   * @Override public Object onRetainNonConfigurationInstance() { return
   * mImageData; }
   */

  /**
   * Called when the control returns from an activity that we launched.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode)
    {
    }
  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub

  }

}
