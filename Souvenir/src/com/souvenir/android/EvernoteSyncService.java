package com.souvenir.android;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.notestore.SyncChunk;
import com.evernote.edam.notestore.SyncChunkFilter;
import com.evernote.edam.notestore.SyncState;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.souvenir.android.database.SouvenirContentProvider;
import com.souvenir.android.database.SouvenirContract;

/**
 * The Service that performs app information downloading, performing the check
 * again occasionally over time. It adds an application to the list of apps and
 * is also responsible for telling the BroadcastReceiver when it has done so.
 */
public class EvernoteSyncService extends IntentService
{
  static final String CONSUMER_KEY = "ironsuturtle";
  static final String CONSUMER_SECRET = "e0441c112aab58f6";
  static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

  private static String TRAVEL_NOTEBOOK_NAME = "Travel Notebook";
  private static String NOTEBOOK_GUID = null;

  private static String TRIPS_TAG = "Trips";
  private static String TRIPS_GUID = null;

  protected EvernoteSession mEvernoteSession = EvernoteSession.getInstance(
      this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_SERVICE);

  /** The ID for the Notification that is generated when a new App is added. */
  public static final int NEW_APP_NOTIFICATION_ID = 1;

  /**
   * The Timer thread which will execute the check for new Apps. Acts like a
   * Thread that can be told to start at a specific time and/or at specific time
   * intervals.
   */
  private Timer m_updateTimer;

  /**
   * The TimerTask which encapsulates the logic that will check for new Apps.
   * This ends up getting run by the Timer in the same way that a Thread runs a
   * Runnable.
   */
  private TimerTask m_updateTask;

  /**
   * The time frequency at which the service should check the server for new
   * Apps.
   */
  private static final long UPDATE_FREQUENCY = 10000L;

  /** A String containing the URL from which to download the list of all Apps. */
  public static final String GET_APPS_URL = "http://www.simexusa.com/aac/getAll.php";

  /**
   * Note that the constructor that takes a String will NOT be properly
   * instantiated. Use the constructor that takes no parameters instead, and
   * pass in a String that contains the name of the service to the super() call.
   */
  public EvernoteSyncService()
  {
    super(null);
    // TODO
  }

  NoteStore.Client noteStore = null;

  public void sync(int high)
  {

    System.out.println("full sync");
    SyncChunkFilter filter1 = new SyncChunkFilter();
    filter1.setIncludeNotebooks(true);
    filter1.setIncludeNotes(true);
    filter1.setIncludeTags(true);
    filter1.setRequireNoteContentClass("com.souvenir.android");

    // NoteFilter filter = new NoteFilter();
    // filter.setOrder(NoteSortOrder.UPDATED.getValue());
    // filter.setWords("notebook:\"" + TRAVEL_NOTEBOOK_NAME + "\"");
    // int high = 0;
    while (high != serverlastUpdateCount)
    {

      SyncChunk data = null;
      try
      {
        data = noteStore.getFilteredSyncChunk(mEvernoteSession.getAuthToken(),
            high, 15, filter1);
      }
      catch (Exception e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      if (!data.isSetChunkHighUSN())
        break;
      high = data.getChunkHighUSN();

      System.out.println(data.getChunkHighUSN() + " " + serverlastUpdateCount);

      if (data.isSetTags())
      {
        if (TRIPS_GUID == null)
        {
          try
          {
            List<Tag> tags = noteStore
                .listTags(mEvernoteSession.getAuthToken());
            for (Tag tag : tags)
            {
              if (tag.getName().equals(TRIPS_TAG))
              {
                TRIPS_GUID = tag.getGuid();
                break;
              }
            }
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
        if (TRIPS_GUID != null)
        {
          for (Tag tag : data.getTags())
          {
            System.out.println("tag " + tag.getName());
            if (tag.isSetParentGuid() && tag.getParentGuid().equals(TRIPS_GUID))
            {
              System.out.println("tag matches:" + tag.getName());

              STrip trip = new STrip(tag);
              trip.insert(getApplicationContext());
            }
          }
        }
      }

      if (data.isSetNotebooks())
      {
        for (Notebook notebook : data.getNotebooks())
        {
          if ((notebook.getName().toString()).equals(TRAVEL_NOTEBOOK_NAME))
          {
            NOTEBOOK_GUID = notebook.getGuid();
            break;
          }
        }
      }

      if (data.isSetNotes())
      {
        for (Note note : data.getNotes())
        {
          // System.out.println(note.getGuid());
          Cursor cursor;
          String[] args = { note.getGuid() };
          if ((cursor = getContentResolver().query(
              Uri.parse(SouvenirContentProvider.CONTENT_URI
                  + SouvenirContentProvider.DatabaseConstants.NOTE), null,
              SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID + "=?", args,
              null)) != null
              && cursor.getCount() > 0)
          {
            // System.out.println("This GUID already exists "
            // + cursor.getCount());
            // System.out.println("old note");
            while (cursor.moveToNext())
            {

              SNote oldNote = new SNote(cursor);
              // System.out.println(oldNote.getEvernoteGUID());
            }
            cursor.close();
            // System.out.println("syncnumber: " + syncnum);
            continue;
          }

          try
          {
            // System.out.println("new note");
            Note note2 = noteStore.getNote(mEvernoteSession.getAuthToken(),
                note.getGuid(), true, true, false, false);
            if (!note.isActive())
            {
              continue;
            }
            SNote insertNote = new SNote(note2);

            Uri uri = getContentResolver().insert(
                Uri.parse(SouvenirContentProvider.CONTENT_URI
                    + SouvenirContentProvider.DatabaseConstants.NOTE),
                insertNote.toContentValues());
            int id = Integer.valueOf(uri.getLastPathSegment());
            insertNote.setId(id);
            insertNote.processResources(note2);
            for (ContentValues cv : insertNote.getResourcesContentValues())
            {
              getContentResolver()
                  .insert(
                      Uri.parse(SouvenirContentProvider.CONTENT_URI
                          + SouvenirContentProvider.DatabaseConstants.NOTE_RES),
                      cv);
            }
            // TODO
            // adapter.notifyDataSetChanged();

          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      }
    }
    lastUpdateCount = serverlastUpdateCount;
    lastSyncTime = serverLastSyncTime;
    prefs.edit().putInt("lastUpdateCount", lastUpdateCount).commit();
    prefs.edit().putLong("lastSyncTime", lastSyncTime).commit();
    sendChanges();

  }

  public void sendChanges()
  {
    // TODO
    // repetitive
    if (TRIPS_GUID == null)
    {
      try
      {
        List<Tag> tags = noteStore.listTags(mEvernoteSession.getAuthToken());
        for (Tag tag : tags)
        {
          if (tag.getName().equals(TRIPS_TAG))
          {
            TRIPS_GUID = tag.getGuid();
            break;
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    if (TRIPS_GUID == null)
    {
      Tag tag = new Tag();
      tag.setName(TRIPS_TAG);
      try
      {
        tag = noteStore.createTag(mEvernoteSession.getAuthToken(), tag);
        TRIPS_GUID = tag.getGuid();
        if (tag.getUpdateSequenceNum() == lastUpdateCount + 1)
        {
          // still in sync
          lastUpdateCount++;
          prefs.edit().putInt("lastUpdateCount", lastUpdateCount).commit();
        }
        else if (tag.getUpdateSequenceNum() > lastUpdateCount + 1)
        {
          sync(tag.getUpdateSequenceNum());
          // incremental
        }
      }
      catch (EDAMUserException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (EDAMSystemException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (EDAMNotFoundException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (TException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    if (NOTEBOOK_GUID == null)
    {
      try
      {
        List<Notebook> notebooks = noteStore.listNotebooks(mEvernoteSession
            .getAuthToken());
        for (Notebook notebook : notebooks)
        {
          if ((notebook.getName().toString()).equals(TRAVEL_NOTEBOOK_NAME))
          {
            NOTEBOOK_GUID = notebook.getGuid();
          }
        }
      }
      catch (EDAMUserException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      catch (EDAMSystemException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      catch (TException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }

    if (NOTEBOOK_GUID == null)
    {
      Notebook notebook = new Notebook();
      notebook.setName(TRAVEL_NOTEBOOK_NAME);
      try
      {
        notebook = noteStore.createNotebook(mEvernoteSession.getAuthToken(),
            notebook);
        NOTEBOOK_GUID = notebook.getGuid();
        if (notebook.getUpdateSequenceNum() == lastUpdateCount + 1)
        {
          // still in sync
          lastUpdateCount++;
          prefs.edit().putInt("lastUpdateCount", lastUpdateCount).commit();
        }
        else if (notebook.getUpdateSequenceNum() > lastUpdateCount + 1)
        {
          sync(notebook.getUpdateSequenceNum());
          // incremental
        }
      }
      catch (EDAMUserException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (EDAMSystemException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (TException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Cursor cursor;

    // Dirty
    if ((cursor = getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.NOTE), null,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_DIRTY, null, null)) != null
        && cursor.getCount() > 0)
    {
      while (cursor.moveToNext())
      {
        System.out
            .println(cursor.getString(cursor
                .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE)));
        SNote snote = new SNote(cursor);
        // TODO
        Cursor resCursor;
        if ((resCursor = getContentResolver().query(
            Uri.parse(SouvenirContentProvider.CONTENT_URI
                + SouvenirContentProvider.DatabaseConstants.NOTE_RES),
            null,
            SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID
                + "=" + snote.getId(), null, null)) != null
            && resCursor.getCount() > 0)
        {
          while (resCursor.moveToNext())
          {
            snote.addResource(new SResource(resCursor));
          }
          resCursor.close();
        }
        if (snote.getSyncNum() == -1)
        {
          saveNote(snote);
        }
        else
        {
          updateNote(snote);
        }
      }
      cursor.close();
    }
  }

  public void saveNote(final SNote snote)
  {
    Note note = snote.toNote();
    note.setNotebookGuid(NOTEBOOK_GUID);
    try
    {
      Note data = noteStore.createNote(mEvernoteSession.getAuthToken(), note);

      snote.setSyncNum(data.getUpdateSequenceNum());
      snote.setDirty(false);
      update(snote);
      if (snote.getSyncNum() == lastUpdateCount + 1)
      {
        // still in sync
        lastUpdateCount++;
        prefs.edit().putInt("lastUpdateCount", snote.getSyncNum()).commit();
      }
      else if (snote.getSyncNum() > lastUpdateCount + 1)
      {
        sync(snote.getSyncNum());
        // incremental
      }
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  public void updateNote(final SNote snote)
  {
    Note note = snote.toNote();
    note.setNotebookGuid(NOTEBOOK_GUID);
    System.out.println(snote.issetV);
    try
    {
      Note data = noteStore.updateNote(mEvernoteSession.getAuthToken(), note);

      snote.setSyncNum(data.getUpdateSequenceNum());
      snote.setDirty(false);
      update(snote);
      if (snote.getSyncNum() == lastUpdateCount + 1)
      {
        // still in sync
        lastUpdateCount++;
        prefs.edit().putInt("lastUpdateCount", snote.getSyncNum()).commit();
      }
      else if (snote.getSyncNum() > lastUpdateCount + 1)
      {
        sync(snote.getSyncNum());
        // incremental
      }
      else
      {
        System.out.println("uh oh!");
      }
      // Toast.makeText(getActivity(), R.string.success_creating_note,
      // Toast.LENGTH_LONG).show();
    }

    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  public void update(SNote mNote2)
  {
    Uri uri = Uri.parse(SouvenirContentProvider.CONTENT_URI
        + SouvenirContentProvider.DatabaseConstants.GET_NOTE.replace("#", ""
            + mNote2.getId()));

    getContentResolver().update(uri, mNote2.toContentValues(), null, null);
  }

  SharedPreferences prefs;
  protected int serverlastUpdateCount;
  protected long serverLastSyncTime;
  int lastUpdateCount;
  long lastSyncTime;

  public void syncCheck()
  {
    try
    {
      noteStore = mEvernoteSession.getClientFactory().createNoteStoreClient()
          .getClient();
    }
    catch (TTransportException e1)
    {
      e1.printStackTrace();
    }
    prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    lastUpdateCount = prefs.getInt("lastUpdateCount", 0);
    lastSyncTime = prefs.getLong("lastSyncTime", 0);
    // TODO
    // need to check if database is empty cause of upgrade
    try
    {
      SyncState ss = noteStore.getSyncState(mEvernoteSession.getAuthToken());
      System.out.println("" + lastSyncTime + " " + lastUpdateCount);
      System.out.println("" + ss.getFullSyncBefore() + " "
          + ss.getUpdateCount());
      serverLastSyncTime = ss.getFullSyncBefore();
      serverlastUpdateCount = ss.getUpdateCount();
      if (serverLastSyncTime > lastSyncTime)
      {
        sync(0);
      }
      else if (serverlastUpdateCount == lastUpdateCount)
      {
        sendChanges();
      }
      else
      {
        sync(lastUpdateCount);
      }
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    syncCheck();
    System.out.println("Sync Done");

    // m_updateTimer.scheduleAtFixedRate(m_updateTask, 0, UPDATE_FREQUENCY);
  }

  @Override
  public void onCreate()
  {
    m_updateTimer = new Timer();
    m_updateTask = new TimerTask()
    {

      @Override
      public void run()
      {
        syncCheck();
      }
    };
    super.onCreate();
  }

  @Override
  public void onDestroy()
  {
    m_updateTimer.cancel();
    super.onDestroy();
  }

  public void userAccountInfo()
  {
    try
    {
      System.out.println("GETTING USER INFO");
      // final String packageName =
      // this.getApplication().getApplicationContext()
      // .getPackageName();
      mEvernoteSession.getClientFactory().createUserStoreClient()
          .getUser(new OnClientCallback<User>()
          {

            @Override
            public void onSuccess(User data)
            {
              System.out.println(data.getUsername());
              // TODO Auto-generated method stub
              // SharedPreferences prefs = getSharedPreferences(packageName,
              // Context.MODE_PRIVATE);
              // prefs.edit().putString("settings_username",
              // data.getUsername());
            }

            @Override
            public void onException(Exception exception)
            {
              // TODO Auto-generated method stub
              System.out.println("Exception");
              exception.printStackTrace();
            }
          });
    }
    catch (IllegalStateException e)
    {
      // TODO Auto-generated catch block
      System.out.println("IllegalStateException");
      e.printStackTrace();
    }
    catch (TTransportException e)
    {
      // TODO Auto-generated catch block
      System.out.println("TTransportException");
      e.printStackTrace();
    }
  }
}
