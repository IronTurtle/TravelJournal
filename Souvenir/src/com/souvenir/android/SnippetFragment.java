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
package com.souvenir.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.evernote.edam.type.Note;
import com.souvenir.android.CreateDialogFragment.DialogListener;
import com.souvenir.android.NewTripDialogFragment.TripDialogListener;
import com.souvenir.android.database.SouvenirContentProvider;
import com.souvenir.android.database.SouvenirContract;

public class SnippetFragment extends ParentFragment implements OnClickListener,
    LoaderManager.LoaderCallbacks<Cursor>, DialogListener, TripDialogListener
{
  private static String TRAVEL_NOTEBOOK_NAME = "Travel Notebook";
  private final static String CREATE_TAG = "CREATE";
  // private static String NOTEBOOK_GUID;

  // UI elements that we update
  private Button mBtnAuth;
  Button btnTakePhoto;
  ImageView imgTakenPhoto;
  final String TAG1 = "MyCamera";
  ListView listView;
  SnippetCursorAdapter adapter;
  int id = 2;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    if (getArguments() != null)
    {
      id = 3;
      trip = getArguments().getString("trip");
    }

    ActionBar abs = getSherlockActivity().getSupportActionBar();
    // abs.setTitle("Outfit");
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);
    View view = inflater.inflate(R.layout.fragment_snippet, container, false);
    // mBtnAuth = (Button) view.findViewById(R.id.auth_button);
    // mBtnAuth.setOnClickListener(this);

    listView = (ListView) view.findViewById(R.id.lview);
    adapter = new SnippetCursorAdapter(getActivity(), null, 0);

    listView.setAdapter(adapter);
    listView.setScrollingCacheEnabled(false);
    listView.setOnItemClickListener(new OnItemClickListener()
    {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id)
      {
        Intent intent = new Intent(SnippetFragment.this.getActivity(),
            NoteActivity.class).putExtra("note",
            ((SouvenirSnippetView) view).getNote());
        getActivity().startActivityForResult(intent, 300);
      }
    });
    getActivity().getSupportLoaderManager().initLoader(id, null, this);

    return view;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    // updateUi();
    if (((DrawerActivity) getActivity()).isAutosync())
    {
      getActivity().startService(
          new Intent(getActivity(), EvernoteSyncService.class));
    }
    getActivity().getSupportLoaderManager().restartLoader(id, null, this);
    adapter.notifyDataSetChanged();
  }

  public void addNoteOnClick(View view)
  {

    Intent intent = new Intent(this.getActivity().getApplicationContext(),
        NoteFragment.class);

    this.startActivityForResult(intent, 200);

  }

  public void goToItineraryOnClick(View view)
  {
    Intent intent = new Intent(this.getActivity().getApplicationContext(),
        ItineraryActivity.class);

    this.startActivityForResult(intent, 100);
  }

  protected void onPostExecute(Note note)
  {
    this.getActivity();

    if (note == null)
    {
      Toast.makeText(this.getActivity().getApplicationContext(),
          R.string.err_creating_note, Toast.LENGTH_LONG).show();
      return;
    }

    Toast.makeText(this.getActivity().getApplicationContext(),
        R.string.success_creating_note, Toast.LENGTH_LONG).show();

  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    // case R.id.auth_button:
    //
    // startAuth(this.getView());
    //
    // break;
    }
  }

  // public class SnippetAdapter extends ArrayAdapter<Note>
  // {
  // protected ImageLoader imageLoader = ImageLoader.getInstance();
  //
  // int resource;
  // String response;
  // Context context;
  // private EvernoteSession mEvernoteSession;
  // DisplayImageOptions options;
  // ArrayList<String> contents = new ArrayList<String>();
  //
  // // Initialize adapter
  // public SnippetAdapter(Context context, int resource, List<Note> items,
  // EvernoteSession mEvernoteSession)
  // {
  // super(context, resource, items);
  // this.resource = resource;
  // this.context = context;
  // this.mEvernoteSession = mEvernoteSession;
  //
  // options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
  // .showStubImage(R.drawable.traveljournal).build();
  //
  // }
  //
  // public View getView(final int position, View convertView, ViewGroup parent)
  // {
  //
  // RelativeLayout snippetView = (RelativeLayout) convertView;
  // // Get the current alert object
  // final Note snippetEntry = getItem(position);
  //
  // // Inflate the view
  // if (convertView == null)
  // {
  // LayoutInflater inflater = (LayoutInflater) context
  // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  // snippetView = (RelativeLayout) inflater.inflate(R.layout.snippet, null);
  //
  // }
  // else
  // {
  // snippetView = (RelativeLayout) convertView;
  // }
  //
  // snippetView.setOnClickListener(new OnClickListener()
  // {
  // @Override
  // public void onClick(View v)
  // {
  // String item = snippetEntry.getTitle();
  // // System.out.println("Clicked" + item);
  //
  // // Toast.makeText(SnippetFragment.this.getActivity().getBaseContext(),item,
  // // Toast.LENGTH_LONG).show();
  //
  // Intent intent = new Intent(SnippetFragment.this.getActivity(),
  // EntryActivity.class).putExtra("note", snippetEntry);
  // SnippetFragment.this.getActivity()
  // .startActivityForResult(intent, 300);
  //
  // }
  // });
  //
  // // Get the text boxes from the listitem.xml file
  // TextView snippetEvent = (TextView) snippetView
  // .findViewById(R.id.snippetEvent);
  // final TextView snippetLocation = (TextView) snippetView
  // .findViewById(R.id.snippetLocation);
  // final TextView snippetText = (TextView) snippetView
  // .findViewById(R.id.snippetText);
  // ImageView snippetPic = (ImageView) snippetView
  // .findViewById(R.id.snippetPic);
  // snippetText.setText("");
  //
  // imageLoader.displayImage(
  // "" + mEvernoteSession.getAuthenticationResult().getWebApiUrlPrefix()
  // + "thm/note/" + snippetEntry.getGuid() + "?auth="
  // + mEvernoteSession.getAuthToken(), snippetPic, options);
  //
  // String location = snippetEntry.getAttributes().getPlaceName();
  // if (location == null)
  // {
  // location = String.valueOf((snippetEntry.getAttributes().getLatitude())
  // + String.valueOf(snippetEntry.getAttributes().getLongitude()));
  // }
  //
  // snippetLocation.setText(location);
  // // System.out.println("LOCATION: " + location);
  // snippetEvent.setText(snippetEntry.getTitle().toUpperCase());
  // snippetText.setText(android.text.Html.fromHtml(snippetEntry.getContent())
  // .toString());
  //
  // // snippetText.setText(Integer.valueOf(position).toString());
  // // snippetLocation.setText("Evernote Hack");
  //
  // return snippetView;
  // }
  // }

  public class SnippetCursorAdapter extends CursorAdapter
  {

    /**
     * The OnAppChangeListener that should be connected to each of the AppViews
     * created/managed by this Adapter.
     */
    // private OnAppChangeListener m_listener;

    public SnippetCursorAdapter(Context context, Cursor cursor, int flags)
    {
      super(context, cursor, flags);
      // this.m_listener = null;
    }

    //
    // /**
    // * Mutator method for changing the OnAppChangeListener.
    // *
    // * @param listener
    // * The OnAppChangeListener that will be notified when the
    // * internal state of any Joke contained in one of this Adapters
    // * AppViews is changed.
    // */
    // public void setOnAppChangeListener(OnAppChangeListener mListener)
    // {
    // // this.m_listener = mListener;
    // }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
      SNote note = new SNote(cursor);
      String[] args = { "" + note.getId() };
      Cursor resCursor;
      if ((resCursor = getActivity().getContentResolver().query(
          Uri.parse(SouvenirContentProvider.CONTENT_URI
              + SouvenirContentProvider.DatabaseConstants.NOTE_RES),
          null,
          SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID + "="
              + note.getId(), null, null)) != null
          && resCursor.getCount() > 0)
      {
        while (resCursor.moveToNext())
        {
          note.addResource(new SResource(resCursor));
        }
      }
      resCursor.close();
      // ((AppView) view).setOnAppChangeListener(null);
      ((SouvenirSnippetView) view).setSNote(note);
      // ((AppView) view).setOnAppChangeListener(this.m_listener);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      SNote note = new SNote(cursor);
      String[] args = { "" + note.getId() };
      Cursor resCursor;
      if ((resCursor = getActivity().getContentResolver().query(
          Uri.parse(SouvenirContentProvider.CONTENT_URI
              + SouvenirContentProvider.DatabaseConstants.NOTE_RES), null,
          null, null, null)) != null
          && resCursor.getCount() > 0)
      {
        while (resCursor.moveToNext())
        {
          SResource sc = new SResource(resCursor);
          // System.out.println(note.getId() + " " + sc.getNoteId());
          note.addResource(sc);
        }
      }
      resCursor.close();
      SouvenirSnippetView sv = new SouvenirSnippetView(context, note);
      return sv;
    }
  }

  // private void checkForTravelNotebook()
  // {
  // try
  // {
  // mEvernoteSession.getClientFactory().createNoteStoreClient()
  // .listNotebooks(new OnClientCallback<List<Notebook>>()
  // {
  //
  // @Override
  // public void onSuccess(List<Notebook> notebookList)
  // {
  // for (Notebook notebook : notebookList)
  // {
  // if ((notebook.getName().toString())
  // .equals(TRAVEL_NOTEBOOK_NAME))
  // {
  // NOTEBOOK_GUID = notebook.getGuid();
  // // listViewCreate();
  // return;
  // }
  // }
  // // Travel Notebook not found/created
  // Notebook notebook = new Notebook();
  // notebook.setName(TRAVEL_NOTEBOOK_NAME);
  // try
  // {
  // mEvernoteSession.getClientFactory().createNoteStoreClient()
  // .createNotebook(notebook, new OnClientCallback<Notebook>()
  // {
  //
  // @Override
  // public void onSuccess(Notebook created)
  // {
  // NOTEBOOK_GUID = created.getGuid();
  // }
  //
  // @Override
  // public void onException(Exception exception)
  // {
  // Toast.makeText(getActivity().getApplicationContext(),
  // "Warning: Travel Notebook not created.",
  // Toast.LENGTH_LONG).show();
  // exception.printStackTrace();
  // }
  //
  // });
  //
  // // listViewCreate();
  // }
  // catch (TTransportException e)
  // {
  // e.printStackTrace();
  // }
  // }
  //
  // @Override
  // public void onException(Exception exception)
  // {
  //
  // }
  //
  // });
  // }
  // catch (TTransportException e1)
  // {
  // e1.printStackTrace();
  // }
  // }

  String trip;

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    String[] projection = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID };

    Uri uri = Uri.parse(SouvenirContentProvider.CONTENT_URI
        + SouvenirContentProvider.DatabaseConstants.NOTE);

    CursorLoader cursorLoader = null;
    if (trip == null)
      cursorLoader = new CursorLoader(getActivity(), uri, null, null, null,
          SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE + " DESC");
    else
    {
      getSherlockActivity().getSupportActionBar().setTitle(
          getSherlockActivity().getSupportActionBar().getTitle() + ": "
              + getArguments().getString("tripName"));
      String[] args1 = { getArguments().getString("tripName") };
      cursorLoader = new CursorLoader(getActivity(), uri, null,
          SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TRIP_ID + "=?", args1,
          SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE + " DESC");
    }
    return cursorLoader;

    // CursorLoader cursorLoader = new CursorLoader(getActivity(),
    // Uri.parse(SouvenirContentProvider.CONTENT_URI
    // + SouvenirContentProvider.DatabaseConstants.TRIP), null, null,
    // null, null);
    // return cursorLoader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
  {
    this.adapter.swapCursor(cursor);
    // this.m_appAdapter.setOnAppChangeListener(this);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    this.adapter.swapCursor(null);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    inflater.inflate(R.menu.completed_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case android.R.id.home:
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. Use NavUtils to allow users
      // to navigate up one level in the application structure. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      // syncCheck();
      break;

    case R.id.menu_add_note:
      // startActivity(new Intent(getActivity(), NoteActivity.class));
      CreateDialogFragment dialogFragment = CreateDialogFragment
          .newInstance(SnippetFragment.this);
      dialogFragment.show(getFragmentManager(), CREATE_TAG);
      break;

    case R.id.menu_refresh:
      getActivity().startService(
          new Intent(getActivity(), EvernoteSyncService.class));
      getActivity().getSupportLoaderManager().restartLoader(id, null, this);
      // adapter.notifyDataSetChanged();
      return true;

      // case R.id.facebook_login:
      // startActivity(new Intent(getActivity(), OtherActivity.class));
      // break;

    }
    return super.onOptionsItemSelected(item);
  }

  public void openTripDialog()
  {

    NewTripDialogFragment dialogFragment = NewTripDialogFragment
        .newInstance(SnippetFragment.this);
    dialogFragment.show(getFragmentManager(), CREATE_TAG);
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog)
  {
    startActivity(new Intent(getActivity(), NoteActivity.class));
  }

  @Override
  public void onDialogNegativeClick(DialogFragment dialog)
  {
    // Create another dialog to create Trip name, Location, & date
    // range
    // Trip name is the only mandatory field
    Toast.makeText(getActivity().getApplicationContext(), "Creating Trip",
        Toast.LENGTH_SHORT).show();
    openTripDialog();
  }

  @Override
  public void onTripDialogPositiveClick(DialogFragment dialog, Bundle tripInfo)
  {
    Toast.makeText(getActivity().getApplicationContext(), "Trip Created",
        Toast.LENGTH_SHORT).show();

    // System.out.println("name: " + tripInfo.getCharSequence("tripName") + "\n"
    // + "genLocation: " + tripInfo.getCharSequence("tripGenLoc") + "\n"
    // + "startDate: " + tripInfo.getCharSequence("tripStartDate") + "\n"
    // + "endDate: " + tripInfo.getCharSequence("tripEndDate"));
    if (tripInfo.getCharSequence("tripName").toString().isEmpty())
      return;
    STrip sTrip = new STrip(tripInfo.getCharSequence("tripName").toString());
    // STrip sTrip = new STrip(tripInfo.getCharSequence("tripName").toString(),
    // tripInfo.getCharSequence("tripGenLoc").toString(), tripInfo
    // .getCharSequence("tripStartDate").toString(), tripInfo
    // .getCharSequence("tripEndDate").toString());
    sTrip.setDirty(true);
    Uri uri = getActivity().getContentResolver().insert(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP),
        sTrip.toContentValues());
    int id = Integer.valueOf(uri.getLastPathSegment());
    sTrip.setId(id);

    Toast.makeText(getActivity(),
        "Trip Created: " + tripInfo.getCharSequence("tripName").toString(),
        Toast.LENGTH_SHORT).show();
    getActivity().startService(
        new Intent(getActivity(), EvernoteSyncService.class));
    getActivity().getSupportLoaderManager().restartLoader(id, null, this);
    System.out.println("Trip ID: " + sTrip.getId());

  }

  @Override
  public void onTripDialogNegativeClick(DialogFragment dialog)
  {

    Toast.makeText(getActivity().getApplicationContext(), "New Trip Cancelled",
        Toast.LENGTH_SHORT).show();

    String[] queryColumns = { SouvenirContract.SouvenirTrip._ID,
        SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_NAME };

    Cursor cursor = getActivity().getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP), null, null,
        null, null);
    while (cursor.moveToNext())
    {
      System.out.println("TripID: "
          + cursor.getString(cursor.getColumnIndex(queryColumns[0]))
          + " TRIPNAME: "
          + cursor.getString(cursor.getColumnIndex(queryColumns[1])));
    }
  }
}