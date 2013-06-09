package com.souvenir.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;

public class ShareActivity extends SherlockFragmentActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fragment_standard);

    ShareFragment mShareFragment = new ShareFragment();
    FragmentTransaction transaction = getSupportFragmentManager()
        .beginTransaction();
    transaction.add(R.id.fragment, mShareFragment);
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
