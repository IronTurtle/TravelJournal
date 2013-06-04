package com.souvenir.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class OtherFragment extends ParentFragment implements OnClickListener
{

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_other, container, false);

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
