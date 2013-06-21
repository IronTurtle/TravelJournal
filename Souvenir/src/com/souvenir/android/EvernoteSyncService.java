package com.souvenir.android;

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
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.notestore.SyncChunk;
import com.evernote.edam.notestore.SyncChunkFilter;
import com.evernote.edam.notestore.SyncState;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.souvenir.database.SouvenirContentProvider;
import com.souvenir.database.SouvenirContract;

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

  public void fullSync()
  {

    System.out.println("full sync");
    SyncChunkFilter filter1 = new SyncChunkFilter();
    filter1.setIncludeNotebooks(true);
    filter1.setIncludeNotes(true);
    filter1.setRequireNoteContentClass("com.souvenir.android");

    NoteFilter filter = new NoteFilter();
    filter.setOrder(NoteSortOrder.UPDATED.getValue());
    filter.setWords("notebook:\"" + TRAVEL_NOTEBOOK_NAME + "\"");
    int high = 0;
    while (high != serverlastUpdateCount)
    {

      SyncChunk data = null;
      try
      {
        data = noteStore.getFilteredSyncChunk(mEvernoteSession.getAuthToken(),
            high, 15, filter1);
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

      if (data == null || data.getNotes() == null)
        continue;
      high = data.getChunkHighUSN();
      System.out.println(data.getChunkHighUSN() + " " + serverlastUpdateCount);
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
                        + SouvenirContentProvider.DatabaseConstants.NOTE_RESOURCES),
                    cv);
          }
          // TODO
          // adapter.notifyDataSetChanged();
          lastUpdateCount = serverlastUpdateCount;
          lastSyncTime = serverLastSyncTime;
          prefs.edit().putInt("lastUpdateCount", lastUpdateCount).commit();
          prefs.edit().putLong("lastSyncTime", lastSyncTime).commit();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    sendChanges();

  }

  public void sendChanges()
  {
    String[] projection = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_SYNC_NUM,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_DIRTY };
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
        String[] args = { "" + snote.getId() };
        Cursor resCursor;
        if ((resCursor = getContentResolver().query(
            Uri.parse(SouvenirContentProvider.CONTENT_URI
                + SouvenirContentProvider.DatabaseConstants.NOTE_RESOURCES),
            null,
            SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID
                + "=" + snote.getId(), null, null)) != null
            && resCursor.getCount() > 0)
        {
          while (resCursor.moveToNext())
          {
            snote.addResource(new SResource(resCursor));
          }
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
    }
  }

  public void saveNote(final SNote snote)
  {
    Note note = snote.toNote();
    try
    {
      Note data = noteStore.createNote(mEvernoteSession.getAuthToken(), note);

      snote.setSyncNum(data.getUpdateSequenceNum());
      snote.setDirty(false);
      update(snote);

    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  public void updateNote(final SNote snote)
  {
    Note note = snote.toNote();
    try
    {
      Note data = noteStore.updateNote(mEvernoteSession.getAuthToken(), note);

      snote.setSyncNum(data.getUpdateSequenceNum());
      snote.setDirty(false);
      update(snote);
      if (snote.getSyncNum() == lastUpdateCount + 1)
      {
        // still in sync
        prefs.edit().putInt("lastUpdateCount", lastUpdateCount).commit();
      }
      else if (snote.getSyncNum() > lastUpdateCount + 1)
      {
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
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    // fullSync();

    lastUpdateCount = prefs.getInt("lastUpdateCount", 0);
    lastSyncTime = prefs.getLong("lastSyncTime", 0);

    // if (lastSyncTime == 0)
    // {
    // fullSync();
    // return;
    // }

    try
    {
      SyncState ss = noteStore.getSyncState(mEvernoteSession.getAuthToken());
      System.out.println("" + lastSyncTime + " " + lastUpdateCount);
      System.out.println("" + ss.getFullSyncBefore() + " "
          + ss.getUpdateCount());
      serverLastSyncTime = ss.getFullSyncBefore();
      serverlastUpdateCount = ss.getUpdateCount();
      if (true)
      {
        fullSync();
        return;
      }
      if (serverLastSyncTime > lastSyncTime)
      {
        fullSync();
      }
      else if (serverlastUpdateCount == lastUpdateCount)
      {
        // Send changes
      }
      else
      {
        // incrementalSync();
        // return;
      }
    }
    catch (Exception e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // if (mEvernoteSession.isLoggedIn())
    // {
    //
    // int pageSize = SNIPPET_PAGE_SIZE;
    //
    // NoteFilter filter = new NoteFilter();
    // filter.setOrder(NoteSortOrder.UPDATED.getValue());
    // filter.setWords("notebook:\"" + TRAVEL_NOTEBOOK_NAME + "\"");
    //
    // NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
    // spec.setIncludeTitle(true);
    //
    // try
    // {
    // mEvernoteSession
    // .getClientFactory()
    // .createNoteStoreClient()
    // .findNotesMetadata(filter, 0, pageSize, spec,
    // new OnClientCallback<NotesMetadataList>()
    // {
    // @Override
    // public void onSuccess(NotesMetadataList notes)
    // {
    // for (int i = 0; i < notes.getNotesSize(); i++)
    // {
    // NoteMetadata snippetEntry = notes.getNotes().get(i);
    // final int position = i;
    // try
    // {
    //
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    //
    // }
    //
    // /*
    // * listView.setOnItemClickListener(new OnItemClickListener()
    // * {
    // *
    // * @Override public void onItemClick(AdapterView<?> parent,
    // * View view, int position, long id) { String item =
    // * entries.get(position).getTitle();
    // * System.out.println("Clicked" + item);
    // *
    // * Toast.makeText(SnippetFragment.this.getActivity().
    // * getBaseContext(), item, Toast.LENGTH_LONG).show(); //
    // * Intent i = new
    // * Intent(this.getActivity().getApplicationContext(), //
    // * JournalEntry.class); // startActivityForResult(i, 100); }
    // * });
    // */
    // }
    //
    // @Override
    // public void onException(Exception exception)
    // {
    // // Log.e(LOGTAG, "Error saving note", exception);
    // // Toast.makeText(getApplicationContext(),
    // // R.string.error_saving_note, Toast.LENGTH_LONG).show();
    // // removeDialog(DIALOG_PROGRESS);
    // }
    // });
    // }
    // catch (TTransportException e)
    // {
    // e.printStackTrace();
    // }
    // }
  }

  // /**
  // * This method downloads all of the Apps from the App server. For each App,
  // it
  // * checks the AppContentProvider to see if it has already been downloaded
  // * before. If it is new, then it adds it to the AppContentProvider by
  // calling
  // * addNewApp.
  // *
  // * @throws MalformedURLException
  // */
  // private void getAppsFromServer()
  // {
  // try
  // {
  // URL url = new URL(GET_APPS_URL);
  // BufferedReader in = new BufferedReader(new InputStreamReader(
  // url.openStream()));
  //
  // StringBuffer sb = new StringBuffer();
  // String s;
  // while ((s = in.readLine()) != null)
  // {
  // sb.append(s);
  // }
  // in.close();
  // String[] apps = sb.toString().split(";");
  // for (String string : apps)
  // {
  // String[] split = string.split(",");
  // addNewApp(new App(split[0], split[1]));
  // }
  //
  // }
  // catch (Exception e)
  // {
  // Log.e(getPackageName(), e.getMessage());
  // }
  //
  // }
  //
  // /**
  // * This method adds a new App to the AppContentProvider.
  // *
  // * @param app
  // * The new App object to add to the ContentProvider.
  // */
  // private void addNewApp(App app)
  // {
  // Uri uri = Uri.parse(AppContentProvider.CONTENT_URI + "/apps/"
  // + app.getName());
  // Cursor cursor = getContentResolver().query(uri, null, null, null, null);
  // if (cursor.getCount() != 0)
  // {
  // return;
  // }
  // ContentValues cv = new ContentValues();
  // cv.put(AppTable.APP_KEY_NAME, app.getName());
  // cv.put(AppTable.APP_KEY_RATING, app.getRating());
  // cv.put(AppTable.APP_KEY_INSTALLURI, app.getInstallURI());
  // cv.put(AppTable.APP_KEY_INSTALLED, app.isInstalled() ? 1 : 0);
  // Uri inUri = Uri.parse(AppContentProvider.CONTENT_URI + "/apps/"
  // + app.getID());
  // inUri = getContentResolver().insert(inUri, cv);
  // app.setID(Long.valueOf(inUri.getLastPathSegment()));
  // announceNewApp();
  // cursor.close();
  // }

  /**
   * This method broadcasts an intent with a specific Action String. This method
   * should be called when a new App has been downloaded and added successfully.
   */
  private void announceNewApp()
  {
    // Intent intent = new
    // Intent(AppRater.DownloadCompleteReceiver.ACTION_NEW_APP_TO_REVIEW).addCategory(Intent.CATEGORY_DEFAULT);
    // sendBroadcast(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    syncCheck();

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

}
