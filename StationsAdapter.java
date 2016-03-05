package com.speedy.skytrain;

import java.util.LinkedHashMap;
import java.util.List;

import com.example.speedyskytrain.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class StationsAdapter extends BaseExpandableListAdapter{

	private Context ctx;
	private LinkedHashMap<String, List<String>> Hash_Stations;
	private List<String> Station_list;
	
	public StationsAdapter(Context ctx, LinkedHashMap<String, List<String>> Hash_Stations, List<String> Station_list){
		
		this.ctx = ctx;
		this.Hash_Stations = Hash_Stations;
		this.Station_list = Station_list;

	}
	
	@Override
	public Object getChild(int parent, int child) {

		return Hash_Stations.get(Station_list.get(parent)).get(child);
	}

	@Override
	public long getChildId(int parent, int child) {
		
		return child;
	}

	@Override
	public View getChildView(int parent, int child,
			boolean lastChild, View convertView, ViewGroup parentView) {
		
		String child_title = (String) getChild(parent, child);
		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.child_layout, parentView, false);
		}
		TextView  child_textview = (TextView) convertView.findViewById(R.id.child_txt);
		child_textview.setText(child_title);
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		
		return Hash_Stations.get(Station_list.get(arg0)).size();
	}

	@Override
	public Object getGroup(int arg0) {
		
		return Station_list.get(arg0);
	}

	@Override
	public int getGroupCount() {

		return Station_list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
	
		return groupPosition;
	}

	@Override
	public View getGroupView(int parent, boolean isExpanded,
			View convertView, ViewGroup parentView) {
		String group_title = (String) getGroup(parent);
		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.parent_layout, parentView, false);
		}
		TextView parent_textView = (TextView) convertView.findViewById(R.id.parent_txt);
		parent_textView.setTypeface(null, Typeface.BOLD);
		parent_textView.setText(group_title);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
