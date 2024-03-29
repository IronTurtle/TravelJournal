package com.souvenir.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.thrift.transport.TTransportException;

public class ItineraryFragment extends ParentFragment
{
  private ArrayList<String> tripsList;
  private ArrayList<ArrayList<String>> tripPlansList;
  private boolean refresh = false;

  private static final int NEW_ITINERARY_ITEM = 3171;
  private final int ITINERARY_REQUEST = 1714;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View v = inflater.inflate(R.layout.fragment_itinerary, null);
    setHasOptionsMenu(true);

    LinearLayout layout = (LinearLayout) v
        .findViewById(R.id.itinerary_add_item);
    layout.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Toast.makeText(getActivity().getApplicationContext(),
            "Adding new itinerary item", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(getActivity(), ItineraryItem.class),
            NEW_ITINERARY_ITEM);
      }
    });

    if (getArguments() != null && getArguments().containsKey("REFRESH"))
    {
      refresh = getArguments().getBoolean("REFRESH");
    }

    if (mEvernoteSession.isLoggedIn())
    {
      refresh = true;
      // Toast.makeText(getActivity().getApplicationContext(),
      // "Logged IN, Itinerary refresh", Toast.LENGTH_SHORT).show();
      getItinerary(refresh);
    }
    else
    {
      // Toast.makeText(getActivity().getApplicationContext(),
      // "Logged OUT, Itinerary refresh", Toast.LENGTH_SHORT).show();

    }

    return v;
  }

  private void getItinerary(boolean refresh)
  {
    // checks if itinerary needs to be refreshed
    if (!refresh)
    {
      return;
    }
    tripsList = new ArrayList<String>();
    tripPlansList = new ArrayList<ArrayList<String>>();

    // set refresh to false
    // refresh = false;
    int pageSize = 1;

    NoteFilter filter = new NoteFilter();
    filter.setWords("tag:app_itinerary");

    NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
    spec.setIncludeTitle(true);

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

                  NoteMetadata itineraryNote = notes.getNotes().get(0);
                  getItineraryData(itineraryNote);
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

  private void getItineraryData(NoteMetadata note)
  {
    String guid = note.getGuid();

    try
    {
      mEvernoteSession.getClientFactory().createNoteStoreClient()
          .getNoteContent(guid, new OnClientCallback<String>()
          {

            @Override
            public void onSuccess(String data)
            {
              String content = android.text.Html.fromHtml(data).toString();

              System.out.println("Content: " + content);
              String[] trips = content.split("\n");
              int j = 0;
              for (int i = 0; i < trips.length; i++)
              {
                String trip = trips[i];
                if (trip.length() == 0)
                {
                  continue;
                }
                // add new trip to list
                tripsList.add(trip.split(":")[0]);
                // add new list of plans for trip
                tripPlansList.add(new ArrayList<String>());

                String[] plans = (trip.split(":")[1]).split(",");
                // Toast.makeText(getActivity().getApplicationContext(), "Trip:"
                // + tripsList, Toast.LENGTH_LONG).show();
                for (String plan : plans)
                {
                  // add trip plan to list
                  (tripPlansList.get(j)).add(plan);
                }

                j++;
              }

              ExpandableListView elv = (ExpandableListView) getActivity()
                  .findViewById(R.id.itinerary_list);
              elv.setAdapter(new ItineraryTripsListAdapter());
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
      // TODO Auto-generated catch block
      e.printStackTrace();
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
    case R.id.itinerary_menu_add:
      System.out.println("Add to Itinerary button pressed");
      Toast.makeText(getActivity().getApplicationContext(),
          "Add to Itinerary button pressed", Toast.LENGTH_SHORT).show();
      break;

    case R.id.itinerary_menu_search:
      System.out.println("Searach to Itinerary button pressed");
      Toast.makeText(getActivity().getApplicationContext(),
          "Search Itinerary button pressed", Toast.LENGTH_SHORT).show();
      break;
    }
    return true;
  }

  public class ItineraryTripsListAdapter extends BaseExpandableListAdapter
  {

    @Override
    public int getGroupCount()
    {
      return tripsList.size();
    }

    @Override
    public int getChildrenCount(int i)
    {
      return (tripPlansList.get(i)).size();
    }

    @Override
    public Object getGroup(int i)
    {
      return tripsList.get(i);
    }

    @Override
    public Object getChild(int i, int i1)
    {
      return (tripPlansList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i)
    {
      return i;
    }

    @Override
    public long getChildId(int i, int i1)
    {
      return i1;
    }

    @Override
    public boolean hasStableIds()
    {
      return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
    {
      TextView textView = new TextView(ItineraryFragment.this.getActivity());
      textView.setTextSize(30);
      // System.out.println("Trip(group):" + getGroup(i).toString());
      textView.setText("    " + getGroup(i).toString());
      return textView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view,
        ViewGroup viewGroup)
    {
      final TextView textView = new TextView(
          ItineraryFragment.this.getActivity());
      textView.setTextSize(30);
      // System.out.println("Trip Plan(child):" + getGroup(i).toString());
      textView.setText(getChild(i, i1).toString());

      textView.setOnClickListener(new OnClickListener()
      {
        @Override
        public void onClick(View v)
        {

          // Show Place Finder Fragment
          // Toast.makeText(getActivity(),
          // "Location Field clicked",Toast.LENGTH_SHORT).show();
          startActivityForResult(new Intent(getActivity(), NoteActivity.class)
              .putExtra("ITINERARY_SELECT", textView.getText().toString()),
              ITINERARY_REQUEST);
        }
      });

      return textView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
      return true;
    }

  }

  /**
   * Called when the control returns from an activity that we launched.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode)
    {

    case NEW_ITINERARY_ITEM:
      Toast.makeText(getActivity().getApplicationContext(), "new itinerary",
          Toast.LENGTH_SHORT).show();
      if (resultCode == Activity.RESULT_OK)
      {
        if (data != null)
        {
          Toast.makeText(getActivity().getApplicationContext(),
              "NEW ITINERARY ITEM: " + data.getExtras(), Toast.LENGTH_SHORT)
              .show();
          // System.out.println("NEW ITINERARY ITEM: " +
          // data.getExtras().toString());
          createNewItineraryItem(data.getExtras());
        }
      }
      break;
    case ITINERARY_REQUEST:
      if (resultCode == Activity.RESULT_OK)
      {
        if (data != null)
        {
          Toast.makeText(getActivity().getApplicationContext(),
              data.toString(), Toast.LENGTH_LONG).show();
        }
      }
      break;
    }
  }

  private void createNewItineraryItem(Bundle bundle)
  {
    bundle.getString("ITINERARY_TITLE");
    bundle.getBoolean("ITINERARY_ISTRIP");
    bundle.getString("ITINERARY_TYPE");
    bundle.getString("ITINERARY_LOCATION");
    bundle.getString("ITINERARY_STARTDATE");
    bundle.getString("ITINERARY_ENDDATE");
    bundle.getString("ITINERARY_STARTTIME");
    bundle.getString("ITINERARY_ENDTIME");
    bundle.getDouble("ITINERARY_DISTANCE");
  }

  private void removeItineraryItem(int i, int i1)
  {
    // remove itinerary item if we want this feature.
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
      }
      catch (InvalidAuthenticationException e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      mEvernoteSession.authenticate(this.getActivity());
    }
    getItinerary(true);
  }
}
