package com.speedy.skytrain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Color;

public class Data_Provider {

	public static LinkedHashMap<String, List<String>> getInfo(List<String> allStations, List<String> allInfo, int[] transfer){

		 LinkedHashMap<String, List<String>> Station_Info = new LinkedHashMap<String, List<String>>();
		 boolean check = false;
		 for(int i =0; i < allStations.size(); i++)
		 {
			 for(int j =0; j < transfer.length; j++)
			 { 
				 if(i == transfer[j])
				 {
					 check = true;
					 List<String> ra = new ArrayList<String>();
					 ra.add(allInfo.get(i));
					 Station_Info.put( allStations.get(i) + " (Transfer Line)", ra);
					 break;
				 }
			 }
			 
			if(!check)
			 {
				 List<String> ra = new ArrayList<String>();
				 ra.add(allInfo.get(i));
				 Station_Info.put( allStations.get(i), ra);
				 check = false;
			 }
			else
			{
				check = false;
			}
		 }
		 return Station_Info;
	 }

	 
}
