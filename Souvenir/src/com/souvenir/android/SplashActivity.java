package com.souvenir.android;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);

    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        this.getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()/* .enableLogging() */.build();
    ImageLoader.getInstance().init(config);

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
          Intent i = new Intent(SplashActivity.this, TabActivity.class);

          startActivity(i);
        }
      }
    };
    splashThread.start();

  }

}
