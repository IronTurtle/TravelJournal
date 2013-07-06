package com.souvenir.android;

import java.util.Arrays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.UserSettingsFragment;

public class FacebookLoginFragment extends ParentFragment implements
    OnClickListener
{
  private UserSettingsFragment fragment;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fblogin_fragment_activity, container,
        false);
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    fragment = (UserSettingsFragment) getFragmentManager().findFragmentById(
        R.id.login_fragment);
    if (fragment == null)
    {
      fragment = new UserSettingsFragment();
      fragment.setReadPermissions(Arrays.asList("publish_actions"));
    }
    fragment.setSessionStatusCallback(new Session.StatusCallback()
    {
      Session cur;
      SharedPreferences pref;

      @Override
      public void call(Session session, SessionState state, Exception exception)
      {
        // TODO Auto-generated method stub
        this.cur = session;
        pref.edit().putString("facebook_app_id", session.getApplicationId());
        pref.edit()
            .putString("facebook_access_token", session.getAccessToken());
      }
    });

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
