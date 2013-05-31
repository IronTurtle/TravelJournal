package com.souvenir.android;

import java.util.ArrayList;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Note;
import com.evernote.edam.notestore.*;

public class ItineraryActivity extends SherlockFragmentActivity
{

  /**
   * ************************************************************************
   * You MUST change the following values to run this sample application. *
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
  private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

  /**
   * ************************************************************************
   * The following values are simply part of the demo application. *
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
  public void onCreate(Bundle savedInstanceState)
  {
    /*
     * super.onCreate(savedInstanceState);
     * setContentView(R.layout.itineraryactivity); setupSession();
     */
    super.onCreate(savedInstanceState);

    setContentView(R.layout.itineraryactivity);
    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    ItineraryFragment mItineraryFragment = new ItineraryFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(R.id.fragment, mItineraryFragment);
    fragmentTransaction.commit();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    updateUi();
  }

  /**
   * Setup the EvernoteSession used to access the Evernote API.
   */
  private void setupSession()
  {

    // Retrieve persisted authentication information
    mEvernoteSession = EvernoteSession.getInstance(this, CONSUMER_KEY,
        CONSUMER_SECRET, EVERNOTE_SERVICE);
  }

  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateUi()
  {
    if (mEvernoteSession.isLoggedIn())
    {
      listViewCreate();
    }
    else
    {
    }
  }

  /**
   * Called when the user taps the "Log in to Evernote" button. Initiates the
   * Evernote OAuth process, or logs out if the user is already logged in.
   */
  public void startAuth(View view)
  {
    if (mEvernoteSession.isLoggedIn())
    {
      try
      {
        mEvernoteSession.logOut(getApplicationContext());
      }
      catch (InvalidAuthenticationException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else
    {
      mEvernoteSession.authenticate(this);
    }
    updateUi();
  }

  // using removeDialog, could use Fragments instead
  // @SuppressWarnings("deprecation")
  // @Override
  protected void onPostExecute(Note note)
  {

    if (note == null)
    {
      Toast.makeText(getApplicationContext(), R.string.err_creating_note,
          Toast.LENGTH_LONG).show();
      return;
    }

    Toast.makeText(getApplicationContext(), R.string.success_creating_note,
        Toast.LENGTH_LONG).show();
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
      // filter.setOrder(NoteSortOrder.UPDATED.getValue());

      filter.setWords("tag:itinerary*");

      NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
      spec.setIncludeTitle(true);
      try
      {
        final NotesMetadataList notes = null;
        mEvernoteSession
            .getClientFactory()
            .createNoteStoreClient()
            .findNotesMetadata(filter, 0, pageSize, spec,
                new OnClientCallback<NotesMetadataList>()
                {
                  @Override
                  public void onSuccess(NotesMetadataList data)
                  {
                    // removeDialog(DIALOG_PROGRESS);
                    // Toast.makeText(getApplicationContext(),
                    // R.string.msg_image_saved, Toast.LENGTH_LONG).show();
                    // notes = data;
                  }

                  @Override
                  public void onException(Exception exception)
                  {
                    // Log.e(LOGTAG, "Error saving note", exception);
                    // Toast.makeText(getApplicationContext(),
                    // R.string.error_saving_note, Toast.LENGTH_LONG).show();
                    // removeDialog(DIALOG_PROGRESS);
                  }
                });
        return notes;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return new NotesMetadataList();
    }

    @Override
    protected void onPostExecute(NotesMetadataList notes)
    {
      // entries.addAll(notes.getNotes());
      for (NoteMetadata note : notes.getNotes())
      {
        entries.add(note.getTitle());
      }

      // Log.e("log_tag ******",notes.getNotes().get(0).getTitle());
      // Log.e("log_tag ******",entries.get(0).getTitle());
      /*
       * for (NoteMetadata note2 : notes.getNotes()) {
       * System.out.println(note2.getTitle()); }
       */
      ListView listView = (ListView) findViewById(R.id.lview);
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(
          ItineraryActivity.this,
          android.R.layout.simple_list_item_activated_1, entries);
      listView.setAdapter(adapter);
      listView.setScrollingCacheEnabled(false);
      listView.setOnItemClickListener(new OnItemClickListener()
      {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
        {
          // Intent i = new Intent(getApplicationContext(), JournalEntry.class);
          // startActivityForResult(i, 100);
        }
      });
    }
  }

}
