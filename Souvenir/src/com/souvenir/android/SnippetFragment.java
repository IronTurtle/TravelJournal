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

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.souvenir.database.SouvenirContentProvider;
import com.souvenir.database.SouvenirContract;

public class SnippetFragment extends ParentFragment implements OnClickListener,
    LoaderManager.LoaderCallbacks<Cursor>
{
  private static final int SNIPPET_PAGE_SIZE = 5;
  private static String TRAVEL_NOTEBOOK_NAME = "Travel Notebook";
  private static String NOTEBOOK_GUID;

  // UI elements that we update
  private Button mBtnAuth;
  Button btnTakePhoto;
  ImageView imgTakenPhoto;
  final String TAG1 = "MyCamera";
  ListView listView;
  SnippetCursorAdapter adapter;
  // public ArrayList<NoteMetadata> entries;
  // public ArrayList<Note> entries2;

  ImageLoader imageLoader;
  DisplayImageOptions options;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    // ContentValues values = new ContentValues();
    // values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID, 12345);
    // values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE, "test");
    // values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
    // "content");
    // this.getActivity()
    // .getContentResolver()
    // .insert(Uri.parse(SouvenirContentProvider.CONTENT_URI + "/apps"),
    // values);
    // ContentValues values2 = new ContentValues();
    // values2.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID, 123456);
    // values2.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
    // "test2");
    // values2.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
    // "content");
    // this.getActivity()
    // .getContentResolver()
    // .insert(Uri.parse(SouvenirContentProvider.CONTENT_URI + "/apps"),
    // values2);

    View view = inflater.inflate(R.layout.fragment_snippet, container, false);
    mBtnAuth = (Button) view.findViewById(R.id.auth_button);
    mBtnAuth.setOnClickListener(this);

    listView = (ListView) view.findViewById(R.id.lview);
    // adapter = new SnippetAdapter(getActivity(), R.layout.snippet, entries2,
    // mEvernoteSession);
    adapter = new SnippetCursorAdapter(getActivity(), null, 0);

    listView.setAdapter(adapter);
    listView.setScrollingCacheEnabled(false);

    getActivity().getSupportLoaderManager().initLoader(1, null, this);

    return view;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    updateUi();
  }

  private void updateUi()
  {
    if (mEvernoteSession.isLoggedIn())
    {
      // mBtnAuth.setText(R.string.label_log_out);
      View b = this.getView().findViewById(R.id.auth_button);
      b.setVisibility(View.GONE);

      // checkForTravelNotebook();
      listViewCreate();
    }
    else
    {
      View b = this.getView().findViewById(R.id.auth_button);
      b.setVisibility(View.VISIBLE);
      // mBtnAuth.setText(R.string.label_log_in);
    }
  }

  public void startAuth(View view)
  {
    if (mEvernoteSession.isLoggedIn())
    {
      try
      {
        mEvernoteSession.logOut(this.getActivity().getApplicationContext());
      }
      catch (InvalidAuthenticationException e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      mEvernoteSession.authenticate(this.getActivity());
    }
    updateUi();
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

  public void listViewCreate()
  {
    // // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
    // entries = new ArrayList<NoteMetadata>();

    // update();
    if (mEvernoteSession.isLoggedIn())
    {
      int pageSize = SNIPPET_PAGE_SIZE;

      NoteFilter filter = new NoteFilter();
      filter.setOrder(NoteSortOrder.UPDATED.getValue());
      filter.setWords("notebook:\"" + TRAVEL_NOTEBOOK_NAME + "\"");

      NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
      spec.setIncludeTitle(true);
      // System.out.println("searching");

      try
      {
        mEvernoteSession
            .getClientFactory()
            .createNoteStoreClient()
            .findNotesMetadata(filter, 0, pageSize, spec,
                new OnClientCallback<NotesMetadataList>()
                {
                  @Override
                  public void onSuccess(NotesMetadataList notes)
                  {

                    // entries.addAll(notes.getNotes());
                    // entries2 = new ArrayList<Note>(entries.size());
                    // final SnippetAdapter adapter = new SnippetAdapter(
                    // SnippetFragment.this.getActivity(), R.layout.snippet,
                    // entries2, mEvernoteSession);
                    // listView.setAdapter(adapter);

                    // Log.e("log_tag ******",
                    // notes.getNotes().get(0).getTitle());
                    // Log.e("log_tag ******", entries.get(0).getTitle());
                    for (int i = 0; i < notes.getNotesSize(); i++)
                    {
                      NoteMetadata snippetEntry = notes.getNotes().get(i);
                      final int position = i;
                      try
                      {
                        mEvernoteSession
                            .getClientFactory()
                            .createNoteStoreClient()
                            .getNote(snippetEntry.getGuid(), true, false,
                                false, false, new OnClientCallback<Note>()
                                {
                                  @Override
                                  public void onSuccess(Note note)
                                  {
                                    // contents.add(android.text.Html.fromHtml(note.getContent()).toString());
                                    // entries2.add(position, note);
                                    // adapter.notifyDataSetChanged();
                                    // snippetText.setText(android.text.Html.fromHtml(note
                                    // .getContent()));
                                    // removeDialog(DIALOG_PROGRESS);
                                    // System.out.println("" + position +
                                    // snippetText.getText());
                                    // Toast.makeText(SnippetFragment.this.getActivity(),
                                    // snippetText.getText(),
                                    // Toast.LENGTH_LONG).show();
                                    // notes = data;
                                    ContentValues values = new ContentValues();
                                    values
                                        .put(
                                            SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
                                            note.getGuid());
                                    values
                                        .put(
                                            SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
                                            note.getTitle());
                                    values
                                        .put(
                                            SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
                                            note.getContent());
                                    SnippetFragment.this
                                        .getActivity()
                                        .getContentResolver()
                                        .insert(
                                            Uri.parse(SouvenirContentProvider.CONTENT_URI
                                                + "/apps"), values);
                                    adapter.notifyDataSetChanged();
                                  }

                                  @Override
                                  public void onException(Exception exception)
                                  {

                                  }
                                });
                      }
                      catch (Exception e)
                      {
                        e.printStackTrace();
                      }

                    }

                    /*
                     * listView.setOnItemClickListener(new OnItemClickListener()
                     * {
                     * 
                     * @Override public void onItemClick(AdapterView<?> parent,
                     * View view, int position, long id) { String item =
                     * entries.get(position).getTitle();
                     * System.out.println("Clicked" + item);
                     * 
                     * Toast.makeText(SnippetFragment.this.getActivity().
                     * getBaseContext(), item, Toast.LENGTH_LONG).show(); //
                     * Intent i = new
                     * Intent(this.getActivity().getApplicationContext(), //
                     * JournalEntry.class); // startActivityForResult(i, 100); }
                     * });
                     */
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
      }
      catch (TTransportException e)
      {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    case R.id.auth_button:

      startAuth(this.getView());

      break;
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
      SNote note = new SNote(cursor.getString(2), cursor.getString(3),
          cursor.getString(3), cursor.getString(0), cursor.getString(0),
          cursor.getString(0), cursor.getString(0), cursor.getString(0));

      // ((AppView) view).setOnAppChangeListener(null);
      ((SnippetView) view).setSNote(note);
      // ((AppView) view).setOnAppChangeListener(this.m_listener);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      SNote note = new SNote(cursor.getString(2), cursor.getString(3),
          cursor.getString(3), cursor.getString(0), cursor.getString(0),
          cursor.getString(0), cursor.getString(0), cursor.getString(0));
      SnippetView sv = new SnippetView(context, note);
      return sv;
    }
  }

  private void checkForTravelNotebook()
  {
    try
    {
      mEvernoteSession.getClientFactory().createNoteStoreClient()
          .listNotebooks(new OnClientCallback<List<Notebook>>()
          {

            @Override
            public void onSuccess(List<Notebook> notebookList)
            {
              for (Notebook notebook : notebookList)
              {
                if ((notebook.getName().toString())
                    .equals(TRAVEL_NOTEBOOK_NAME))
                {
                  NOTEBOOK_GUID = notebook.getGuid();
                  // listViewCreate();
                  return;
                }
              }
              // Travel Notebook not found/created
              Notebook notebook = new Notebook();
              notebook.setName(TRAVEL_NOTEBOOK_NAME);
              try
              {
                mEvernoteSession.getClientFactory().createNoteStoreClient()
                    .createNotebook(notebook, new OnClientCallback<Notebook>()
                    {

                      @Override
                      public void onSuccess(Notebook created)
                      {
                        NOTEBOOK_GUID = created.getGuid();
                      }

                      @Override
                      public void onException(Exception exception)
                      {
                        Toast.makeText(getActivity().getApplicationContext(),
                            "Warning: Travel Notebook not created.",
                            Toast.LENGTH_LONG).show();
                        exception.printStackTrace();
                      }

                    });

                // listViewCreate();
              }
              catch (TTransportException e)
              {
                e.printStackTrace();
              }
            }

            @Override
            public void onException(Exception exception)
            {

            }

          });
    }
    catch (TTransportException e1)
    {
      e1.printStackTrace();
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    String[] projection = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT };

    Uri uri = Uri.parse(SouvenirContentProvider.CONTENT_URI + "/apps");

    CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,
        projection, null, null, "");
    return cursorLoader;
    // return null;
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
}