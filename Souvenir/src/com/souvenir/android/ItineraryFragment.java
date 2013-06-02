package com.souvenir.android;

import com.actionbarsherlock.view.MenuItem;
import com.souvenir.android.NoteFragment.btnFindPlace;

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
import android.widget.TextView;
import android.widget.Toast;

public class ItineraryFragment extends ParentFragment 
{
	private String selectedChild;
	private final int ITINERARY_REQUEST = 1714;

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 View v = inflater.inflate(R.layout.fragment_itinerary, null);
	 ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.itinerary_list);
	 elv.setAdapter(new ItineraryTripsListAdapter());
	 
	 setHasOptionsMenu(true);
	 
	 return v;
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
	    		System.out.println("Add to Itinerary button pressed");
		    	Toast.makeText(getActivity().getApplicationContext(), "Search Itinerary button pressed", Toast.LENGTH_SHORT).show();
				break;
	    }
	    return true;
	  }
	 
	 public class ItineraryTripsListAdapter extends BaseExpandableListAdapter {
	  
	 private String[] groups = { "Tokyo, Japan", "Seoul, S. Korea", "Bangkok, Thailand" };
	  
	 private String[][] children = {
	 { "Shinjuku", "Shibuya", "Harajuku", "Akihabara", "Odaiba", "Roppongi", "Asakusa" },
	 { "Itaewan", "Somgakji", "Noryangjin", "Gangnam", "Gyeongbokgung", "Hongdae" },
	 { "Nana", "Siam", "Wisgar", "Lub'd" }
	 };
	  
	 @Override
	 public int getGroupCount() {
	 return groups.length;
	 }
	  
	 @Override
	 public int getChildrenCount(int i) {
	 return children[i].length;
	 }
	  
	 @Override
	 public Object getGroup(int i) {
	 return groups[i];
	 }
	  
	 @Override
	 public Object getChild(int i, int i1) {
	 return children[i][i1];
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
	 textView.setText("    " + getGroup(i).toString());
	 return textView;
	 }
	  
	 @Override
	 public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
	 final TextView textView = new TextView(ItineraryFragment.this.getActivity());
	 textView.setTextSize(30);
	 textView.setText("\t" + getChild(i, i1).toString());
	 
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
