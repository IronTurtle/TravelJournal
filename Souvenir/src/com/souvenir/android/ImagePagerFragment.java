package com.souvenir.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.edam.type.Resource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.souvenir.android.NoteFragment.ImageData;

@SuppressWarnings("serial")
public class ImagePagerFragment extends ParentFragment
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
  // private class ImageData
  // {
  // public Bitmap imageBitmap;
  // public String filePath;
  // public String mimeType;
  // public String fileName;
  // public String hash = "";
  // protected String caption = "";
  // public boolean isNew = false;
  // }

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
  // ImageData mImageData = new ImageData();
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
    View view = inflater
        .inflate(R.layout.fragment_imagepager, container, false);
    super.onCreateView(inflater, container, savedInstanceState);
    setHasOptionsMenu(true);
    getSherlockActivity().getSupportActionBar().hide();
    getActivity().invalidateOptionsMenu();
    pager = (ViewPager) view.findViewById(R.id.bigpager);

    options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
        .showStubImage(android.R.color.white).build();

    // mTitle.setText("LOREM IPSUM LROSDFSADFSDAFSAFDSDFSDFASFSAFSADF");
    // urls.add("http://www.joshuakennon.com/wp-content/uploads/2010/01/earnings-yield-stock-valuation.jpg");
    // urls.add("http://us.123rf.com/400wm/400/400/forwardcom/forwardcom0710/forwardcom071000126/1877196-parthenon-erechthion-herodion-and-lycabetus-the-main-landmarks-of-athens-greece.jpg");
    // pager.setOnPageChangeListener(new OnPageChangeListener()
    // {
    //
    // @Override
    // public void onPageSelected(int arg0)
    // {
    // // mCaption.setText(images.get(arg0).caption);
    // // mCaptionEdit.setText(images.get(arg0).caption);
    // }
    //
    // @Override
    // public void onPageScrolled(int arg0, float arg1, int arg2)
    // {
    //
    // }
    //
    // @Override
    // public void onPageScrollStateChanged(int arg0)
    // {
    //
    // }
    // });

    Bundle bundle = this.getArguments();
    if (bundle != null)
    {
      Log.i("souvenir", "Loading note...");
      // getActivity().getWindow().setSoftInputMode(
      // WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      // getMetadata();
      isEditMode = true;
      // setViewEditMode(view, false);
      oldNote = true;
      mNote = (SNote) bundle.get("note");
      images = bundle.getParcelableArrayList("images");
      pager.setAdapter(new ClothingPagerAdapter(images));
      pager.getAdapter().notifyDataSetChanged();
      pager.setCurrentItem(bundle.getInt("position"));
      // String guid = (String) bundle.get("guid");
      // note.setGuid(guid);
      // String title = (String) bundle.get("title");
      // displayNote();
    }
    // }
    // else if (savedInstanceState != null)
    // {
    //
    // Log.i("souvenir", "Loading data...");
    // System.out
    // .println("LOCATION:"
    // + savedInstanceState.getCharSequence("SAVED_STATE_NOTE_LOCATION",
    // ""));
    // isEditMode = true;
    // // setViewEditMode(view);
    // // mTitle.setText(savedInstanceState.getCharSequence(
    // // "SAVED_STATE_NOTE_TITLE", ""));
    // /*
    // * System.out.println("General Location:" + ((NoteActivity)
    // * getActivity()).generalLocation); if (((NoteActivity)
    // * getActivity()).generalLocation != null) {
    // * mTitle.setText((mTitle.getText().toString()).split("at")[0] + " at " +
    // * ((NoteActivity) getActivity()).generalLocation); }
    // */
    //
    // if (((NoteActivity) getActivity()).location != null)
    // {
    // mLocation.setText(((NoteActivity) getActivity()).location);
    // mLocationEdit.setText(((NoteActivity) getActivity()).location);
    // }
    // else
    // {
    // mLocation.setText(savedInstanceState.getCharSequence(
    // "SAVED_STATE_NOTE_LOCATION", ""));
    // }
    // mEntry.setText(savedInstanceState.getCharSequence(
    // "SAVED_STATE_NOTE_ENTRY", ""));
    // }
    // else if (((NoteActivity) getActivity()).location != null)
    // {
    // mLocation.setText(((NoteActivity) getActivity()).location);
    // System.out.println("General Location:"
    // + ((NoteActivity) getActivity()).generalLocation);
    //
    // mTitle.setText("Souvenir Note on " + getDateTime() + " at "
    // + ((NoteActivity) getActivity()).generalLocation);
    // // TODO: something happens to redo the title after the call above
    //
    // fromAnotherFragment = true;
    // isEditMode = ((NoteActivity) getActivity()).isEditMode;
    // // setViewEditMode(view);
    // fromAnotherFragment = false;
    // }
    // else
    // {
    //
    // Log.i("souvenir", "New note...");
    // // isEditMode = true;
    // // setViewEditMode(view);
    // // // setup locationManager for GPS request
    // // mlocManager = (LocationManager) getActivity().getSystemService(
    // // Context.LOCATION_SERVICE);
    // // mlocListener = new AppLocationListener();
    // // mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
    // // mlocListener, null);
    // // setDefaultTitle();
    //
    // }

    // mLocation.setOnClickListener(new btnFindPlace());
    // mLocationBtn.setOnClickListener(new btnFindPlace());
    // mEntry.setOnKeyListener(new NoteEntryField());

    // getTravelNotebook();
    // System.out.println(mTitle.getText());
    // getActivity().getWindow().setSoftInputMode(
    // WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    return view;
  }

  // @Override
  // public void onSaveInstanceState(Bundle outState)
  // {
  // super.onSaveInstanceState(outState);
  // Log.i("souvenir", "Saving state...");
  // if (mTitle.getText().toString() != null)
  // outState.putString("SAVED_STATE_NOTE_TITLE", mTitle.getText().toString());
  // if (mLocation.getText().toString() != null)
  // outState.putString("SAVED_STATE_NOTE_LOCATION", mLocation.getText()
  // .toString());
  // if (mEntry.getText().toString() != null)
  // outState.putString("SAVED_STATE_NOTE_ENTRY", mEntry.getText().toString());
  //
  // outState.putBoolean("SAVED_STATE_NOTE_VIEW", this.isEditMode);
  //
  // // System.out.println("Note trip:" +
  // // tripSpinner.getSelectedItem().toString());
  // outState.putString("SAVED_STATE_NOTE_TRIP", tripSpinner.getSelectedItem()
  // .toString());
  // }
  //
  // @Override
  // public void onStop()
  // {
  // super.onStop();
  // Log.i("souvenir", "Onstopped");
  // onSaveInstanceState(new Bundle());
  // }
  //
  // @Override
  // public void onPause()
  // {
  // super.onPause();
  // Log.i("souvenir", "Onpaused");
  // onSaveInstanceState(new Bundle());
  // }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(null);
    // TODO: add value in fields

    Log.i("souvenir", "onActivityCreated Loading data...");

  }

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

  private class ClothingPagerAdapter extends
      android.support.v4.view.PagerAdapter
  {

    private ArrayList<ImageData> clothing;
    private LayoutInflater inflater;

    ClothingPagerAdapter(ArrayList<ImageData> clothing)
    {
      this.clothing = clothing;
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
      return clothing.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position)
    {
      View imageLayout = inflater.inflate(R.layout.item_bigpager_image, view,
          false);
      ImageView imageView = (ImageView) imageLayout.findViewById(R.id.bigimage);
      // mCaption.setText(clothing.get(position).caption);

      final ProgressBar spinner = (ProgressBar) imageLayout
          .findViewById(R.id.bigloading);

      imageLoader.displayImage("file://" + clothing.get(position).filePath,
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
