package com.souvenir.android;

import java.util.ArrayList;
import java.util.List;

import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.PlacesResult;
import org.gmarz.googleplaces.models.Place;
import org.gmarz.googleplaces.query.NearbySearchQuery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlacesFragment extends ParentFragment
{

  int radiusRanges[] = { 50, 100, 150, 200 };
  private double longitude;
  private double latitude;
  private String generalLocation = null;

  private LocationManager mlocManager;
  private LocationListener mlocListener;

  private EditText selected;

  private ArrayList<Place> placeList = new ArrayList<Place>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_places, container, false);
    setHasOptionsMenu(false);
    selected = (EditText) view.findViewById(R.id.selectedPlace);
    String prev_loc = getArguments().getString("PREV_LOC_DATA");
    selected.setText(prev_loc);

    // // TODO: Should not use try-catch, but if hasExtra, if not,
    // getSingleUpdate
    // try
    // {
    // System.out.println(getActivity().getIntent().hasExtra("PREV_LOC_DATA"));
    // // if(getActivity().getIntent().hasExtra("PREV_LOC_DATA")) {
    // String prev_loc = (String) getActivity().getIntent().getExtras()
    // .getString("PREV_LOC_DATA");
    // selected.setText(prev_loc);
    // /*
    // * } else { mlocManager = (LocationManager)
    // * getActivity().getSystemService(Context.LOCATION_SERVICE);
    // *
    // * mlocListener = new AppLocationListener();
    // * mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
    // * mlocListener, null); }
    // */
    // }
    // catch (Exception e)
    // {
    // // no prev location selected, do nothing
    // e.printStackTrace();
    // }

    // set up location service and listener for Google Places API
    mlocManager = (LocationManager) getActivity().getSystemService(
        Context.LOCATION_SERVICE);
    mlocListener = new AppLocationListener();
    getActivity().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    // get nearby places from gps
    getNearbyPlaces();
    return view;
  }

  public void getNearbyPlaces()
  {

    // TODO: don't use try-catch, instead, check if hasExtra
    try
    {
      // use long and lat from coords obtained in create note
      latitude = Double.valueOf(selected.getText().toString().split(",")[0]);
      longitude = Double.valueOf(selected.getText().toString().split(",")[1]);
      // query results from Google Places API
      LongOperation l = new LongOperation();
      l.execute("");
    }
    catch (Exception e)
    {
      // long and lat not obtained earlier, request coords
      mlocManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
          mlocListener, null);
    }

  }

  private class LongOperation extends AsyncTask<String, Void, String>
  {

    @Override
    protected String doInBackground(String... params)
    {
      try
      {
        GooglePlaces googlePlaces = new GooglePlaces(
            "AIzaSyDR0BCaO8el9549_l6QuAHOpp6BBxwRbE8");

        System.out
            .println("(lat,long) = (" + latitude + ", " + longitude + ")");

        if (latitude != 0 && longitude != 0)
        {
          NearbySearchQuery query = new NearbySearchQuery(latitude, longitude);

          query.setRadius(150); // inc rrIndex as long as places results < 20

          System.out.println("Query: " + query.toString());
          PlacesResult result = googlePlaces.getPlaces(query);

          placeList = (ArrayList<Place>) result.getPlaces();

          for (int i = 0; i < placeList.size(); i++)
          {
            System.out.println(placeList.get(i).getName());
            System.out.println("\t" + placeList.get(i).getAddress());
            if (i == 0)
            {
              generalLocation = placeList.get(i).getAddress();
            }
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
      ListView listView = (ListView) getView().findViewById(R.id.placeListView);
      PlaceAdapter adapter = new PlaceAdapter(getActivity(),
          R.layout.place_snippet, placeList);
      listView.setAdapter(adapter);
      listView.setScrollingCacheEnabled(false);

    }

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
    }
  }

  private class PlaceAdapter extends ArrayAdapter<Place>
  {
    int resource;
    String response;
    Context context;
    TextView snippetName;
    TextView snippetAddress;

    public PlaceAdapter(Context context, int resource, List<Place> items)
    {
      super(context, resource, items);
      this.resource = resource;
      this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
      LinearLayout snippetView = (LinearLayout) convertView;
      // Get the current alert object
      final Place curPlace = getItem(position);

      // Inflate the view
      if (convertView == null)
      {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        snippetView = (LinearLayout) inflater.inflate(R.layout.place_snippet,
            null);
      }
      else
      {
        snippetView = (LinearLayout) convertView;
      }

      try
      {

        // Get the text boxes from the listitem.xml file
        snippetName = (TextView) snippetView.findViewById(R.id.placeName);
        snippetAddress = (TextView) snippetView.findViewById(R.id.placeAddress);
        snippetName.setText(curPlace.getName());
        snippetAddress.setText(curPlace.getAddress());
        // ImageView snippetPic =
        // (ImageView)snippetView.findViewById(R.id.snippetPic);

        /*
         * try { String path = jp.getJSONObject(0).getString("path"); String
         * imageUrl = "http://kevinsutardji.com:8080/images/" + path; //String
         * imageUrl="http://lorempixel.com/100/80/"; Bitmap bitmap =
         * BitmapFactory.decodeStream((InputStream)new
         * URL(imageUrl).getContent()); Bitmap resized =
         * Bitmap.createScaledBitmap(bitmap, 100, 80, false);
         * snippetPic.setImageBitmap(bitmap); } catch (MalformedURLException e)
         * { e.printStackTrace(); } catch (IOException e) { e.printStackTrace();
         * }
         */
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

      snippetView.setOnClickListener(new View.OnClickListener()
      {

        @Override
        public void onClick(View v)
        {
          EditText selected = (EditText) getActivity().findViewById(
              R.id.selectedPlace);
          System.out.println(curPlace.getName());
          selected.setText(curPlace.getName() + "\n" + curPlace.getAddress());

          ((NoteActivity) getActivity()).sendLocationData(curPlace.getName()
              + "\n" + curPlace.getAddress(), generalLocation);

        }
      });
      return snippetView;
    }

  }

  /* Class My Location Listener */
  public class AppNetworkLocationListener implements LocationListener
  {

    @Override
    public void onLocationChanged(Location loc)
    {

      latitude = loc.getLatitude();
      longitude = loc.getLongitude();

      String text = "Current location is: " + "Latitude = " + loc.getLatitude()
          + " Longitude = " + loc.getLongitude();

      ((EditText) (getView().findViewById(R.id.selectedPlace)))
          .setText(latitude + ", " + longitude);
      // Toast.makeText(getActivity().getApplicationContext(), text,
      // Toast.LENGTH_SHORT).show();
      // query results from Google Places API
      LongOperation l = new LongOperation();
      l.execute("");
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

      String text = "Current location is: " + "Latitude = " + loc.getLatitude()
          + " Longitude = " + loc.getLongitude();

      ((EditText) (getView().findViewById(R.id.selectedPlace)))
          .setText(latitude + ", " + longitude);
      Toast.makeText(getActivity().getApplicationContext(), text,
          Toast.LENGTH_SHORT).show();
      // query results from Google Places API
      LongOperation l = new LongOperation();
      l.execute("");
    }

    @Override
    public void onProviderDisabled(String provider)
    {
      // Toast.makeText(getActivity().getApplicationContext(), "Gps Disabled",
      // Toast.LENGTH_SHORT).show();
      // create popup to ask if user wants to turn on GPS. If so, remind them to
      // press back to go back to App.
      GPSDialogFragment d = new GPSDialogFragment();
      d.show(getFragmentManager(), "GPS");
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
                  startActivity(new Intent(
                      android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
}