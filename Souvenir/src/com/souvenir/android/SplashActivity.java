package com.souvenir.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SplashActivity extends Activity
{

  SharedPreferences pref;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);

    pref = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    String access_token = pref.getString("facebook_access_token", null);

    if (!ImageLoader.getInstance().isInited())
    {
      ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
          this.getApplicationContext())
          .threadPriority(Thread.NORM_PRIORITY - 2)
          .denyCacheImageMultipleSizesInMemory()/* .enableLogging() */.build();
      ImageLoader.getInstance().init(config);
    }

    Thread splashThread = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          int waited = 0;
          while (waited < 500)
          {
            sleep(100);
            waited += 100;
          }
        }
        catch (InterruptedException e)
        {
          // do nothing
        }
        finally
        {
          finish();
          Intent i = new Intent(SplashActivity.this, DrawerActivity.class);

          startActivity(i);
        }
      }
    };
    splashThread.start();

  }
}
