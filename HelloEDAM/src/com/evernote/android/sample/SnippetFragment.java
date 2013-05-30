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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import android.widget.ListView;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Note;
import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;
import com.evernote.thrift.transport.TTransportException;

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

  // Your Evernote API key. See http://dev.evernote.com/documentation/cloud/
  // Please obfuscate your code to help keep these values secret.
  private static final String CONSUMER_KEY = "ironsuturtle";
  private static final String CONSUMER_SECRET = "e0441c112aab58f6";

  /**
   * ************************************************************************
   * Change these values as needed to use this code in your own application. *
   * *************************************************************************
   */

  // Name of this application, for logging
  private static final String TAG = "HelloEDAM";

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

  // Activity result request codes
  private static final int SELECT_IMAGE = 1;

  // UI elements that we update
  private Button mBtnAuth;
  private Button mBtnSave;
  private Button mBtnSelect;
  private Button mBtnAddNote;
  private EditText mTextArea;
  private ImageView mImageView;
  private final int DIALOG_PROGRESS = 101;

  Button btnTakePhoto;
  ImageView imgTakenPhoto;
  private static final int CAMERA_PIC_REQUEST = 1313;
  final String TAG1 = "MyCamera";

  public ArrayList<NoteMetadata> entries;
  public ArrayList<Note> entries2;

  // The path to and MIME type of the currently selected image from the
  // gallery
  private class ImageData
  {
    public Bitmap imageBitmap;
    public String filePath;
    public String mimeType;
    public String fileName;
  }

  private ImageData mImageData;
  ImageLoader imageLoader;
  DisplayImageOptions options;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_snippet, container, false);
    mBtnAuth = (Button) view.findViewById(R.id.auth_button);
    mBtnAuth.setOnClickListener(this);
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this
        .getActivity().getApplicationContext())
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()/* .enableLogging() */.build();
    ImageLoader.getInstance().init(config);

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
      } catch (InvalidAuthenticationException e)
      {
        // TODO Auto-generated catch block
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
    this.getActivity().removeDialog(DIALOG_PROGRESS);

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
    String item = "clicked2";
    // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
    entries = new ArrayList<NoteMetadata>();

    // update();
    if (mEvernoteSession.isLoggedIn())
    {
      int pageSize = 10;

      NoteFilter filter = new NoteFilter();
      filter.setOrder(NoteSortOrder.UPDATED.getValue());
      //filter.setWords("-tag:itinerary*");

      NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
      spec.setIncludeTitle(true);
      System.out.println("searching");

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
                    // removeDialog(DIALOG_PROGRESS);
                    // Toast.makeText(getApplicationContext(),
                    // R.string.msg_image_saved, Toast.LENGTH_LONG).show();
                    // notes = data;

                    ListView listView = (ListView) SnippetFragment.this
                        .getView().findViewById(R.id.lview);
                    entries.addAll(notes.getNotes());
                    entries2 = new ArrayList<Note>(entries.size());
                    final SnippetAdapter adapter = new SnippetAdapter(
                        SnippetFragment.this.getActivity(), R.layout.snippet,
                        entries2, mEvernoteSession);
                    listView.setAdapter(adapter);
                    listView.setScrollingCacheEnabled(false);


                    Log.e("log_tag ******", notes.getNotes().get(0).getTitle());
                    Log.e("log_tag ******", entries.get(0).getTitle());
                    for (NoteMetadata note2 : notes.getNotes())
                    {
                      System.out.println(note2.getTitle());
                    }
                    for (int i = 0; i < entries.size(); i++)
                    {
                      NoteMetadata snippetEntry = entries.get(i);
                      final int position = i;
                      try
                      {
                        mEvernoteSession
                            .getClientFactory()
                            .createNoteStoreClient()
                            .getNote(snippetEntry.getGuid(), true, true, true,
                                true, new OnClientCallback<Note>()
                                {
                                  @Override
                                  public void onSuccess(Note note)
                                  {
                                    // contents.add(android.text.Html.fromHtml(note.getContent()).toString());
                                    entries2.add(position, note);
                                    adapter.notifyDataSetChanged();
                                    for (Note note2 : entries2)
                                    {
                                      System.out.println(note2.getTitle());
                                    }
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
                      } catch (Exception e)
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
      } catch (TTransportException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub
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
          System.out.println("Clicked" + item);

          Toast.makeText(SnippetFragment.this.getActivity().getBaseContext(),
              item, Toast.LENGTH_LONG).show();

          Intent intent = new Intent(SnippetFragment.this.getActivity(),
              EntryActivity.class).putExtra("title", item).putExtra("guid",
              snippetEntry.getGuid());
          SnippetFragment.this.getActivity()
              .startActivityForResult(intent, 300);

        }
      });

      // Get the text boxes from the listitem.xml file
      TextView snippetEvent = (TextView) snippetView
          .findViewById(R.id.snippetEvent);
      TextView snippetLocation = (TextView) snippetView
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

      snippetEvent.setText(snippetEntry.getTitle().toUpperCase());

      snippetText.setText(android.text.Html.fromHtml(snippetEntry.getContent())
          .toString());

      // snippetText.setText(Integer.valueOf(position).toString());
      snippetLocation.setText("Evernote Hack");

      return snippetView;
    }
  }

}
