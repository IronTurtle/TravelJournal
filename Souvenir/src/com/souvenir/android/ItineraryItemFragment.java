package com.souvenir.android;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.view.MenuItem;

public class ItineraryItemFragment extends ParentFragment
{

  private String itemTitle = "", itemLocation = "", itemType = "",
      itemStartDate = "", itemEndDate = "", itemStartTime = "",
      itemEndTime = "";
  private boolean itemIsTrip = false;
  private double itemDistance = 0.0;

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

    preloadTypesSpinner(v);
    initFields(v);

    return v;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // This uses the imported MenuItem from ActionBarSherlock
    // Toast.makeText(this.getActivity(), "Got click: " + item.toString(),
    // Toast.LENGTH_SHORT).show();
    switch (item.getItemId())
    {
    case R.id.menu_save_itinerary_item:
      View v = this.getView();
      itemTitle = ((EditText) v.findViewById(R.id.itinerary_item_title))
          .getText().toString();
      itemIsTrip = ((CheckBox) v
          .findViewById(R.id.itinerary_item_istrip_checkbox)).isChecked();
      itemType = ((Spinner) v.findViewById(R.id.itinerary_item_type))
          .getSelectedItem().toString();
      itemLocation = ((EditText) v.findViewById(R.id.itinerary_item_location))
          .getText().toString();
      itemStartDate = ((Button) v.findViewById(R.id.itinerary_item_startdate))
          .getText().toString();
      itemEndDate = ((Button) v.findViewById(R.id.itinerary_item_enddate))
          .getText().toString();
      itemStartTime = ((Button) v.findViewById(R.id.itinerary_item_starttime))
          .getText().toString();
      itemEndTime = ((Button) v.findViewById(R.id.itinerary_item_endtime))
          .getText().toString();
      itemDistance = Double.valueOf(((EditText) v
          .findViewById(R.id.itinerary_item_distance)).getText().toString());

      // ((ItineraryItem) getActivity()).createNewItineraryItem(itemTitle,
      // itemIsTrip, itemType, itemLocation, itemStartDate, itemEndDate,
      // itemStartTime, itemEndTime, itemDistance);
      //
      // getFragmentManager().popBackStack();
      break;
    case R.id.menu_cancel_itinerary_item:

      break;
    }
    return true;

  }

  private void preloadTypesSpinner(View v)
  {
    Spinner spinner = (Spinner) v.findViewById(R.id.itinerary_item_type);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        getActivity().getApplicationContext(), R.array.itinerary_type,
        android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new SpinnerActivity());
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

  private class SpinnerActivity extends Activity implements
      OnItemSelectedListener
  {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
        long id)
    {
      itemType = (String) parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
  }
}
