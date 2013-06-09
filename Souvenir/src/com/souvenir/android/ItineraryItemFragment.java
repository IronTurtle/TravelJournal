package com.souvenir.android;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.view.MenuItem;

public class ItineraryItemFragment extends ParentFragment
{

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    View v = inflater.inflate(R.layout.fragment_itinerary_item, null);

    this.getSherlockActivity().getSupportActionBar()
        .setDisplayUseLogoEnabled(false);
    this.getSherlockActivity().getSupportActionBar()
        .setDisplayShowTitleEnabled(false);

    setHasOptionsMenu(true);

    initFields(v);

    return v;
  }

  private void initFields(View v)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, LLL dd, yyyy;hh:mm a");
    Date d = new Date();
    String curDate = sdf.format(d).split(";")[0];
    String curTime = sdf.format(d).split(";")[1];

    ((Button) v.findViewById(R.id.itinerary_item_startdate)).setText(curDate);
    ((Button) v.findViewById(R.id.itinerary_item_enddate)).setText(curDate);

    ((Button) v.findViewById(R.id.itinerary_item_starttime)).setText(curTime);
    ((Button) v.findViewById(R.id.itinerary_item_endtime)).setText(curTime);

  }

  public boolean onOptionsItemSelected(MenuItem item)
  {
    // This uses the imported MenuItem from ActionBarSherlock
    // Toast.makeText(this.getActivity(), "Got click: " + item.toString(),
    // Toast.LENGTH_SHORT).show();
    switch (item.getItemId())
    {

    }
    return true;
  }
}
