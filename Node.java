package com.speedy.skytrain;

import android.util.Log;


public class Node {

	

	private String stationName;
	private int id; //maybe
	private String[] stationLine;
	private int fareZone;
	private double longitude;
	private double latitude;
	private String info;
	
	
	Node(){
		
	}
	
	Node(String name, String[] lines, int zone, double longitude, double latitude, String i){
		
		this.stationName = name;
		this.stationLine = lines;
		this.fareZone = zone;
		this.longitude = longitude;
		this.latitude = latitude;
		this.info = i;
	}
	
	
	public void setName(String s){
		
		this.stationName = s;
	}
	
	public String getName(){
		return this.stationName;
	}
	
	public void setId(int x){
		this.id = x;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setLine(String[] arr){
		
		this.stationLine = arr;
	}
	
	public String[] getLine(){
		return this.stationLine;
	}
	
	public void setZone(int x){
		
		this.fareZone = x;
	}
	
	public int getZone(){
		return this.fareZone;
	}
	
	public double getLong(){
		//Log.d("MAT_NODE_LONGITUDE", this.longitude+"");
		return this.longitude;
		
	}
	
	public void setLong(Double l){
		this.longitude = l;
	}
	
	public double getLat(){
		return this.latitude;
	}
	
	public void setLat(Double l){
		this.latitude = l;
	}
	
	public String getInfo(){
		return this.info;
	}
	
	public void setInfo(String s){
		this.info = s;
	}
	
}

