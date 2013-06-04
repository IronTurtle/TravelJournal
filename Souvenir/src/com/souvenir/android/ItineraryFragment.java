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
	private ArrayList<String> tripsList = new ArrayList<String>();
	private ArrayList<ArrayList<String>> tripPlansList = new ArrayList<ArrayList<String>>();
	private HashMap<String, String> tripMap = new HashMap<String, String>();
	
	private final int ITINERARY_REQUEST = 1714;

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 View v = inflater.inflate(R.layout.fragment_itinerary, null);
	 setHasOptionsMenu(true);
	 
	 getItinerary();
	 
	 
	 return v;
	 }
	 
	private void getItinerary() {
		
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

					List<NoteMetadata> notesList = notes.getNotes();
					
					for(int i = 0; i < notesList.size(); i++) {
						System.out.println(notesList.get(i).getTitle());
						Toast.makeText(getActivity().getApplicationContext(), notesList.get(i).getTitle(), Toast.LENGTH_SHORT).show();
					}
					
					NoteMetadata itineraryNote = notes.getNotes().get(0);
					getItineraryData(itineraryNote);
					
					
					/*ListView listView = (ListView) SnippetFragment.this
							.getView().findViewById(R.id.lview);
					entries.addAll(notes.getNotes());
					entries2 = new ArrayList<Note>(entries
							.size());
					final SnippetAdapter adapter = new SnippetAdapter(
							SnippetFragment.this.getActivity(),
							R.layout.snippet, entries2,
							mEvernoteSession);
					listView.setAdapter(adapter);
					listView.setScrollingCacheEnabled(false);

					// Log.e("log_tag ******",
					// notes.getNotes().get(0).getTitle());
					// Log.e("log_tag ******",
					// entries.get(0).getTitle());
					for (int i = 0; i < entries.size(); i++) {
						NoteMetadata snippetEntry = entries
								.get(i);
						final int position = i;
						try {
							mEvernoteSession
									.getClientFactory()
									.createNoteStoreClient()
									.getNote(
											snippetEntry
													.getGuid(),
											true,
											true,
											true,
											true,
											new OnClientCallback<Note>() {
												@Override
												public void onSuccess(
														Note note) {
													// contents.add(android.text.Html.fromHtml(note.getContent()).toString());
													entries2.add(
															position,
															note);
													adapter.notifyDataSetChanged();
													// snippetText.setText(android.text.Html.fromHtml(note
													// .getContent()));
													// removeDialog(DIALOG_PROGRESS);
													// System.out.println(""
													// +
													// position
													// +
													// snippetText.getText());
													// Toast.makeText(SnippetFragment.this.getActivity(),
													// snippetText.getText(),
													// Toast.LENGTH_LONG).show();
													// notes =
													// data;
												}

												@Override
												public void onException(
														Exception exception) {

												}
											});
						
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					*/
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

					//Toast.makeText(getActivity().getApplicationContext(), content, Toast.LENGTH_LONG).show();
					System.out.println("Content: " + content);
					String[] trips = content.split("\n");
					int j = 0;
					for(int i = 0; i < trips.length;i++) {
						String trip = trips[i];
						if(trip.length() == 0){
							continue;
						}
						//System.out.println("Trip:" + trip);
						//add new trip to list
						tripsList.add(trip.split(":")[0]);
						//add new list of plans for trip
						tripPlansList.add(new ArrayList<String>());
						
						String[] plans = (trip.split(":")[1]).split(",");
						//Toast.makeText(getActivity().getApplicationContext(), "Trip:" + tripsList, Toast.LENGTH_LONG).show();
						for(String plan : plans) {
							//Toast.makeText(getActivity().getApplicationContext(), "Plan:" + plan, Toast.LENGTH_LONG).show();
							//System.out.println("Plan:" + plan);
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
	 System.out.println("Group:" + getGroup(i).toString());
	 textView.setText("    " + getGroup(i).toString());
	 return textView;
	 }
	  
	 @Override
	 public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
	 final TextView textView = new TextView(ItineraryFragment.this.getActivity());
	 textView.setTextSize(30);
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
		
		/*class btnCreateItineraryNote implements Button.OnClickListener {
			@Override
			public void onClick(View v) {
				
				// Show Place Finder Fragment
				//Toast.makeText(getActivity(), "Location Field clicked",Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(getActivity(),
						NoteActivity.class).putExtra("ITINERARY_SELECT", selectedChild), ITINERARY_REQUEST);
			}
		}*/

}
