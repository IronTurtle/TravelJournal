package com.souvenir.android;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.view.MenuItem;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.LazyMap;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteAttributes;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.transport.TTransportException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.internal.Logger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("serial")
public class EntryFragment extends ParentFragment implements OnClickListener,
    Serializable
{

  // The path to and MIME type of the currently selected image from the
  // gallery
  @SuppressWarnings("unused")
  private class ImageData
  {
    public Bitmap imageBitmap;
    public String filePath;
    public String mimeType;
    public String fileName;
  }

  protected ImageLoader imageLoader = ImageLoader.getInstance();
  private final int FACEBOOK_SHARE = 6135;
  DisplayImageOptions options;

  // Note fields
  ImageView mImageView;
  ImageData mImageData = new ImageData();
  EditText mTitle;
  TextView mLocation;
  EditText mEntry;

  private SessionState state = SessionState.OPENED_TOKEN_UPDATED;
  
  Note note = new Note();
  Resource resource = new Resource();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_entry, container, false);
    setHasOptionsMenu(true);

    mImageView = (ImageView) view.findViewById(R.id.entry_image);
    mTitle = (EditText) view.findViewById(R.id.entry_title);
    mLocation = (TextView) view.findViewById(R.id.entry_location);
    mEntry = (EditText) view.findViewById(R.id.entry_entry);

    options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
        .showStubImage(R.drawable.traveljournal).build();

    // getMetadata();
    Bundle bundle = this.getActivity().getIntent().getExtras();

    String guid = (String) bundle.get("guid");
    note.setGuid(guid);
    String title = (String) bundle.get("title");
    displayNote(guid, title);
    
    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // This uses the imported MenuItem from ActionBarSherlock
    //Toast.makeText(this.getActivity(), "Got click: " + item.toString(),Toast.LENGTH_SHORT).show();
    switch (item.getItemId())
    {
    case R.id.viewedit_note_menu_save:
	      System.out.println("Save pressed");
	      //Toast.makeText(getActivity(), "Save Button clicked", Toast.LENGTH_SHORT).show();
	      updateNote(this.getView());
	      break;
    case R.id.viewedit_note_menu_fbshare:
    	System.out.println("FACEBOOK SHARING");
    	getActivity().startActivityForResult(new Intent(getActivity(), ShareActivity.class), FACEBOOK_SHARE);
    	break;
    /*
     * case R.id.create_note_menu_camera: //startActivity(new Intent(this,
     * NoteActivity.class));
     * 
     * Intent cameraIntent = new Intent(
     * android.provider.MediaStore.ACTION_IMAGE_CAPTURE); ContentValues values =
     * new ContentValues();
     * 
     * mImageUri = getActivity().getApplicationContext() .getContentResolver()
     * .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); if
     * (mImageUri == null) { Log.e("image uri is null", "what?"); } else {
     * 
     * Log.e("oh nevermind", "image uri is NOT null"); }
     * cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
     * startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
     * 
     * break;
     * 
     * case R.id.create_note_menu_select: //startActivity(new Intent(this,
     * EntryActivity.class));
     * 
     * Intent intent = new Intent(Intent.ACTION_PICK,
     * MediaStore.Images.Media.INTERNAL_CONTENT_URI);
     * startActivityForResult(intent, SELECT_IMAGE); break;
     * 
     * case R.id.create_note_menu_trophy: //startActivity(new Intent(this,
     * EntryActivity.class)); Toast.makeText(getActivity(),
     * "Trophy Button clicked", Toast.LENGTH_SHORT).show(); break;
     */
    }
    return true;
  }
  
  @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			// Grab image data when picker returns result
			case FACEBOOK_SHARE:
			if (resultCode == Activity.RESULT_OK) {
				((EntryActivity)getActivity()).finish();
				getFragmentManager().popBackStack();
				
			}
			break;
		}
		
	}
  
  
  private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}

  public void getMetadata()
  {
    int pageSize = 10;

    NoteFilter filter = new NoteFilter();
    filter.setOrder(NoteSortOrder.UPDATED.getValue());
    filter.setWords("-tag:itinerary*");

    NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
    spec.setIncludeTitle(true);

    try
    {
      if (mEvernoteSession.isLoggedIn())
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
                    displayNote(notes.getNotes().get(0).getGuid(), notes
                        .getNotes().get(0).getTitle());
                  }

                  @Override
                  public void onException(Exception exception)
                  {

                  }
                });
      }
    }
    catch (TTransportException e)
    {

    }
  }

  public void displayNote(final String guid, String title)
  {

    mTitle.setText(title.toUpperCase(Locale.US));
    try
    {
      // Set location to correct field
      mEvernoteSession.getClientFactory().createNoteStoreClient()
          .getNoteApplicationData(guid, new OnClientCallback<LazyMap>()
          {
            public void onSuccess(LazyMap resources)
            {

              System.out.println(resources.getFullMap().values());
              String location = resources.getFullMap().get("LOCATION");
              System.out.println("LOCATION: " + location);
              mLocation.setText(location);
            }

            @Override
            public void onException(Exception exception)
            {
              exception.printStackTrace();
            }
          });

      System.out.println("Getting Note data");
      mEvernoteSession.getClientFactory().createNoteStoreClient()
          .getNoteContent(guid, new OnClientCallback<String>()
          {
            @Override
            public void onSuccess(String noteContent)
            {

              System.out.println("Getting note content...");
              String contents = noteContent;

              System.out.println("Got note content");

              mEntry.setText(android.text.Html.fromHtml(contents));

              Document doc = Jsoup.parse(contents);
              System.out.println("contents");
              System.out.println(contents);
              Elements divs = doc.getElementsByAttribute("hash");
              System.out.println("Images");
              for (Element div : divs)
              {
                System.out.println(div.attr("hash"));
                // TODO: may be wrong...
                mImageData.fileName = div.attr("hash");
                try
                {
                  mEvernoteSession
                      .getClientFactory()
                      .createNoteStoreClient()
                      .getResourceByHash(guid,
                          EvernoteUtil.hexToBytes(div.attr("hash")), false,
                          true, false, new OnClientCallback<Resource>()
                          {
                            @Override
                            public void onSuccess(Resource data)
                            {
                              resource = data;
                              System.out.println(data.toString());
                              imageLoader.displayImage(
                                  ""
                                      + mEvernoteSession
                                          .getAuthenticationResult()
                                          .getWebApiUrlPrefix() + "res/"
                                      + data.getGuid() + "?auth="
                                      + mEvernoteSession.getAuthToken(),
                                  mImageView, options);
                              // TODO: may be wrong...
                              // mImageData.imageBitmap =
                              // mImageView.getDrawingCache(false);

                            }

                            @Override
                            public void onException(Exception exception)
                            {
                              exception.printStackTrace();

                            }

                          });
                }
                catch (TTransportException e)
                {
                  e.printStackTrace();
                }
              }

              // removeDialog(DIALOG_PROGRESS);
              // Toast.makeText(getApplicationContext(),
              // R.string.msg_image_saved,
              // Toast.LENGTH_LONG).show();
              // notes = data;
            }

            @Override
            public void onException(Exception exception)
            {

            }
          });
    }
    catch (TTransportException e)
    {
      e.printStackTrace();
    }
  }

  
  public void updateNote(View view)
  {
    try
    {
      note.setTitle(mTitle.getText().toString());
      System.out.println("Note Title: " + note.getTitle());

      // Trying to add locations to data resources
      NoteAttributes attr = new NoteAttributes();
      LazyMap map = new LazyMap();

      map.putToFullMap("LOCATION", mLocation.getText().toString());
      attr.setApplicationData(map);
      note.setAttributes(attr);

      System.out.println(note.getAttributes().getApplicationData().toString());

      note.addToResources(resource);

      note.setTitle(mTitle.getText().toString());
      note.setContent(EvernoteUtil.NOTE_PREFIX + "<p>"
          + mEntry.getText().toString() + "</p>"
          + EvernoteUtil.createEnMediaTag(resource) + EvernoteUtil.NOTE_SUFFIX);

      mEvernoteSession.getClientFactory().createNoteStoreClient()
          .updateNote(note, new OnClientCallback<Note>()
          {

            @Override
            public void onSuccess(Note data)
            {
              Toast.makeText(getActivity(), R.string.success_creating_note,
                  Toast.LENGTH_LONG).show();
              ((EntryActivity) getActivity()).finish();
              getFragmentManager().popBackStack();
            }

            @Override
            public void onException(Exception exception)
            {
              exception.printStackTrace();
              Toast.makeText(getActivity(), R.string.err_update_note,
                  Toast.LENGTH_LONG).show();
              ((EntryActivity) getActivity()).finish();
              getFragmentManager().popBackStack();
            }

          });
    }
    catch (TTransportException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    }
  }
}
