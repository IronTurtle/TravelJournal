/*
 * Copyright 2012 Evernote Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.android.sample;

import java.util.ArrayList;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.client.oauth.android.EvernoteUtil;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.os.AsyncTask;
import android.util.Log;
/**
 * This simple Android app demonstrates how to integrate with the
 * Evernote API (aka EDAM).
 * <p/>
 * In this sample, the user authorizes access to their account using OAuth
 * and chooses an image from the device's image gallery. The image is then
 * saved directly to user's Evernote account as a new note.
 */
public class ItineraryActivity extends BaseActivity {

  /**
   * ************************************************************************
   * You MUST change the following values to run this sample application.    *
   * *************************************************************************
   */

  // Your Evernote API key. See http://dev.evernote.com/documentation/cloud/
  // Please obfuscate your code to help keep these values secret.
  private static final String CONSUMER_KEY = "ironsuturtle";
  private static final String CONSUMER_SECRET = "e0441c112aab58f6";

  /**
   * ************************************************************************
   * Change these values as needed to use this code in your own application. *
   * *************************************************************************
   */

  // Initial development is done on Evernote's testing service, the sandbox.
  // Change to HOST_PRODUCTION to use the Evernote production service
  // once your code is complete, or HOST_CHINA to use the Yinxiang Biji
  // (Evernote China) production service.
  private static final String EVERNOTE_HOST = EvernoteSession.HOST_SANDBOX;

  /**
   * ************************************************************************
   * The following values are simply part of the demo application.           *
   * *************************************************************************
   */

  ArrayList<String> entries;

  // Used to interact with the Evernote web service
  private EvernoteSession mEvernoteSession;

  /**
   * Called when the activity is first created.
   */
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.itineraryactivity);

    setupSession();
  }


  @Override
  public void onResume() {
    super.onResume();
    updateUi();
  }

  /**
   * Setup the EvernoteSession used to access the Evernote API.
   */
  private void setupSession() {

    // Retrieve persisted authentication information
    mEvernoteSession = EvernoteSession.init(this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_HOST, null);
  }

  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateUi() {
    if (mEvernoteSession.isLoggedIn()) {
      listViewCreate();
    } else {
    }
  }

  /**
   * Called when the user taps the "Log in to Evernote" button.
   * Initiates the Evernote OAuth process, or logs out if the user is already
   * logged in.
   */
  public void startAuth(View view) {
    if (mEvernoteSession.isLoggedIn()) {
      mEvernoteSession.logOut(getApplicationContext());
    } else {
      mEvernoteSession.authenticate(this);
    }
    updateUi();
  }

  // using removeDialog, could use Fragments instead
  //  @SuppressWarnings("deprecation")
  //  @Override
  protected void onPostExecute(Note note) {

    if (note == null) {
      Toast.makeText(getApplicationContext(), R.string.err_creating_note, Toast.LENGTH_LONG).show();
      return;
    }

    Toast.makeText(getApplicationContext(), R.string.msg_image_saved, Toast.LENGTH_LONG).show();
  }

  public void update()
  {
    new EntryUpdater().execute("");
  }

  public void listViewCreate()
  {
    entries = new ArrayList<String>();
    update();
  }

  private class EntryUpdater extends AsyncTask<String, Void, NotesMetadataList>
  {
    // using Display.getWidth and getHeight on older SDKs
    @SuppressWarnings("deprecation")
    // suppress lint check on Display.getSize(Point)
    @TargetApi(16)
    @Override
    protected NotesMetadataList doInBackground(String... strings) 
    {
      int pageSize = 10;
      NoteFilter filter = new NoteFilter();
      filter.setOrder(NoteSortOrder.UPDATED.getValue());
      filter.setWords("itinerary");

      NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
      spec.setIncludeTitle(true);
      try{
        NotesMetadataList notes = mEvernoteSession.createNoteStore().findNotesMetadata(mEvernoteSession.getAuthToken() , filter, 0, pageSize, spec);
        return notes;
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      return new NotesMetadataList();
    }

    @Override
    protected void onPostExecute(NotesMetadataList notes) {
//      entries.addAll(notes.getNotes());     
    for(NoteMetadata note : notes.getNotes())
    {
      entries.add(note.getTitle());
    }
      
     // Log.e("log_tag ******",notes.getNotes().get(0).getTitle());
     // Log.e("log_tag ******",entries.get(0).getTitle());
     /* for (NoteMetadata note2 : notes.getNotes()) {
        System.out.println(note2.getTitle());
      }    
*/
      ListView listView = (ListView) findViewById(R.id.lview);
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(ItineraryActivity.this, android.R.layout.simple_list_item_1, entries);
      listView.setAdapter(adapter);
      listView.setScrollingCacheEnabled(false);
      listView.setOnItemClickListener(new OnItemClickListener()
          {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
              long id)
            {
              //            Intent i = new Intent(getApplicationContext(), JournalEntry.class);
              //            startActivityForResult(i, 100);
            }
          });
    }
  }


}
