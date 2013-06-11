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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
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

/**
 * This simple Android app demonstrates how to integrate with the Evernote API
 * (aka EDAM).
 * <p/>
 * In this sample, the user authorizes access to their account using OAuth and
 * chooses an image from the device's image gallery. The image is then saved
 * directly to user's Evernote account as a new note.
 */
public class SnippetFragment extends ParentFragment implements OnClickListener
{
  private static final int SNIPPET_PAGE_SIZE = 5;
  private static String TRAVEL_NOTEBOOK_NAME = "Travel Notebook";
  private static String NOTEBOOK_GUID;

  // UI elements that we update
  private Button mBtnAuth;

  Button btnTakePhoto;
  ImageView imgTakenPhoto;
  final String TAG1 = "MyCamera";

  public ArrayList<NoteMetadata> entries;
  public ArrayList<Note> entries2;

  ImageLoader imageLoader;
  DisplayImageOptions options;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_snippet, container, false);
    mBtnAuth = (Button) view.findViewById(R.id.auth_button);
    mBtnAuth.setOnClickListener(this);
    checkForTravelNotebook();
    return view;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    updateUi();
  }

  /*
   * @Override public Object onRetainNonConfigurationInstance() { return
   * mImageData; }
   */
  /*
   * // using createDialog, could use Fragments instead
   * 
   * @SuppressWarnings("deprecation")
   * 
   * @Override protected Dialog onCreateDialog(int id) { switch (id) { case
   * DIALOG_PROGRESS: return new ProgressDialog(HelloEDAM.this); } return
   * super.onCreateDialog(id); }
   */

  /*
   * @Override protected void onPrepareDialog(int id, Dialog dialog) { switch
   * (id) { case DIALOG_PROGRESS: ((ProgressDialog)
   * dialog).setIndeterminate(true); dialog.setCancelable(false);
   * ((ProgressDialog) dialog).setMessage(getString(R.string.loading)); } }
   */

  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateUi()
  {
    if (mEvernoteSession.isLoggedIn())
    {
      // mBtnAuth.setText(R.string.label_log_out);
      View b = this.getView().findViewById(R.id.auth_button);
      b.setVisibility(View.GONE);
      listViewCreate();
    }
    else
    {
      View b = this.getView().findViewById(R.id.auth_button);
      b.setVisibility(View.VISIBLE);
      // mBtnAuth.setText(R.string.label_log_in);
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

  /***************************************************************************
   * The remaining code in this class simply demonstrates the use of the *
   * Evernote API once authnetication is complete. You don't need any of it * in
   * your application. *
   ***************************************************************************/

  /**
   * Called when the control returns from an activity that we launched.
   */
  /*
   * @Override public void onActivityResult(int requestCode, int resultCode,
   * Intent data) { super.onActivityResult(requestCode, resultCode, data);
   * switch (requestCode) { //Update UI when oauth activity returns result case
   * EvernoteSession.REQUEST_CODE_OAUTH: if (resultCode == Activity.RESULT_OK) {
   * updateUi(); } break; //Grab image data when picker returns result case
   * SELECT_IMAGE: if (resultCode == Activity.RESULT_OK) { new
   * ImageSelector().execute(data); } break; case CAMERA_PIC_REQUEST:
   * if(resultCode == RESULT_OK) { new ImageSelector().execute(data);
   * 
   * 
   * } } }
   */

  // using removeDialog, could use Fragments instead
  // @SuppressWarnings("deprecation")
  // @Override
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
    // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
    entries = new ArrayList<NoteMetadata>();

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

                    ListView listView = (ListView) SnippetFragment.this
                        .getView().findViewById(R.id.lview);
                    entries.addAll(notes.getNotes());
                    entries2 = new ArrayList<Note>(entries.size());
                    final SnippetAdapter adapter = new SnippetAdapter(
                        SnippetFragment.this.getActivity(), R.layout.snippet,
                        entries2, mEvernoteSession);
                    listView.setAdapter(adapter);
                    listView.setScrollingCacheEnabled(false);

                    // Log.e("log_tag ******",
                    // notes.getNotes().get(0).getTitle());
                    // Log.e("log_tag ******", entries.get(0).getTitle());
                    for (int i = 0; i < entries.size(); i++)
                    {
                      NoteMetadata snippetEntry = entries.get(i);
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
                                    entries2.add(position, note);
                                    adapter.notifyDataSetChanged();
                                    // snippetText.setText(android.text.Html.fromHtml(note
                                    // .getContent()));
                                    // removeDialog(DIALOG_PROGRESS);
                                    // System.out.println("" + position +
                                    // snippetText.getText());
                                    // Toast.makeText(SnippetFragment.this.getActivity(),
                                    // snippetText.getText(),
                                    // Toast.LENGTH_LONG).show();
                                    // notes = data;
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

  public class SnippetAdapter extends ArrayAdapter<Note>
  {
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    int resource;
    String response;
    Context context;
    private EvernoteSession mEvernoteSession;
    DisplayImageOptions options;
    ArrayList<String> contents = new ArrayList<String>();

    // Initialize adapter
    public SnippetAdapter(Context context, int resource, List<Note> items,
        EvernoteSession mEvernoteSession)
    {
      super(context, resource, items);
      this.resource = resource;
      this.context = context;
      this.mEvernoteSession = mEvernoteSession;

      options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
          .showStubImage(R.drawable.traveljournal).build();

    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {

      RelativeLayout snippetView = (RelativeLayout) convertView;
      // Get the current alert object
      final Note snippetEntry = getItem(position);

      // Inflate the view
      if (convertView == null)
      {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        snippetView = (RelativeLayout) inflater.inflate(R.layout.snippet, null);

      }
      else
      {
        snippetView = (RelativeLayout) convertView;
      }

      snippetView.setOnClickListener(new OnClickListener()
      {
        @Override
        public void onClick(View v)
        {
          String item = snippetEntry.getTitle();
          // System.out.println("Clicked" + item);

          // Toast.makeText(SnippetFragment.this.getActivity().getBaseContext(),item,
          // Toast.LENGTH_LONG).show();

          Intent intent = new Intent(SnippetFragment.this.getActivity(),
              EntryActivity.class).putExtra("note", snippetEntry);
          SnippetFragment.this.getActivity()
              .startActivityForResult(intent, 300);

        }
      });

      // Get the text boxes from the listitem.xml file
      TextView snippetEvent = (TextView) snippetView
          .findViewById(R.id.snippetEvent);
      final TextView snippetLocation = (TextView) snippetView
          .findViewById(R.id.snippetLocation);
      final TextView snippetText = (TextView) snippetView
          .findViewById(R.id.snippetText);
      ImageView snippetPic = (ImageView) snippetView
          .findViewById(R.id.snippetPic);
      snippetText.setText("");

      imageLoader.displayImage(
          "" + mEvernoteSession.getAuthenticationResult().getWebApiUrlPrefix()
              + "thm/note/" + snippetEntry.getGuid() + "?auth="
              + mEvernoteSession.getAuthToken(), snippetPic, options);

      String location = snippetEntry.getAttributes().getPlaceName();
      if (location == null)
      {
        location = String.valueOf((snippetEntry.getAttributes().getLatitude())
            + String.valueOf(snippetEntry.getAttributes().getLongitude()));
      }

      snippetLocation.setText(location);
      System.out.println("LOCATION: " + location);
      snippetEvent.setText(snippetEntry.getTitle().toUpperCase());
      snippetText.setText(android.text.Html.fromHtml(snippetEntry.getContent())
          .toString());

      // snippetText.setText(Integer.valueOf(position).toString());
      // snippetLocation.setText("Evernote Hack");

      return snippetView;
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
}