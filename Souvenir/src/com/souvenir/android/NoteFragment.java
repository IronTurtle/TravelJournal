package com.souvenir.android;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.transport.TTransportException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.souvenir.android.database.SouvenirContentProvider;
import com.souvenir.android.database.SouvenirContract;

@SuppressWarnings("serial")
public class NoteFragment extends ParentFragment implements OnClickListener,
    Serializable
{
  private static String TRAVEL_NOTEBOOK_NAME = "Travel Notebook";
  private static final String LOCATION_NOT_SPECIFIED = "<Location not specified>";
  private static String NOTEBOOK_GUID;
  Boolean oldNote = false;
  ViewPager pager;
  // ArrayList<String> urls = new ArrayList<String>();
  ArrayList<ImageData> images = new ArrayList<ImageData>();
  ArrayList<String> tripsList;
  HashMap<String, STrip> tripMap;;

  // The path to and MIME type of the currently selected image from the
  // gallery
  public class ImageData implements Parcelable
  {
    // public Bitmap imageBitmap;
    public String filePath;
    public String mimeType;
    public String fileName;
    public String hash = "";
    protected String caption = "";

    public boolean isNew = false;

    public ImageData(Parcel in)
    {
      filePath = in.readString();
      mimeType = in.readString();
      fileName = in.readString();
      hash = in.readString();
      caption = in.readString();
      isNew = in.readInt() == 1;
      // TODO Auto-generated constructor stub
    }

    public ImageData()
    {
      // TODO Auto-generated constructor stub
    }

    @Override
    public int describeContents()
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
      dest.writeString(filePath);
      dest.writeString(mimeType);
      dest.writeString(fileName);
      dest.writeString(hash);
      dest.writeString(caption);
      dest.writeInt(isNew ? 1 : 0);
    }

    public final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>()
    {
      public ImageData createFromParcel(Parcel in)
      {
        return new ImageData(in);
      }

      public ImageData[] newArray(int size)
      {
        return new ImageData[size];
      }
    };
  }

  Uri mImageUri;

  // location var
  int radiusRanges[] = { 50, 100, 150, 200 };
  private double longitude;
  private double latitude;
  private LocationManager mlocManager;
  private LocationListener mlocListener;
  private boolean selectedPlace = false;
  private boolean contentChanged = false;

  protected ImageLoader imageLoader = ImageLoader.getInstance();
  private final int FACEBOOK_SHARE = 6135;
  DisplayImageOptions options;

  boolean isEditMode = true;
  boolean fromAnotherFragment = false;
  // Note fields
  ImageView mImageView;
  ImageData mImageData = new ImageData();
  TextView mTitle;
  TextView mLocation;
  TextView mEntry;
  TextView mCaption;

  EditText mTitleEdit;
  EditText mLocationEdit;
  EditText mEntryEdit;
  EditText mCaptionEdit;

  Button mLocationBtn;
  CheckBox mIsFinished;
  Spinner tripSpinner;

  KeyListener titleKeyListener;

  // Note note = new Note();
  SNote mNote;
  Resource resource = new Resource();

  // Activity result request codes
  private static final int SELECT_IMAGE = 1;

  private static final int CAMERA_PIC_REQUEST = 1313;
  private static final int LOCATION_REQUEST = 1034;
  private static final int GPS_REQUEST = 695;
  // UI elements that we update

  Button btnTakePhoto;
  private static final String CAMERA_TAG = "CAMERA";
  private static final String GPS_TAG = "GPS";
  private static final String TROPHY_TAG = "TROPHY";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_note, container, false);
    super.onCreateView(inflater, container, savedInstanceState);
    setHasOptionsMenu(true);

    // mImageView = (ImageView) view.findViewById(R.id.entry_image);
    mTitle = (TextView) view.findViewById(R.id.note_title);
    mTitleEdit = (EditText) view.findViewById(R.id.note_title_edit);
    mLocation = (TextView) view.findViewById(R.id.note_location);
    mLocationEdit = (EditText) view.findViewById(R.id.note_location_edit);
    mEntry = (TextView) view.findViewById(R.id.note_entry);
    mEntryEdit = (EditText) view.findViewById(R.id.note_entry_edit);
    mCaption = (TextView) view.findViewById(R.id.image_caption);
    mCaptionEdit = (EditText) view.findViewById(R.id.image_caption_edit);
    mLocationBtn = (Button) view.findViewById(R.id.note_location_btn);
    // mIsFinished = (CheckBox) view.findViewById(R.id.note_is_finished);

    tripSpinner = (Spinner) view.findViewById(R.id.note_trips_spinner);
    setupTripSpinner();

    if (savedInstanceState != null
        && savedInstanceState.containsKey("SAVED_STATE_NOTE_VIEW"))
    {
      isEditMode = savedInstanceState.getBoolean("SAVED_STATE_NOTE_VIEW",
          isEditMode);
      // this.setViewEditMode(view);
    }

    mCaption.addTextChangedListener(new TextWatcher()
    {

      @Override
      public void afterTextChanged(Editable s)
      {
        if (!images.isEmpty())
        {
          images.get(pager.getCurrentItem()).caption = s.toString();
        }

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count)
      {
      }

    });

    mCaptionEdit.addTextChangedListener(new TextWatcher()
    {

      @Override
      public void afterTextChanged(Editable s)
      {
        if (!images.isEmpty())
        {
          images.get(pager.getCurrentItem()).caption = s.toString();
        }

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count)
      {
      }

    });
    pager = (ViewPager) view.findViewById(R.id.pager);

    options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
        .showStubImage(android.R.color.white).build();

    pager.setAdapter(new PhotoPagerAdapter(images));
    pager.setOnPageChangeListener(new OnPageChangeListener()
    {

      @Override
      public void onPageSelected(int arg0)
      {
        mCaption.setText(images.get(arg0).caption);
        mCaptionEdit.setText(images.get(arg0).caption);
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2)
      {

      }

      @Override
      public void onPageScrollStateChanged(int arg0)
      {

      }
    });

    System.out.println("savedInstanceState is not null: "
        + (savedInstanceState != null));
    Bundle bundle = this.getActivity().getIntent().getExtras();
    if (bundle != null && bundle.containsKey("note"))
    {
      Log.i("souvenir", "Loading note...");
      // getActivity().getWindow().setSoftInputMode(
      // WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      // getMetadata();
      isEditMode = true;
      setViewEditMode(view, false);
      oldNote = true;
      mNote = (SNote) bundle.get("note");
      // String guid = (String) bundle.get("guid");
      // note.setGuid(guid);
      // String title = (String) bundle.get("title");
      displayNote();

    }
    else if (savedInstanceState != null)
    {

      Log.i("souvenir", "Loading data...");
      System.out
          .println("LOCATION:"
              + savedInstanceState.getCharSequence("SAVED_STATE_NOTE_LOCATION",
                  ""));
      isEditMode = true;
      // setViewEditMode(view);
      // mTitle.setText(savedInstanceState.getCharSequence(
      // "SAVED_STATE_NOTE_TITLE", ""));
      /*
       * System.out.println("General Location:" + ((NoteActivity)
       * getActivity()).generalLocation); if (((NoteActivity)
       * getActivity()).generalLocation != null) {
       * mTitle.setText((mTitle.getText().toString()).split("at")[0] + " at " +
       * ((NoteActivity) getActivity()).generalLocation); }
       */

      if (((NoteActivity) getActivity()).location != null)
      {
        mLocation.setText(((NoteActivity) getActivity()).location);
        mLocationEdit.setText(((NoteActivity) getActivity()).location);
      }
      else
      {
        mLocation.setText(savedInstanceState.getCharSequence(
            "SAVED_STATE_NOTE_LOCATION", ""));
      }
      mEntry.setText(savedInstanceState.getCharSequence(
          "SAVED_STATE_NOTE_ENTRY", ""));
    }
    else if (((NoteActivity) getActivity()).location != null)
    {
      mLocation.setText(((NoteActivity) getActivity()).location);
      System.out.println("General Location:"
          + ((NoteActivity) getActivity()).generalLocation);

      mTitle.setText("Souvenir Note on " + getDateTime() + " at "
          + ((NoteActivity) getActivity()).generalLocation);
      // TODO: something happens to redo the title after the call above

      fromAnotherFragment = true;
      isEditMode = ((NoteActivity) getActivity()).isEditMode;
      // setViewEditMode(view);
      fromAnotherFragment = false;
    }
    else
    {

      Log.i("souvenir", "New note...");
      // isEditMode = true;
      // setViewEditMode(view);
      // // setup locationManager for GPS request
      // mlocManager = (LocationManager) getActivity().getSystemService(
      // Context.LOCATION_SERVICE);
      // mlocListener = new AppLocationListener();
      // mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
      // mlocListener, null);
      // setDefaultTitle();

    }

    // mLocation.setOnClickListener(new btnFindPlace());
    mLocationBtn.setOnClickListener(new btnFindPlace());
    mEntry.setOnKeyListener(new NoteEntryField());

    // getTravelNotebook();
    System.out.println(mTitle.getText());
    getActivity().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    Log.i("souvenir", "Saving state...");
    if (mTitle.getText().toString() != null)
      outState.putString("SAVED_STATE_NOTE_TITLE", mTitle.getText().toString());
    if (mLocation.getText().toString() != null)
      outState.putString("SAVED_STATE_NOTE_LOCATION", mLocation.getText()
          .toString());
    if (mEntry.getText().toString() != null)
      outState.putString("SAVED_STATE_NOTE_ENTRY", mEntry.getText().toString());

    outState.putBoolean("SAVED_STATE_NOTE_VIEW", this.isEditMode);

    // System.out.println("Note trip:" +
    // tripSpinner.getSelectedItem().toString());
    outState.putString("SAVED_STATE_NOTE_TRIP", tripSpinner.getSelectedItem()
        .toString());
  }

  @Override
  public void onStop()
  {
    super.onStop();
    Log.i("souvenir", "Onstopped");
    onSaveInstanceState(new Bundle());
  }

  @Override
  public void onPause()
  {
    super.onPause();
    Log.i("souvenir", "Onpaused");
    onSaveInstanceState(new Bundle());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(null);
    // TODO: add value in fields

    Log.i("souvenir", "onActivityCreated Loading data...");

  }

  @Override
  public void onViewStateRestored(Bundle savedInstanceState)
  {

    super.onViewStateRestored(null);
    Log.i("souvenir", "onActivityRestored Loading data...");
    if (savedInstanceState != null
        && savedInstanceState.containsKey("SAVED_STATE_NOTE_VIEW"))
    {
      isEditMode = savedInstanceState.getBoolean("SAVED_STATE_NOTE_VIEW",
          isEditMode);
      System.out.println("isEditMode: " + isEditMode);
      // this.setViewEditMode(getView());
    }
    if ((((NoteActivity) getActivity()).location != null)
        && (((NoteActivity) getActivity()).generalLocation != null))
    {
      mLocation.setText(((NoteActivity) getActivity()).location);
      mLocationEdit.setText(((NoteActivity) getActivity()).location);
      System.out.println("location set");
      if (mTitleEdit.getText().length() == 0 && mTitle.getText().length() == 0)
      {
        mTitle.setText("Souvenir Note on " + getDateTime() + " at "
            + ((NoteActivity) getActivity()).generalLocation);
        mTitleEdit.setText(mTitleEdit + " at "
            + ((NoteActivity) getActivity()).generalLocation);
      }
      System.out.println("FROM SELECTING LOCATION");
      if (oldNote)
      {
        isEditMode = false;
        setViewEditMode(getView(), false);

      }// else
       // {
       // mTitle.setText(mTitle.getText().toString() + " at "
       // + ((NoteActivity) getActivity()).generalLocation);
       // mTitleEdit.setText(mTitleEdit.getText().toString() + " at "
       // + ((NoteActivity) getActivity()).generalLocation);
       // }
    }

  }

  public void setupTripSpinner()
  {

    // String[] array = { "Uncategorized" };
    tripsList = new ArrayList<String>();
    tripMap = new HashMap<String, STrip>();

    // tripsList.addAll(Arrays.asList(array));
    // tripMap.put(array[0], new STrip(array[0]));

    Cursor resCursor;
    if ((resCursor = getActivity().getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP), null, null,
        null, null)) != null
        && resCursor.getCount() > 0)
    {
      while (resCursor.moveToNext())
      {

        STrip curTrip = new STrip(resCursor);
        tripMap.put(curTrip.tripName, curTrip);
        System.out.println(curTrip.tripName);

        tripsList
            .add(resCursor.getString(resCursor
                .getColumnIndex(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_NAME)));
      }
    }
    resCursor.close();
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
        .getApplicationContext(), android.R.layout.simple_spinner_item,
        tripsList);
    // Specify the layout to use when the list of choices appears
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the tripSpinner
    tripSpinner.setAdapter(adapter);
  }

  private void getTravelNotebook()
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

  // TODO: finish
  public void setViewEditMode(View v, boolean saveNote)
  {
    if (isEditMode)
    {
      // is in edit mode, switch to view mode
      // Toast.makeText(getActivity().getApplicationContext(),
      // "Switching to View Mode", Toast.LENGTH_SHORT).show();
      isEditMode = false;
      if (!fromAnotherFragment)
        this.getSherlockActivity().invalidateOptionsMenu();

      // title viewswitcher
      ViewSwitcher switcher = (ViewSwitcher) v
          .findViewById(R.id.switcher_title);
      switcher.showNext(); // or switcher.showPrevious();
      mTitle.setText(mTitleEdit.getText().toString());

      // location view switcher
      switcher = (ViewSwitcher) v.findViewById(R.id.switcher_location);
      switcher.showNext(); // or switcher.showPrevious();
      mLocation.setText(mLocationEdit.getText().toString());

      // hide places select btn
      mLocationBtn.setVisibility(View.GONE);

      // entry view switcher
      switcher = (ViewSwitcher) v.findViewById(R.id.switcher_entry);
      switcher.showNext(); // or switcher.showPrevious();
      mEntry.setText(mEntryEdit.getText().toString());

      // caption view switcher
      switcher = (ViewSwitcher) v.findViewById(R.id.switcher_caption);
      switcher.showNext(); // or switcher.showPrevious();
      mCaption.setText(mCaptionEdit.getText().toString());

      // trip spinner disabled
      tripSpinner.setClickable(false);

      if (saveNote && mTitleEdit.getText().length() != 0)
      {
        // Save/Update to Evernote
        System.out.println("Save pressed");
        Toast.makeText(getActivity(), "Saving Souvenir", Toast.LENGTH_SHORT)
            .show();

        if (!oldNote)
        {
          saveNote(this.getView());
        }
        else
        {
          updateNote(this.getView());
        }
      }
    }
    else
    {
      // is in view mode, switch to edit mode
      // Toast.makeText(getActivity().getApplicationContext(),
      // "Switching to Edit Mode", Toast.LENGTH_SHORT).show();
      isEditMode = true;
      if (!fromAnotherFragment)
        this.getSherlockActivity().invalidateOptionsMenu();

      // title viewswitcher
      ViewSwitcher switcher = (ViewSwitcher) v
          .findViewById(R.id.switcher_title);
      switcher.showPrevious();
      mTitleEdit.setText(mTitle.getText().toString());

      // location viewswitcher
      switcher = (ViewSwitcher) v.findViewById(R.id.switcher_location);
      switcher.showPrevious();
      mLocationEdit.setText(mLocation.getText().toString());

      // show places select btn
      mLocationBtn.setVisibility(View.VISIBLE);

      // entry viewswitcher
      switcher = (ViewSwitcher) v.findViewById(R.id.switcher_entry);
      switcher.showPrevious();
      mEntryEdit.setText(mEntry.getText().toString());

      // caption viewswitcher
      switcher = (ViewSwitcher) v.findViewById(R.id.switcher_caption);
      switcher.showPrevious();
      mCaptionEdit.setText(mCaption.getText().toString());

      // trip spinner enables
      tripSpinner.setClickable(true);
    }
  }

  @Override
  public void onPrepareOptionsMenu(final Menu menu)
  {
    super.onPrepareOptionsMenu(menu);
    MenuItem viewEdit = menu.findItem(R.id.menu_note_viewedit);

    if (isEditMode)
    {
      viewEdit.setTitle(R.string.note_view_mode_string)
          .setIcon(R.drawable.save);
    }
    else
    {
      viewEdit.setTitle(R.string.note_edit_mode_string);
    }

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // This uses the imported MenuItem from ActionBarSherlock
    // Toast.makeText(this.getActivity(), "Got click: " + item.toString(),
    // Toast.LENGTH_SHORT).show();
    switch (item.getItemId())
    {
    case R.id.menu_note_viewedit:
      setViewEditMode(this.getView(), true);

      break;
    // case R.id.menu_note_save:
    // System.out.println("Save pressed");
    // Toast.makeText(getActivity(), "Saving to Evernote", Toast.LENGTH_SHORT)
    // .show();
    // if (!oldNote)
    // {
    // saveNote(this.getView());
    // }
    // else
    // {
    // updateNote(this.getView());
    // }
    // break;
    case R.id.menu_note_camera:
      // startActivity(new Intent(this, NoteActivity.class));

      Intent cameraIntent = new Intent(
          android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
      ContentValues values = new ContentValues();

      mImageUri = getActivity().getApplicationContext().getContentResolver()
          .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      if (mImageUri == null)
      {
        Log.e("image uri is null", "what?");
      }
      else
      {

        Log.e("oh nevermind", "image uri is NOT null");
      }
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
      startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

      break;

    case R.id.menu_note_select:
      // startActivity(new Intent(this, EntryActivity.class));

      Intent intent = new Intent(Intent.ACTION_PICK,
          MediaStore.Images.Media.INTERNAL_CONTENT_URI);
      startActivityForResult(intent, SELECT_IMAGE);
      break;

    // case R.id.menu_note_trophy:
    // // startActivity(new Intent(this, EntryActivity.class));
    // Toast
    // .makeText(getActivity(), "Trophy Button clicked", Toast.LENGTH_SHORT)
    // .show();
    // TrophyDialogFragment trophy = new TrophyDialogFragment();
    // trophy.show(getFragmentManager(), "TROPHY");
    // break;
    case R.id.viewedit_note_menu_save:
      System.out.println("Save pressed");
      // Toast.makeText(getActivity(), "Save Button clicked",
      // Toast.LENGTH_SHORT).show();
      updateNote(this.getView());
      break;
    case R.id.menu_note_fbshare:
      System.out.println("FACEBOOK SHARING");

      String[] noteContent = { mTitle.getText().toString(),
          mLocation.getText().toString(), mEntry.getText().toString() };
      getActivity().startActivityForResult(
          new Intent(getActivity(), ShareActivity.class).putExtra("NOTE",
              noteContent), FACEBOOK_SHARE);

      break;
    }

    return true;
  }

  class NoteEntryField implements EditText.OnKeyListener
  {

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
      // updateUi();
      return false;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode)
    {
    case FACEBOOK_SHARE:
      if (resultCode == Activity.RESULT_OK)
      {
        getActivity().finish();
        getFragmentManager().popBackStack();

      }
      break;
    // Grab image data when picker returns result
    case SELECT_IMAGE:
      if (resultCode == Activity.RESULT_OK)
      {
        mImageUri = data.getData();

        new ImageSelector().execute(data);
      }
      break;
    case CAMERA_PIC_REQUEST:
      if (resultCode == Activity.RESULT_OK)
      {
        if (data != null)
        {
          Log.e("Intent value:", data.toString());
          mImageUri = data.getData();
          // System.out.println("CAMERA DATA:" + data.getDataString());
        }
        else
        {
          Log.e("Intent is null", "yep it is.");
          if (mImageUri == null)
          {
            Log.e("nullcheck on memberimageuri", "its null");
          }
          else
          {
            Log.e("nullcheckon memberimage", mImageUri.toString());
          }
        }

        new ImageSelector().execute(data);
        // setup locationManager for GPS request
        if (mLocation.getText().length() == 0)
        {
          mlocManager = (LocationManager) getActivity().getSystemService(
              Context.LOCATION_SERVICE);
          if (!mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
              && !mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
          {

            GPSDialogFragment d = new GPSDialogFragment();
            d.show(getFragmentManager(), GPS_TAG);
          }
          else
          {
            mlocListener = new AppNetworkLocationListener();
            try
            {

              mlocManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
                  mlocListener, null);
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
        }
        if (mTitle.getText().length() == 0
            && mTitleEdit.getText().length() == 0)
        {
          setDefaultTitle();
        }
      }

      break;
    case LOCATION_REQUEST:
      if (resultCode == Activity.RESULT_OK)
      {
        if (data != null)
        {
          Log.e("Intent value:", data.toString());
          this.mLocation.setText(data.getStringExtra("LOCATION_DATA"));
          selectedPlace = true;
        }
        else
        {
          Log.e("Intent is null", "yep it is.");
        }
      }
      break;

    case GPS_REQUEST:
      System.out.println("GPS_REQUEST RETURNED");
      mlocListener = new AppNetworkLocationListener();

      mlocManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
          mlocListener, null);
      break;
    }

    // mEntry.requestFocus();
    // getActivity().getWindow().setSoftInputMode(
    // WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
  }

  // public void getMetadata()
  // {
  // int pageSize = 10;
  //
  // NoteFilter filter = new NoteFilter();
  // filter.setOrder(NoteSortOrder.UPDATED.getValue());
  // filter.setWords("-tag:app_itinerary*");
  //
  // NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
  // spec.setIncludeTitle(true);
  //
  // try
  // {
  // if (mEvernoteSession.isLoggedIn())
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
  // displayNote(notes.getNotes().get(0).getGuid(), notes
  // .getNotes().get(0).getTitle());
  // }
  //
  // @Override
  // public void onException(Exception exception)
  // {
  //
  // }
  // });
  // }
  // }
  // catch (TTransportException e)
  // {
  //
  // }
  // }
  public void displayNote()
  {

    // mTitle.setText(mNote.getTitle().toUpperCase(Locale.US));
    mTitle.setText(mNote.getTitle());
    mLocation.setText(mNote.getLocation());
    String contents = mNote.getContent();

    Cursor resCursor;
    if ((resCursor = getActivity().getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP), null, null,
        null, null)) != null
        && resCursor.getCount() > 0)
    {
      while (resCursor.moveToNext())
      {

        STrip curTrip = new STrip(resCursor);
        // if (curTrip..equals(mNote.getTripID()))
        // {
        tripSpinner.setSelection(tripsList.indexOf(mNote.getTripID()));
        // }
      }
    }
    resCursor.close();
    // mEntry.setText(android.text.Html.fromHtml(contents).toString().trim());
    mEntry.setText(mNote.getContent().substring(
        mNote.getContent().indexOf("<p>") + "<p>".length(),
        mNote.getContent().indexOf("</p>")));
    Document doc = Jsoup.parse(contents);
    System.out.println("contents");
    System.out.println(contents);

    Elements divs = doc.getElementsByAttribute("hash");
    System.out.println("Images");
    for (final Element div : divs)
    {
      System.out.println(div.attr("hash"));
      System.out.println(div.attr("title"));

      for (SResource resource : mNote.resources)
      {
        System.out.println("hash " + div.attr("hash") + " "
            + resource.getHash() + " " + resource.getNoteId());
        if (div.attr("hash").equals(resource.getHash()))
        {
          ImageData imagedata = new ImageData();
          imagedata.filePath = resource.getPath();
          imagedata.caption = resource.getCaption();
          imagedata.mimeType = resource.getMime();
          imagedata.hash = div.attr("hash");
          images.add(imagedata);
          pager.getAdapter().notifyDataSetChanged();
          mCaption.setText(images.get(pager.getCurrentItem()).caption);
        }
      }

      mImageData.fileName = div.attr("hash");

      // urls.add("file://" + mNote);

      // try
      // {
      // mEvernoteSession
      // .getClientFactory()
      // .createNoteStoreClient()
      // .getResourceByHash(mNote.getEvernoteGUID(),
      // EvernoteUtil.hexToBytes(div.attr("hash")), false, true, false,
      // new OnClientCallback<Resource>()
      // {
      // @Override
      // public void onSuccess(Resource data)
      // {
      // resource = data;
      // System.out.println(data.toString());
      // urls.add(""
      // + mEvernoteSession.getAuthenticationResult()
      // .getWebApiUrlPrefix() + "res/" + data.getGuid()
      // + "?auth=" + mEvernoteSession.getAuthToken());
      // ImageData mImageData = new ImageData();
      //
      // mImageData.caption = div.attr("title");
      // images.add(mImageData);
      // pager.getAdapter().notifyDataSetChanged();
      // mCaption.setText(images.get(pager.getCurrentItem()).caption);
      // }
      //
      // @Override
      // public void onException(Exception exception)
      // {
      // exception.printStackTrace();
      //
      // }
      //
      // });
      // }
      // catch (TTransportException e)
      // {
      // e.printStackTrace();
      // }
    }

  }

  private void setDefaultTitle()
  {
    Time now = new Time();
    now.setToNow();
    String title = "Souvenir Note on " + getDateTime();
    if (getActivity().getIntent().hasExtra("ITINERARY_SELECT"))
    {
      mTitle.setText(title + ", at "
          + getActivity().getIntent().getStringExtra("ITINERARY_SELECT"));
      mTitleEdit.setText(title + ", at "
          + getActivity().getIntent().getStringExtra("ITINERARY_SELECT"));
    }
    else
    {
      mTitle.setText(title);
      mTitleEdit.setText(title);
    }
  }

  private String getDateTime()
  {
    Calendar cal = Calendar.getInstance();
    return String.valueOf(cal.get(Calendar.MONTH) + 1) + "/"
        + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + "/"
        + String.valueOf(cal.get(Calendar.YEAR)) /*
                                                  * + ", " +
                                                  * String.valueOf(cal.get
                                                  * (Calendar.HOUR_OF_DAY)) +
                                                  * ":" +
                                                  * String.valueOf(cal.get(
                                                  * Calendar.MINUTE))
                                                  */;
  }

  private void openCamera()
  {
    // Using MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA allows you
    // to take multiple pics b4 exiting the camera intent
    Intent cameraIntent = new Intent(
        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    ContentValues values = new ContentValues();

    mImageUri = NoteFragment.this.getActivity().getApplicationContext()
        .getContentResolver()
        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    if (mImageUri == null)
    {
      Log.e("image uri is null", "what?");
    }
    else
    {

      Log.e("oh nevermind", "image uri is NOT null");
    }
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
  }

  /*
   * Saves text field content as note to selected notebook, or default notebook
   * if no notebook select
   */
  public void saveNote(View view)
  {
    String content = mEntry.getText().toString();
    String location = mLocation.getText().toString();
    String title = mTitle.getText().toString().trim();
    SNote snote = new SNote(title, content, location);
    snote.setDirty(true);
    // snote.finished = mIsFinished.isChecked();

    snote.tripID = ((STrip) tripMap.get(tripSpinner.getSelectedItem()
        .toString())).evernoteGUID;
    Uri uri = getActivity().getContentResolver().insert(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.NOTE),
        snote.toContentValues());
    int id = Integer.valueOf(uri.getLastPathSegment());
    snote.setId(id);
    content = EvernoteUtil.NOTE_PREFIX + "<p>" + content + "</p>";
    for (ImageData imageData : images)
    {
      String f = imageData.filePath;
      System.out.println(f);
      InputStream in;
      try
      {
        in = new BufferedInputStream(new FileInputStream(f));
        byte[] hash = EvernoteUtil.hash(in);
        String enmedia = "<en-media hash=\"" + EvernoteUtil.bytesToHex(hash)
            + "\" type=\"" + imageData.mimeType + "\" title=\""
            + imageData.caption + "\"/>";
        FileData data = new FileData(EvernoteUtil.hash(in), new File(f));
        // System.out.println(EvernoteUtil.bytesToHex(hash));
        content += enmedia;
        snote.addResource(new SResource(imageData.caption, EvernoteUtil
            .bytesToHex(hash), imageData.mimeType, imageData.filePath));
        // System.out.println(content);
        in.close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    content += EvernoteUtil.NOTE_SUFFIX;
    snote.setContent(content);
    getActivity().getContentResolver().update(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.GET_NOTE.replace("#",
                "" + snote.getId())), snote.toContentValues(), null, null);
    for (ContentValues cv : snote.getResourcesContentValues())
    {
      getActivity().getContentResolver().insert(
          Uri.parse(SouvenirContentProvider.CONTENT_URI
              + SouvenirContentProvider.DatabaseConstants.NOTE_RES), cv);
      System.out.println(cv
          .get(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_HASH));
    }
    getActivity().finish();
  }

  public void updateNote(View view)
  {
    String content = mEntry.getText().toString();
    String location = mLocation.getText().toString();
    String title = mTitle.getText().toString().trim();
    // TODO
    // Ugly clone
    SNote snote = new SNote(title, content, location);
    snote.setCreateDate(mNote.createDate);
    snote.setModifyDate(System.currentTimeMillis());
    snote.setEvernoteGUID(mNote.getEvernoteGUID());
    snote.setSyncNum(mNote.syncNum);
    snote.setId(mNote.getId());
    // snote.finished = mIsFinished.isChecked();
    snote.setTripID(tripSpinner.getSelectedItem().toString());
//    snote.setTripName(tripSpinner.getSelectedItem().toString().trim());

    if (!title.equals(mNote.getTitle()))
    {
      snote.issetV.add(SNote.isset.title);
    }
    System.out.println("|"
        + mNote.getContent().substring(
            mNote.getContent().indexOf("<p>") + "<p>".length(),
            mNote.getContent().indexOf("</p>")) + "|");
    if (!content.equals(mNote.getContent().substring(
        mNote.getContent().indexOf("<p>") + "<p>".length(),
        mNote.getContent().indexOf("</p>"))))
    {
      // content = mNote.getContent().replace(
      // mNote.getContent().substring(
      // mNote.getContent().indexOf("<p>") + "<p>".length(),
      // mNote.getContent().indexOf("</p>")), content);
      snote.issetV.add(SNote.isset.content);
    }
    if (!location.equals(mNote.getLocation()))
    {
      snote.issetV.add(SNote.isset.location);
    }
    if (!(tripSpinner.getSelectedItem().toString()).equals(mNote.getTripID()))
    {
      snote.issetV.add(SNote.isset.tripid);
      snote.issetV.add(SNote.isset.tripname);
    }
    // System.out.println(SNote.encode((EnumSet<SNote.isset>) snote.issetV));
    snote.setDirty(true);
    // Uri uri = getActivity().getContentResolver().insert(
    // Uri.parse(SouvenirContentProvider.CONTENT_URI
    // + SouvenirContentProvider.DatabaseConstants.NOTE),
    // snote.toContentValues());
    // int id = Integer.valueOf(uri.getLastPathSegment());
    // snote.setId(id);
    content = EvernoteUtil.NOTE_PREFIX + "<p>" + content + "</p>";
    // content.replace(EvernoteUtil.NOTE_SUFFIX, "");
    for (ImageData imageData : images)
    {
      if (!imageData.hash.isEmpty())
      {
        String enmedia = "<en-media hash=\"" + imageData.hash + "\" type=\""
            + imageData.mimeType + "\" title=\"" + imageData.caption + "\"/>";
        content += enmedia;
        SResource sr = mNote.r.get(imageData.hash);
        if (!sr.getCaption().equals(imageData.caption))
        {
          sr.caption = imageData.caption;
          snote.issetV.add(SNote.isset.content);
          getActivity().getContentResolver().update(
              Uri.parse(SouvenirContentProvider.CONTENT_URI
                  + SouvenirContentProvider.DatabaseConstants.GET_RES.replace(
                      "#", "" + sr.id)), sr.toContentValues(), null, null);
        }
      }
      else
      {
        String f = imageData.filePath;
        System.out.println(f);
        InputStream in;
        try
        {
          in = new BufferedInputStream(new FileInputStream(f));
          byte[] hash = EvernoteUtil.hash(in);
          String enmedia = "<en-media hash=\"" + EvernoteUtil.bytesToHex(hash)
              + "\" type=\"" + imageData.mimeType + "\" title=\""
              + imageData.caption + "\"/>";
          content += enmedia;
          SResource sr = new SResource(imageData.caption,
              EvernoteUtil.bytesToHex(hash), imageData.mimeType,
              imageData.filePath);
          snote.addResource(sr);
          getActivity().getContentResolver().insert(
              Uri.parse(SouvenirContentProvider.CONTENT_URI
                  + SouvenirContentProvider.DatabaseConstants.NOTE_RES),
              sr.toContentValues());

          // System.out.println(content);
          in.close();
          snote.issetV.add(SNote.isset.content);
          snote.issetV.add(SNote.isset.resources);

        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    content += EvernoteUtil.NOTE_SUFFIX;
    snote.setContent(content);
    if (!snote.issetV.isEmpty())
    {
      snote.setDirty(true);
      System.out.println(SNote.decode(SNote.encode(snote.issetV),
          SNote.isset.class));
      Uri uri2 = Uri.parse(SouvenirContentProvider.CONTENT_URI
          + SouvenirContentProvider.DatabaseConstants.GET_NOTE.replace("#", ""
              + mNote.getId()));

      getActivity().getContentResolver().update(uri2, snote.toContentValues(),
          null, null);
    }
    // for (ContentValues cv : snote.getResourcesContentValues())
    // {
    // getActivity().getContentResolver().insert(
    // Uri.parse(SouvenirContentProvider.CONTENT_URI
    // + SouvenirContentProvider.DatabaseConstants.NOTE_RESOURCES), cv);
    // System.out.println(cv
    // .get(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_HASH));
    // }
    getActivity().finish();
  }

  protected void update(SNote mNote2)
  {
    Uri uri = Uri.parse(SouvenirContentProvider.CONTENT_URI
        + SouvenirContentProvider.DatabaseConstants.GET_NOTE + mNote2.getId());

    ContentValues values = new ContentValues();
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        mNote2.getEvernoteGUID());
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_SYNC_NUM,
        mNote2.getSyncNum());

    getActivity().getContentResolver().update(uri, values, null, null);
  }

  /**
   * Button to capture image for note
   * 
   * @author ironsuturtle Sends the user to the camera application to take a
   *         photo and save
   */
  class btnTakePhotoClicker implements Button.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      Intent cameraIntent = new Intent(
          android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
      ContentValues values = new ContentValues();

      mImageUri = NoteFragment.this.getActivity().getApplicationContext()
          .getContentResolver()
          .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      if (mImageUri == null)
      {
        Log.e("image uri is null", "what?");
      }
      else
      {

        Log.e("oh nevermind", "image uri is NOT null");
      }
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
      startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }
  }

  class btnFindPlace implements Button.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      //
      // // Show Place Finder Fragment
      // // Toast.makeText(getActivity(),
      // // "Location Field clicked",Toast.LENGTH_SHORT).show();
      // startActivityForResult(
      // new Intent(getActivity(), PlacesActivity.class).putExtra(
      // "PREV_LOC_DATA", mLocation.getText().toString()),
      // LOCATION_REQUEST);
      getActivity().getWindow().setSoftInputMode(
          WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      mCallback.onArticleSelected(mLocation.getText().toString(), isEditMode);
    }
  }

  OnHeadlineSelectedListener mCallback;

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);

    // This makes sure that the container activity has implemented
    // the callback interface. If not, it throws an exception.
    try
    {
      mCallback = (OnHeadlineSelectedListener) activity;
    }
    catch (ClassCastException e)
    {
      throw new ClassCastException(activity.toString()
          + " must implement OnHeadlineSelectedListener");
    }
  }

  public interface OnHeadlineSelectedListener
  {
    /** Called by HeadlinesFragment when a list item is selected */
    public void onArticleSelected(String location, boolean isViewMode);
  }

  public void clearForm(View view)
  {
    // mImageView.setImageResource(android.R.color.transparent);
    mTitle.setText("");
    mLocation.setText("");
    mEntry.setText("");
  }

  /**
   * Called when the user taps the "Select Image" button.
   * <p/>
   * Sends the user to the image gallery to choose an image to share.
   */
  public void startSelectImage(View view)
  {
    Intent intent = new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(intent, SELECT_IMAGE);
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    }
  }

  public static class GPSDialogFragment extends DialogFragment
  {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      // Use the Builder class for convenient dialog construction
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder
          .setMessage(R.string.gps_dialog)
          .setPositiveButton(R.string.gps_yes,
              new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface dialog, int id)
                {
                  startActivityForResult(
                      new Intent(
                          android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                      GPS_REQUEST);
                }
              })
          .setNegativeButton(R.string.gps_no,
              new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface dialog, int id)
                {
                  // User cancelled the dialog
                }
              });
      // Create the AlertDialog object and return it
      return builder.create();
    }

  }

  public static class TrophyDialogFragment extends DialogFragment
  {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      // Get the layout inflater
      LayoutInflater inflater = getActivity().getLayoutInflater();
      final View view = inflater.inflate(R.layout.dialog_trophy, null);

      // Inflate and set the layout for the dialog
      // Pass null as the parent view because its going in the dialog layout
      builder
          .setView(view)
          // Add action buttons
          .setPositiveButton("Give Trophy",
              new DialogInterface.OnClickListener()
              {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                  final TextView trophyDialog = (TextView) view
                      .findViewById(R.id.trophyText);
                  Toast.makeText(getActivity().getApplicationContext(),
                      "Trophy created: " + trophyDialog.getText().toString(),
                      Toast.LENGTH_SHORT).show();

                }
              })
          .setNegativeButton(R.string.cancel,
              new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface dialog, int id)
                {
                  Toast.makeText(getActivity().getApplicationContext(),
                      "Trophy cancelled", Toast.LENGTH_SHORT).show();
                }
              });
      return builder.create();

    }
  }

  /**
   * Called when control returns from the image gallery picker. Loads the image
   * that the user selected.
   */
  private class ImageSelector extends AsyncTask<Intent, Void, ImageData>
  {

    // using showDialog, could use Fragments instead
    @Override
    protected void onPreExecute()
    {
      // showDialog(DIALOG_PROGRESS);
    }

    /**
     * The callback from the gallery contains a pointer into a table. Look up
     * the appropriate record and pull out the information that we need, in this
     * case, the path to the file on disk, the file name and the MIME type.
     * 
     * @param intents
     * @return
     */
    // using Display.getWidth and getHeight on older SDKs
    @SuppressWarnings("deprecation")
    @Override
    // suppress lint check on Display.getSize(Point)
    @TargetApi(16)
    protected ImageData doInBackground(Intent... intents)
    {
      /*
       * if (intents == null || intents.length == 0) { return null; }
       */

      Uri selectedImage = mImageUri;
      String[] queryColumns = { MediaStore.Images.Media._ID,
          MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE,
          MediaStore.Images.Media.DISPLAY_NAME };

      Cursor cursor = null;
      ImageData image = null;
      try
      {
        cursor = NoteFragment.this.getActivity().getApplicationContext()
            .getContentResolver()
            .query(selectedImage, queryColumns, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
          image = new ImageData();

          image.filePath = cursor.getString(cursor
              .getColumnIndex(queryColumns[1]));
          image.mimeType = cursor.getString(cursor
              .getColumnIndex(queryColumns[2]));
          image.fileName = cursor.getString(cursor
              .getColumnIndex(queryColumns[3]));
          image.isNew = true;

          // First decode with inJustDecodeBounds=true to check
          // dimensions
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;

          // Bitmap tempBitmap = BitmapFactory.decodeFile(image.filePath,
          // options);

          int dimen = 0;
          int x = 0;
          int y = 0;

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
          {
            Point size = new Point();
            NoteFragment.this.getActivity().getWindowManager()
                .getDefaultDisplay().getSize(size);

            x = size.x;
            y = size.y;
          }
          else
          {
            x = NoteFragment.this.getActivity().getWindowManager()
                .getDefaultDisplay().getWidth();
            y = NoteFragment.this.getActivity().getWindowManager()
                .getDefaultDisplay().getHeight();
          }

          dimen = x < y ? x : y;

          // Calculate inSampleSize
          options.inSampleSize = calculateInSampleSize(options, dimen, dimen);

          // Decode bitmap with inSampleSize set
          options.inJustDecodeBounds = false;

          // tempBitmap = BitmapFactory.decodeFile(image.filePath,options);

          // Scaling isn't changed...
          // tempBitmap = BitmapFactory.decodeFile(image.filePath);

          // image.imageBitmap = Bitmap.createScaledBitmap(tempBitmap, y, x,
          // true);
          // tempBitmap.recycle();

        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
        Log.e(CAMERA_TAG, "Error retrieving image");
      }
      finally
      {
        if (cursor != null)
        {
          cursor.close();
        }
      }
      return image;
    }

    /**
     * Calculates a sample size to be used when decoding a bitmap if you don't
     * require (or don't have enough memory) to load the full size bitmap.
     * <p/>
     * <p>
     * This function has been taken form Android's training materials,
     * specifically the section about "Loading Large Bitmaps Efficiently".
     * <p>
     * 
     * @param options
     *          a BitmapFactory.Options object, obtained from decoding only the
     *          bitmap's bounds.
     * @param reqWidth
     *          The required minimum width of the decoded bitmap.
     * @param reqHeight
     *          The required minimum height of the decoded bitmap.
     * @return the sample size needed to decode the bitmap to a size that meets
     *         the required width and height.
     * @see <a
     *      href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap">Load
     *      a Scaled Down Version into Memory</a>
     */
    protected int calculateInSampleSize(BitmapFactory.Options options,
        int reqWidth, int reqHeight)
    {
      // Raw height and width of image
      final int height = options.outHeight;
      final int width = options.outWidth;
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth)
      {
        if (width > height)
        {
          inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        else
        {
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
    protected void onPostExecute(ImageData image)
    {
      // removeDialog(DIALOG_PROGRESS);

      if (image == null)
      {
        Toast.makeText(NoteFragment.this.getActivity().getApplicationContext(),
            R.string.err_image_selected, Toast.LENGTH_SHORT).show();
        return;
      }

      // if (image.imageBitmap != null)
      // {
      // mImageView.setImageBitmap(image.imageBitmap);
      // }

      System.out.println("image filepath: " + image.filePath);
      // urls.add("file://" + image.filePath);
      images.add(image);
      pager.getAdapter().notifyDataSetChanged();
      mCaption.setText(images.get(pager.getCurrentItem()).caption);
      if (mEvernoteSession.isLoggedIn())
      {
        // mBtnSave.setEnabled(true);
      }

      mImageData = image;
    }
  }

  /*
   * @Override protected void onSaveInstanceState(Bundle outState) {
   * super.onSaveInstanceState(outState); if (mImageUri != null) {
   * outState.putString("cameraImageUri", mImageUri.toString()); } }
   * 
   * @Override protected void onRestoreInstanceState(Bundle savedInstanceState)
   * { super.onRestoreInstanceState(savedInstanceState); if
   * (savedInstanceState.containsKey("cameraImageUri")) { mImageUri =
   * Uri.parse(savedInstanceState.getString("cameraImageUri")); } }
   */

  /* Class My Location Listener */
  public class AppNetworkLocationListener implements LocationListener
  {

    @Override
    public void onLocationChanged(Location loc)
    {

      latitude = loc.getLatitude();
      longitude = loc.getLongitude();

      String Text = "My current location is: " + "Latitude = "
          + loc.getLatitude() + " Longitude = " + loc.getLongitude();

      // Toast.makeText(getActivity().getApplicationContext(), Text,
      // Toast.LENGTH_SHORT).show();

      // setText of coordinates to mLocation field
      mLocation.setText(loc.getLatitude() + ", " + loc.getLongitude());
      if (!getActivity().getIntent().hasExtra("ITINERARY_SELECT"))
      {
        mTitle.setText(mTitle.getText().toString() + ", " + loc.getLatitude()
            + ", " + loc.getLongitude());
      }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
      mlocListener = new AppLocationListener();

      mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
          mlocListener, null);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

  }

  /* Class My Location Listener */
  public class AppLocationListener implements LocationListener
  {

    @Override
    public void onLocationChanged(Location loc)
    {

      latitude = loc.getLatitude();
      longitude = loc.getLongitude();

      // setText of coordinates to mLocation field
      mLocation.setText(loc.getLatitude() + ", " + loc.getLongitude());
      if (getActivity() != null && getActivity().getIntent() != null
          && !getActivity().getIntent().hasExtra("ITINERARY_SELECT"))
      {
        mTitle.setText(mTitle.getText().toString() + ", " + loc.getLatitude()
            + ", " + loc.getLongitude());
      }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
      // Toast.makeText(getActivity().getApplicationContext(), "Gps Disabled",
      // Toast.LENGTH_SHORT).show();
      // create popup to ask if user wants to turn on GPS. If so, remind them to
      // press back to go back to App.
      // GPSDialogFragment d = new GPSDialogFragment();
      // d.show(getFragmentManager(), GPS_TAG);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
      // Toast.makeText(getActivity().getApplicationContext(), "Gps Enabled",
      // Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

  }

  public void setLocationData(String data)
  {
    mLocation.setText(data);
    mLocationEdit.setText(data);
  }

  private class PhotoPagerAdapter extends android.support.v4.view.PagerAdapter
  {

    private ArrayList<ImageData> imageList;
    private LayoutInflater inflater;

    PhotoPagerAdapter(ArrayList<ImageData> imageList)
    {
      this.imageList = imageList;
      inflater = getActivity().getLayoutInflater();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
      ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container)
    {
    }

    @Override
    public int getCount()
    {
      return imageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position)
    {
      View imageLayout = inflater.inflate(R.layout.item_pager_image, view,
          false);
      ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
      // mCaption.setText(imageList.get(position).caption);

      imageView.setOnClickListener(new OnClickListener()
      {

        @Override
        public void onClick(View v)
        {
          // TODO Auto-generated method stub
          ImagePagerFragment frag = new ImagePagerFragment();
          Bundle args1 = new Bundle();
          args1.putParcelableArrayList("images", imageList);
          args1.putInt("position", position);
          // Put any other arguments
          frag.setArguments(args1);

          FragmentTransaction transaction = getFragmentManager()
              .beginTransaction();
          transaction.replace(android.R.id.content, frag);
          // transaction.addToBackStack(null);
          transaction.commit();
        }
      });

      final ProgressBar spinner = (ProgressBar) imageLayout
          .findViewById(R.id.loading);

      imageLoader.displayImage("file://" + imageList.get(position).filePath,
          imageView, options, new SimpleImageLoadingListener()
          {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
              spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                FailReason failReason)
            {
              String message = null;
              switch (failReason.getType())
              {
              case IO_ERROR:
                message = "Input/Output error";
                break;
              case DECODING_ERROR:
                message = "Image can't be decoded";
                break;
              case NETWORK_DENIED:
                message = "Downloads are denied";
                break;
              case OUT_OF_MEMORY:
                message = "Out Of Memory error";
                break;
              case UNKNOWN:
                message = "Unknown error";
                break;
              }
              Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

              spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage)
            {
              spinner.setVisibility(View.GONE);
            }
          });

      ((ViewPager) view).addView(imageLayout, 0);
      return imageLayout;
    }

    public boolean isViewFromObject(View view, Object object)
    {
      return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {
    }

    @Override
    public Parcelable saveState()
    {
      return null;
    }

    @Override
    public void startUpdate(View container)
    {
    }
  }

}
