package com.souvenir.android;

import java.util.Arrays;

import org.json.JSONException;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.UserSettingsFragment;

public class ShareActivity extends SherlockFragmentActivity {

	
private UserSettingsFragment userSettingsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*
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
        */
        
		setContentView(R.layout.fragment_standard);
		
        ShareFragment mShareFragment = new ShareFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction();
        transaction.add(R.id.fragment, mShareFragment);
        transaction.commit();
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.create_note_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

}
