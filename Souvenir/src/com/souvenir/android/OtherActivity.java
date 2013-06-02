package com.souvenir.android;

import java.util.Arrays;

import com.facebook.widget.UserSettingsFragment;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.facebook.Session;
import com.facebook.SessionState;

public class OtherActivity extends SherlockFragmentActivity {

	private UserSettingsFragment userSettingsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fblogin_fragment_activity);
		
		// Fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
		userSettingsFragment = (UserSettingsFragment) fragmentManager.findFragmentById(R.id.login_fragment);
        if (userSettingsFragment == null) {
            userSettingsFragment = new UserSettingsFragment();
            transaction.add(R.id.login_fragment, userSettingsFragment);
            userSettingsFragment.setReadPermissions(Arrays.asList("publish_actions"));
        }
        
        transaction.commit();
        
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.create_note_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

}
