package com.evernote.android.sample;

import java.util.ArrayList;
import java.util.List;

import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.PlacesResult;
import org.gmarz.googleplaces.models.Place;
import org.gmarz.googleplaces.query.NearbySearchQuery;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceFragment extends Activity {

	private double longitude;
	private double latitude;
	private LocationManager mlocManager;
	private LocationListener mlocListener;
	
	private ArrayList<Place> placeList = new ArrayList<Place>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		/* Use the LocationManager class to obtain GPS locations */
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mlocListener = new AppLocationListener();
		/*mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);*/
		mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mlocListener, null);
		
	}

	public void getNearbyPlaces() {
		LongOperation l = new LongOperation();
		l.execute("");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.activity_places, menu);
		return true;
	}

	private class PlaceAdapter extends ArrayAdapter<Place> {
		int resource;
		String response;
		Context context;
		TextView snippetName;
		TextView snippetAddress;

		public PlaceAdapter(Context context, int resource, List<Place> items) {
			super(context, resource, items);
			this.resource = resource;
			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout snippetView = (LinearLayout) convertView;
			// Get the current alert object
			final Place curPlace = getItem(position);

			// Inflate the view
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				snippetView = (LinearLayout) inflater.inflate(
						R.layout.place_snippet, null);
			} else {
				snippetView = (LinearLayout) convertView;
			}

			try {
				// Get the text boxes from the listitem.xml file
				snippetName = (TextView) snippetView
						.findViewById(R.id.placeName);
				snippetAddress = (TextView) snippetView
						.findViewById(R.id.placeAddress);
				snippetName.setText(curPlace.getName());
				snippetAddress.setText(curPlace.getAddress());
				// ImageView snippetPic =
				// (ImageView)snippetView.findViewById(R.id.snippetPic);

				/*
				 * try { String path = jp.getJSONObject(0).getString("path");
				 * String imageUrl = "http://kevinsutardji.com:8080/images/" +
				 * path; //String imageUrl="http://lorempixel.com/100/80/";
				 * Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new
				 * URL(imageUrl).getContent()); Bitmap resized =
				 * Bitmap.createScaledBitmap(bitmap, 100, 80, false);
				 * snippetPic.setImageBitmap(bitmap); } catch
				 * (MalformedURLException e) { e.printStackTrace(); } catch
				 * (IOException e) { e.printStackTrace(); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}

			snippetView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					TextView selected = (TextView) PlaceFragment.this
							.findViewById(R.id.selectedPlace);
					System.out.println(curPlace.getName());
					selected.setText(curPlace.getName() + ", "
							+ curPlace.getAddress());
				}
			});
			return snippetView;
		}

	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				GooglePlaces googlePlaces = new GooglePlaces(
						"AIzaSyDR0BCaO8el9549_l6QuAHOpp6BBxwRbE8");

				NearbySearchQuery query = new NearbySearchQuery(latitude, longitude);

				query.setRadius(50);

				System.out.println("Query: " + query.toString());
				PlacesResult result = googlePlaces.getPlaces(query);

				placeList = (ArrayList<Place>) result.getPlaces();

				for (int i = 0; i < placeList.size(); i++) {
					System.out.println(placeList.get(i).getName());
					System.out.println("\t" + placeList.get(i).getAddress());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			ListView listView = (ListView) findViewById(R.id.placeListView);
			PlaceAdapter adapter = new PlaceAdapter(PlaceFragment.this,
					R.layout.place_snippet, placeList);
			listView.setAdapter(adapter);
			listView.setScrollingCacheEnabled(false);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/* Class My Location Listener */
	public class AppLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {

			latitude = loc.getLatitude();
			longitude = loc.getLongitude();

			String Text = "My current location is: " + "Latitude = "
					+ loc.getLatitude() + " Longitude = " + loc.getLongitude();

			Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
					.show();
			LongOperation l = new LongOperation();
			
			l.execute("");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}
}