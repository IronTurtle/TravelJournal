package com.souvenir.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class ItineraryFragment extends ParentFragment 
{

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 View v = inflater.inflate(R.layout.fragment_itinerary, null);
	 ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.itinerary_list);
	 elv.setAdapter(new SavedTabsListAdapter());
	 return v;
	 }
	  
	 public class SavedTabsListAdapter extends BaseExpandableListAdapter {
	  
	 private String[] groups = { "Tokyo, Japan", "Seoul, S.Korea", "Bangkok, Thailand" };
	  
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
	 TextView textView = new TextView(ItineraryFragment.this.getActivity());
	 textView.setTextSize(30);
	 textView.setText("\t" + getChild(i, i1).toString());
	 return textView;
	 }
	  
	 @Override
	 public boolean isChildSelectable(int i, int i1) {
	 return true;
	 }
	  
	 }

}
