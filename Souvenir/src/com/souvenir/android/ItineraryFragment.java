package com.souvenir.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.view.MenuItem;
import com.evernote.client.android.AsyncNoteStoreClient;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.thrift.transport.TTransportException;
import com.souvenir.android.NoteFragment.btnFindPlace;
import com.souvenir.android.SnippetFragment.SnippetAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItineraryFragment extends ParentFragment 
{
	private ArrayList<String> tripsList;
	private ArrayList<ArrayList<String>> tripPlansList;
	private boolean refresh;
	
	private final int ITINERARY_REQUEST = 1714;

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 View v = inflater.inflate(R.layout.fragment_itinerary, null);
	 setHasOptionsMenu(true);
	 
	 refresh = getArguments().getBoolean("REFRESH", false);
	 
	 getItinerary(refresh);
	 
	 
	 return v;
	 }
	 
	private void getItinerary(boolean refresh) {
		//checks if itinerary needs to be refreshed
		if(!refresh){
			return;
		}
		tripsList = new ArrayList<String>();
		tripPlansList = new ArrayList<ArrayList<String>>();
		
		//set refresh to false
		refresh = false;
		int pageSize = 1;
		 
		NoteFilter filter = new NoteFilter();
		filter.setWords("tag:app_itinerary");
		 
		NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
		spec.setIncludeTitle(true);
		 
		try {
			mEvernoteSession.getClientFactory().createNoteStoreClient()
				.findNotesMetadata(filter, 0, pageSize, spec, new OnClientCallback<NotesMetadataList>() {
				@Override
				public void onSuccess(NotesMetadataList notes) {
					
					NoteMetadata itineraryNote = notes.getNotes().get(0);
					getItineraryData(itineraryNote);
				}

				@Override
				public void onException(Exception exception) {
				}
							});
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
	
	private void getItineraryData(NoteMetadata note)
	{
		String guid = note.getGuid();
		
		try {
			mEvernoteSession.getClientFactory().createNoteStoreClient()
			  .getNoteContent(guid, new OnClientCallback<String>()
			  {

				@Override
				public void onSuccess(String data) {
					String content = android.text.Html.fromHtml(data).toString();

					System.out.println("Content: " + content);
					String[] trips = content.split("\n");
					int j = 0;
					for(int i = 0; i < trips.length;i++) {
						String trip = trips[i];
						if(trip.length() == 0){
							continue;
						}
						//add new trip to list
						tripsList.add(trip.split(":")[0]);
						//add new list of plans for trip
						tripPlansList.add(new ArrayList<String>());
						
						String[] plans = (trip.split(":")[1]).split(",");
						//Toast.makeText(getActivity().getApplicationContext(), "Trip:" + tripsList, Toast.LENGTH_LONG).show();
						for(String plan : plans) {
							//add trip plan to list
							(tripPlansList.get(j)).add(plan);
						}

						j++;
					}
					

					 ExpandableListView elv = (ExpandableListView) getActivity().findViewById(R.id.itinerary_list);
					 elv.setAdapter(new ItineraryTripsListAdapter());
				}

				@Override
				public void onException(Exception exception) {
					exception.printStackTrace();
					
				}
			  });
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item)
	  {
	    // This uses the imported MenuItem from ActionBarSherlock
	    //Toast.makeText(this.getActivity(), "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
	    switch (item.getItemId())
	    {
	    	case R.id.itinerary_menu_add:
		    	System.out.println("Add to Itinerary button pressed");
		    	Toast.makeText(getActivity().getApplicationContext(), "Add to Itinerary button pressed", Toast.LENGTH_SHORT).show();
				break;
				
	    	case R.id.itinerary_menu_search:
	    		System.out.println("Searach to Itinerary button pressed");
		    	Toast.makeText(getActivity().getApplicationContext(), "Search Itinerary button pressed", Toast.LENGTH_SHORT).show();
				break;
	    }
	    return true;
	  }
	 
	 public class ItineraryTripsListAdapter extends BaseExpandableListAdapter {
	 
	 @Override
	 public int getGroupCount() {
	 return tripsList.size();
	 }
	  
	 @Override
	 public int getChildrenCount(int i) {
	 return (tripPlansList.get(i)).size();
	 }
	  
	 @Override
	 public Object getGroup(int i) {
	 return tripsList.get(i);
	 }
	  
	 @Override
	 public Object getChild(int i, int i1) {
	 return (tripPlansList.get(i)).get(i1);
	 }
	  
	 @Override
	 public long getGroupId(int i) {
	 return i;
	 }
	  
	 @Override
	 public long getChildId(int i, int i1) { 	
	 return i1;
	 }
	  
	 @Override
	 public boolean hasStableIds() {
	 return true;
	 }
	  
	 @Override
	 public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
	 TextView textView = new TextView(ItineraryFragment.this.getActivity());
	 textView.setTextSize(30);
	 //System.out.println("Trip(group):" + getGroup(i).toString());
	 textView.setText("    " + getGroup(i).toString());
	 return textView;
	 }
	  
	 @Override
	 public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
	 final TextView textView = new TextView(ItineraryFragment.this.getActivity());
	 textView.setTextSize(30);
	 //System.out.println("Trip Plan(child):" + getGroup(i).toString());
	 textView.setText(getChild(i, i1).toString());
	 
	 textView.setOnClickListener(new OnClickListener(){
		 @Override
			public void onClick(View v) {
				
				// Show Place Finder Fragment
				//Toast.makeText(getActivity(), "Location Field clicked",Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(getActivity(),
						NoteActivity.class).putExtra("ITINERARY_SELECT", textView.getText().toString()), ITINERARY_REQUEST);
			}
	 });
	 
	 return textView;
	 }
	  
	 @Override
	 public boolean isChildSelectable(int i, int i1) {
	 return true;
	 }
	  
	 }
	 
	 /**
		 * Called when the control returns from an activity that we launched.
		 */
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			// Grab image data when picker returns result
			case ITINERARY_REQUEST:
				if (resultCode == Activity.RESULT_OK) {
					if(data != null){
						Toast.makeText(getActivity().getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
					}
				}
				break;
			}
		}
	
		private void removeItineraryItem(int i, int i1)
		{
			//remove itinerary item if we want this feature.
		}

}
