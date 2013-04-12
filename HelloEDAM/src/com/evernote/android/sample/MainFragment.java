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

import android.widget.ListView;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import android.annotation.TargetApi;
//import android.app.ActionBar;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.os.AsyncTask;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;


/**
 * This simple Android app demonstrates how to integrate with the Evernote API
 * (aka EDAM).
 * <p/>
 * In this sample, the user authorizes access to their account using OAuth and
 * chooses an image from the device's image gallery. The image is then saved
 * directly to user's Evernote account as a new note.
 */
public class MainFragment extends SherlockFragment implements ActionBar.TabListener{

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
	private static final String EVERNOTE_HOST = EvernoteSession.HOST_SANDBOX;

	/**
	 * ************************************************************************
	 * The following values are simply part of the demo application. *
	 * *************************************************************************
	 */

	// Activity result request codes
	private static final int SELECT_IMAGE = 1;

	// Used to interact with the Evernote web service
	private EvernoteSession mEvernoteSession;

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

	// The path to and MIME type of the currently selected image from the
	// gallery
	private class ImageData {
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
    View view = inflater.inflate(R.layout.fragment_main, container, false);
    mBtnAuth = (Button) view.findViewById(R.id.auth_button);
    
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        this.getActivity().getApplicationContext())
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory().enableLogging() // Not
                                    // necessary
                                    // in
                                    // common
        .build();
    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(config);
    

    setupSession();
    
    return view;
  }
	
	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  
		super.onCreate(savedInstanceState);


		
		/*
		 * mBtnAddNote = (Button) findViewById(R.id.menu_add_note);
		 */

		
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUi();
	}
/*
	@Override
	public Object onRetainNonConfigurationInstance() {
		return mImageData;
	}
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
	 * Setup the EvernoteSession used to access the Evernote API.
	 */
	private void setupSession() {

		// Retrieve persisted authentication information
		mEvernoteSession = EvernoteSession.init(this.getActivity(), CONSUMER_KEY,
				CONSUMER_SECRET, EVERNOTE_HOST, null);
	}

	/**
	 * Update the UI based on Evernote authentication state.
	 */
	private void updateUi() {
		if (mEvernoteSession.isLoggedIn()) {
			// mBtnAuth.setText(R.string.label_log_out);
			View b = this.getView().findViewById(R.id.auth_button);
			b.setVisibility(View.GONE);
			listViewCreate();
		} else {
			View b = this.getView().findViewById(R.id.auth_button);
			b.setVisibility(View.VISIBLE);
			// mBtnAuth.setText(R.string.label_log_in);
		}
	}

	/**
	 * Called when the user taps the "Log in to Evernote" button. Initiates the
	 * Evernote OAuth process, or logs out if the user is already logged in.
	 */
	public void startAuth(View view) {
		if (mEvernoteSession.isLoggedIn()) {
			mEvernoteSession.logOut(this.getActivity().getApplicationContext());
		} else {
			mEvernoteSession.authenticate(this.getActivity());
		}
		updateUi();
	}

	public void addNoteOnClick(View view) {

		Intent intent = new Intent(this.getActivity().getApplicationContext(), NoteFragment.class);

		this.startActivityForResult(intent, 200);

	}

	public void goToItineraryOnClick(View view) {
		Intent intent = new Intent(this.getActivity().getApplicationContext(),
				ItineraryActivity.class);

		this.startActivityForResult(intent, 100);
	}

	/***************************************************************************
	 * The remaining code in this class simply demonstrates the use of the *
	 * Evernote API once authnetication is complete. You don't need any of it *
	 * in your application. *
	 ***************************************************************************/

	/**
	 * Called when the control returns from an activity that we launched.
	 */
	/*
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { super.onActivityResult(requestCode, resultCode, data);
	 * switch (requestCode) { //Update UI when oauth activity returns result
	 * case EvernoteSession.REQUEST_CODE_OAUTH: if (resultCode ==
	 * Activity.RESULT_OK) { updateUi(); } break; //Grab image data when picker
	 * returns result case SELECT_IMAGE: if (resultCode == Activity.RESULT_OK) {
	 * new ImageSelector().execute(data); } break; case CAMERA_PIC_REQUEST:
	 * if(resultCode == RESULT_OK) { new ImageSelector().execute(data);
	 * 
	 * 
	 * } } }
	 */

	// using removeDialog, could use Fragments instead
	// @SuppressWarnings("deprecation")
	// @Override
	protected void onPostExecute(Note note) {
		this.getActivity().removeDialog(DIALOG_PROGRESS);

		if (note == null) {
			Toast.makeText(this.getActivity().getApplicationContext(), R.string.err_creating_note,
					Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(this.getActivity().getApplicationContext(), R.string.msg_image_saved,
				Toast.LENGTH_LONG).show();
	}

	public void update() {
		new EntryUpdater().execute("");

		String item = "clicked1";
		// Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
	}

	public void listViewCreate() {
		String item = "clicked2";
		// Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
		entries = new ArrayList<NoteMetadata>();
		update();
	}

	private class EntryUpdater extends
			AsyncTask<String, Void, NotesMetadataList> {
		// using Display.getWidth and getHeight on older SDKs
		@SuppressWarnings("deprecation")
		// suppress lint check on Display.getSize(Point)
		@TargetApi(16)
		@Override
		protected NotesMetadataList doInBackground(String... strings) {
			int pageSize = 10;
			NoteFilter filter = new NoteFilter();
			filter.setOrder(NoteSortOrder.UPDATED.getValue());
			filter.setWords("-tag:itinerary*");

			NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
			spec.setIncludeTitle(true);
			try {
				NotesMetadataList notes = mEvernoteSession.createNoteStore()
						.findNotesMetadata(mEvernoteSession.getAuthToken(),
								filter, 0, pageSize, spec);
				return notes;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new NotesMetadataList();
		}

		@Override
		protected void onPostExecute(NotesMetadataList notes) {
			entries.addAll(notes.getNotes());
			Log.e("log_tag ******", notes.getNotes().get(0).getTitle());
			Log.e("log_tag ******", entries.get(0).getTitle());
			for (NoteMetadata note2 : notes.getNotes()) {
				System.out.println(note2.getTitle());
			}

			ListView listView = (ListView) MainFragment.this.getView().findViewById(R.id.lview);
			SnippetAdapter adapter = new SnippetAdapter(MainFragment.this.getActivity(),
					R.layout.snippet, entries, mEvernoteSession);
			listView.setAdapter(adapter);
			listView.setScrollingCacheEnabled(false);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// String item = "clicked3";
					// Toast.makeText(getBaseContext(), item,
					// Toast.LENGTH_LONG).show();
					// Intent i = new Intent(this.getActivity().getApplicationContext(),
					// JournalEntry.class);
					// startActivityForResult(i, 100);
				}
			});
		}
	}

  @Override
  public void onTabSelected(com.actionbarsherlock.app.ActionBar.Tab tab,
      android.support.v4.app.FragmentTransaction ft)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onTabUnselected(com.actionbarsherlock.app.ActionBar.Tab tab,
      android.support.v4.app.FragmentTransaction ft)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onTabReselected(com.actionbarsherlock.app.ActionBar.Tab tab,
      android.support.v4.app.FragmentTransaction ft)
  {
    // TODO Auto-generated method stub
    
  }

}