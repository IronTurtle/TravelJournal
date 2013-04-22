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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
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
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.transport.TTransportException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This simple Android app demonstrates how to integrate with the Evernote API
 * (aka EDAM).
 * <p/>
 * In this sample, the user authorizes access to their account using OAuth and
 * chooses an image from the device's image gallery. The image is then saved
 * directly to user's Evernote account as a new note.
 */
public class NoteFragment extends ParentFragment implements OnClickListener {

	Uri mImageUri;

	// Activity result request codes
	private static final int SELECT_IMAGE = 1;

	// UI elements that we update
	@SuppressWarnings("unused")
	private Button mBtnAuth;
	private Button mBtnSave;
	@SuppressWarnings("unused")
	private Button mBtnSelect;
	@SuppressWarnings("unused")
	private EditText mTextArea;
	private ImageView mImageView;

	Button btnTakePhoto;
	private static final int CAMERA_PIC_REQUEST = 1313;
	private static final int LOCATION_REQUEST = 1034;
	final String TAG = "MyCamera";

	// The path to and MIME type of the currently selected image from the
	// gallery
	private class ImageData {
		public Bitmap imageBitmap;
		public String filePath;
		public String mimeType;
		public String fileName;
	}

	// Note fields
	EditText mTitle;
	TextView mLocation;
	EditText mEntry;

	private ImageData mImageData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_note, container, false);
		mBtnAuth = (Button) view.findViewById(R.id.auth_button);
		mBtnSelect = (Button) view.findViewById(R.id.select_button);
		mBtnSave = (Button) view.findViewById(R.id.save_button);
		mImageView = (ImageView) view.findViewById(R.id.note_image);
		mTitle = (EditText) view.findViewById(R.id.note_title);
		mLocation = (TextView) view.findViewById(R.id.note_location);
		mEntry = (EditText) view.findViewById(R.id.note_entry);

		btnTakePhoto = (Button) view.findViewById(R.id.camera_button);

		mLocation.setOnClickListener(new btnFindPlace());
		mBtnSave.setOnClickListener(this);
		btnTakePhoto.setOnClickListener(new btnTakePhotoClicker());
		mEntry.setOnKeyListener(new NoteEntryField());
		/*
		 * if (getLastNonConfigurationInstance() != null) { mImageData =
		 * (ImageData) getLastNonConfigurationInstance();
		 * mImageView.setImageBitmap(mImageData.imageBitmap); }
		 */
		return view;
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/*
	 * @Override public Object onRetainNonConfigurationInstance() { return
	 * mImageData; }
	 */

	/***************************************************************************
	 * The remaining code in this class simply demonstrates the use of the *
	 * Evernote API once authnetication is complete. You don't need any of it *
	 * in your application. *
	 ***************************************************************************/

	/**
	 * Called when the control returns from an activity that we launched.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		// Grab image data when picker returns result
		case SELECT_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				mImageUri = data.getData();

				new ImageSelector().execute(data);
			}
			break;
		case CAMERA_PIC_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Log.e("Intent value:", data.toString());
					mImageUri = data.getData();
				} else {
					Log.e("Intent is null", "yep it is.");
					if (mImageUri == null) {
						Log.e("nullcheck on memberimageuri", "its null");
					} else {
						Log.e("nullcheckon memberimage", mImageUri.toString());
					}
				}

				new ImageSelector().execute(data);
			}
			break;
		case LOCATION_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Log.e("Intent value:", data.toString());
					this.mLocation
							.setText(data.getStringExtra("LOCATION_DATA"));
				} else {
					Log.e("Intent is null", "yep it is.");
					if (mImageUri == null) {
						Log.e("nullcheck on memberimageuri", "its null");
					} else {
						Log.e("nullcheckon memberimage", mImageUri.toString());
					}
				}
			}
			break;
		}
	}

	/**
	 * Button to capture image for note
	 * 
	 * @author ironsuturtle Sends the user to the camera application to take a
	 *         photo and save
	 */
	class NoteEntryField implements EditText.OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			// updateUi();
			return false;
		}
	}

	/**
	 * Button to capture image for note
	 * 
	 * @author ironsuturtle Sends the user to the camera application to take a
	 *         photo and save
	 */
	class btnTakePhotoClicker implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			ContentValues values = new ContentValues();

			mImageUri = NoteFragment.this
					.getActivity()
					.getApplicationContext()
					.getContentResolver()
					.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							values);
			if (mImageUri == null) {
				Log.e("image uri is null", "what?");
			} else {

				Log.e("oh nevermind", "image uri is NOT null");
			}
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
		}
	}

	class btnFindPlace implements Button.OnClickListener {
		@Override
		public void onClick(View v) {

			// Show Place Finder Fragment
			Toast.makeText(getActivity(), "Location Field clicked",
					Toast.LENGTH_SHORT).show();
			startActivityForResult(new Intent(getActivity(),
					PlacesActivity.class).putExtra("PREV_LOC_DATA", mLocation
					.getText().toString()), LOCATION_REQUEST);
		}
	}

	/**
	 * Saves text field content as note to selected notebook, or default
	 * notebook if no notebook select
	 */
	public void saveNote(View view) {
		Resource resource = new Resource();

		ImageData imageData = mImageData;
		String f = imageData.filePath;
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(f));

			FileData data = new FileData(EvernoteUtil.hash(in), new File(f));
			in.close();

			resource.setData(data);
			resource.setMime(imageData.mimeType);
			ResourceAttributes attributes = new ResourceAttributes();
			attributes.setFileName(imageData.fileName);
			resource.setAttributes(attributes);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Time now = new Time();
		now.setToNow();
		String title = now.toString();//mTitle.getText().toString();
		String content = mTitle.getText().toString() + "\n"  + mEntry.getText().toString();
		String location = mLocation.getText().toString();

		Note note = new Note();
		note.setTitle(title);
		System.out.println("Note Title: " + note.getTitle());

		note.addToResources(resource);
		note.setContent(EvernoteUtil.NOTE_PREFIX + "<p>" + "Location: "
				+ location + "\n" + content + "</p>"
				+ EvernoteUtil.createEnMediaTag(resource)
				+ EvernoteUtil.NOTE_SUFFIX);

		try {
			
			mEvernoteSession.getClientFactory().createNoteStoreClient()
					.createNote(note, new OnClientCallback<Note>() {

			@Override
			public void onSuccess(Note data) {
				NoteFragment.this.clearForm(NoteFragment.this
						.getView());
				Toast.makeText(
						NoteFragment.this.getActivity()
								.getApplicationContext(),
						R.string.msg_image_saved, Toast.LENGTH_LONG)
						.show();
			}

			@Override
			public void onException(Exception exception) {
				NoteFragment.this.clearForm(NoteFragment.this
						.getView());
				exception.printStackTrace();
				Toast.makeText(
						NoteFragment.this.getActivity()
								.getApplicationContext(),
						R.string.err_creating_note,
						Toast.LENGTH_LONG).show();
			}
		});
		} catch (TTransportException exception) {
			exception.printStackTrace();
		}

	}

	/**
	 * Called when the user taps the "Select Image" button.
	 * <p/>
	 * Sends the user to the image gallery to choose an image to share.
	 */
	public void startSelectImage(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(intent, SELECT_IMAGE);
	}

	public void clearForm(View view) {
		mImageView.setImageResource(android.R.color.transparent);
		mTitle.setText("");
		mLocation.setText("");
		mEntry.setText("");
	}

	/**
	 * Called when control returns from the image gallery picker. Loads the
	 * image that the user selected.
	 */
	private class ImageSelector extends AsyncTask<Intent, Void, ImageData> {

		// using showDialog, could use Fragments instead
		@Override
		protected void onPreExecute() {
			// showDialog(DIALOG_PROGRESS);
		}

		/**
		 * The callback from the gallery contains a pointer into a table. Look
		 * up the appropriate record and pull out the information that we need,
		 * in this case, the path to the file on disk, the file name and the
		 * MIME type.
		 * 
		 * @param intents
		 * @return
		 */
		// using Display.getWidth and getHeight on older SDKs
		@SuppressWarnings("deprecation")
		@Override
		// suppress lint check on Display.getSize(Point)
		@TargetApi(16)
		protected ImageData doInBackground(Intent... intents) {
			/*
			 * if (intents == null || intents.length == 0) { return null; }
			 */

			Uri selectedImage = mImageUri;
			String[] queryColumns = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.MIME_TYPE,
					MediaStore.Images.Media.DISPLAY_NAME };

			Cursor cursor = null;
			ImageData image = null;
			try {
				cursor = NoteFragment.this.getActivity()
						.getApplicationContext().getContentResolver()
						.query(selectedImage, queryColumns, null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					image = new ImageData();

					image.filePath = cursor.getString(cursor
							.getColumnIndex(queryColumns[1]));
					image.mimeType = cursor.getString(cursor
							.getColumnIndex(queryColumns[2]));
					image.fileName = cursor.getString(cursor
							.getColumnIndex(queryColumns[3]));

					// First decode with inJustDecodeBounds=true to check
					// dimensions
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;

					Bitmap tempBitmap = BitmapFactory.decodeFile(
							image.filePath, options);

					int dimen = 0;
					int x = 0;
					int y = 0;

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
						Point size = new Point();
						NoteFragment.this.getActivity().getWindowManager()
								.getDefaultDisplay().getSize(size);

						x = size.x;
						y = size.y;
					} else {
						x = NoteFragment.this.getActivity().getWindowManager()
								.getDefaultDisplay().getWidth();
						y = NoteFragment.this.getActivity().getWindowManager()
								.getDefaultDisplay().getHeight();
					}

					dimen = x < y ? x : y;

					// Calculate inSampleSize
					options.inSampleSize = calculateInSampleSize(options,
							dimen, dimen);

					// Decode bitmap with inSampleSize set
					options.inJustDecodeBounds = false;

					tempBitmap = BitmapFactory.decodeFile(image.filePath,
							options);

					image.imageBitmap = Bitmap.createScaledBitmap(tempBitmap,
							dimen, dimen, true);
					tempBitmap.recycle();

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "Error retrieving image");
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			return image;
		}

		/**
		 * Calculates a sample size to be used when decoding a bitmap if you
		 * don't require (or don't have enough memory) to load the full size
		 * bitmap.
		 * <p/>
		 * <p>
		 * This function has been taken form Android's training materials,
		 * specifically the section about "Loading Large Bitmaps Efficiently".
		 * <p>
		 * 
		 * @param options
		 *            a BitmapFactory.Options object, obtained from decoding
		 *            only the bitmap's bounds.
		 * @param reqWidth
		 *            The required minimum width of the decoded bitmap.
		 * @param reqHeight
		 *            The required minimum height of the decoded bitmap.
		 * @return the sample size needed to decode the bitmap to a size that
		 *         meets the required width and height.
		 * @see <a
		 *      href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap">Load
		 *      a Scaled Down Version into Memory</a>
		 */
		protected int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				if (width > height) {
					inSampleSize = Math.round((float) height
							/ (float) reqHeight);
				} else {
					inSampleSize = Math.round((float) width / (float) reqWidth);
				}
			}
			return inSampleSize;
		}

		/**
		 * Sets the image to the background and enables saving it to evernote
		 * 
		 * @param image
		 */
		@Override
		protected void onPostExecute(ImageData image) {
			// removeDialog(DIALOG_PROGRESS);

			if (image == null) {
				Toast.makeText(
						NoteFragment.this.getActivity().getApplicationContext(),
						R.string.err_image_selected, Toast.LENGTH_SHORT).show();
				return;
			}

			if (image.imageBitmap != null) {
				mImageView.setImageBitmap(image.imageBitmap);
			}

			if (mEvernoteSession.isLoggedIn()) {
				mBtnSave.setEnabled(true);
			}

			mImageData = image;
		}
	}

	/*
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState); if (mImageUri != null) {
	 * outState.putString("cameraImageUri", mImageUri.toString()); } }
	 * 
	 * @Override protected void onRestoreInstanceState(Bundle
	 * savedInstanceState) { super.onRestoreInstanceState(savedInstanceState);
	 * if (savedInstanceState.containsKey("cameraImageUri")) { mImageUri =
	 * Uri.parse(savedInstanceState.getString("cameraImageUri")); } }
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_button:
			System.out.println("Save pressed");
			saveNote(this.getView());

			break;
		}
		// TODO Auto-generated method stub

	}
}
