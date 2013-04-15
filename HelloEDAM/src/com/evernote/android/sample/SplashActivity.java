package com.evernote.android.sample;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.splash);
	      Thread splashThread = new Thread() {
	         @Override
	         public void run() {
	            try {
	               int waited = 0;
	               while (waited < 500) {
	                  sleep(100);
	                  waited += 100;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	               finish();
	               Intent i = new Intent();
	               i.setClassName("com.evernote.android.sample",
	                              "com.evernote.android.sample.AbsActivity");
	               startActivity(i);
	            }
	         }
	      };
	      splashThread.start();
		   
	}

}
