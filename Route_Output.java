package com.speedy.skytrain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.speedyskytrain.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Route_Output<var> extends Activity {
	public String first[];
	String start;
	String end;
	public double longitude[];
	public double latitude[];
	public List<String> allStations;
	public List<String> allInfo;
	MatsAwesomeXmlParser mat;
	String stations[];
	TextView text;
	TextView price;
	TextView duration;
	ListView list;
	LinkedHashMap	<String, List<String>> Hash_Stations;
	List<String> Station_list;
	ExpandableListView Exp_list;
	StationsAdapter adapter;
	private GoogleMap map;
	LatLng vancouver_center_focus = new LatLng(49.20593450433625,
			-122.94490814208984);
	Spinner spinner;
	Polyline line = null;
	JungPlay jung;
	ArrayList<Node> path;
	Marker marker = null;
	List<Marker> markers = new ArrayList<Marker>();
	List<Polyline> polyline = new ArrayList<Polyline>();
	Boolean mark = true;
	Bitmap halfBlue = null;
	Bitmap halfRed= null;
	int[] transfer;
	Button mar = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_output);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		   jung = new JungPlay(this);
		   mar= (Button)findViewById(R.id.btnMarker);
	        // 1. get passed intent 
	        Intent intent = getIntent();
	 
	        // 2. get message value from intent
	        String start = intent.getStringExtra("start");
	        String end = intent.getStringExtra("end"); 
	        allStations = new ArrayList<String>();
	        allInfo = new ArrayList<String>();
		    path = jung.shortestPath(start, end);
		    longitude = new double[path.size()];
			latitude = new double[path.size()];
		   for (int i = 0; i < path.size(); i++) {
			   allInfo.add(path.get(i).getInfo());
			   allStations.add(path.get(i).getName()); 
			   longitude[i]=path.get(i).getLong();
			   latitude[i]=path.get(i).getLat();
		   }
			
			duration();
			price();

			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();	
			map.setMyLocationEnabled(true);
			map.getUiSettings().setCompassEnabled(true);
			map.getMyLocation();

			vancouver_center_focus = new LatLng(latitude[(path.size())/2],
					longitude[(path.size())/2]);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					vancouver_center_focus, 0.5f));
			map.getUiSettings().setZoomControlsEnabled(true);
			   map.clear();
			   map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			   map.getUiSettings().setZoomControlsEnabled(true);

		   boolean flag=false;
		   int[] transfer = new int[path.size()];
		   for (int i = 0; i < path.size(); i++) {
			   transfer[i] = 100;
		   }

		   int temp=0;
		   //half blue marker size
		   Bitmap blue = BitmapFactory.decodeResource(getResources(),
                   R.drawable.blue_marker);
		   Bitmap halfBlue=Bitmap.createScaledBitmap(blue, blue.getWidth()/2,blue.getHeight()/2, false);

		   //half red marker size
		   Bitmap red = BitmapFactory.decodeResource(getResources(),
                   R.drawable.red_marker);
		   Bitmap halfRed=Bitmap.createScaledBitmap(red, red.getWidth()/2,red.getHeight()/2, false);


		   // show the markers and the polylines
		   for (int i = 0; i < path.size(); i++) {
		    LatLng test = new LatLng(latitude[i], longitude[i]);
		    for (int j =0;j<jung.getTransferStations().size();j++){
		    	if (i==jung.getTransferStations().get(j)){
		    		transfer[temp] = i;
		    		temp++;
		    		flag = true;
		    		marker = map.addMarker(new MarkerOptions()
				      .position(test)
				      .title(path.get(i).getName())
				      .icon(BitmapDescriptorFactory.fromBitmap(halfRed)));
		    		break;
		    	}
		    }
		    if (!flag){
		    	marker = map.addMarker(new MarkerOptions()
			      .position(test)
			      .title(path.get(i).getName())
			    .icon(BitmapDescriptorFactory.fromBitmap(halfBlue)));
		    }	    	
		    else
		    	flag = false;
		    	
		    markers.add(marker);
		    
		    if (i<path.size()-1)
		    line = map.addPolyline(new PolylineOptions()
		      .add(new LatLng(path.get(i).getLat(), path.get(i)
		        .getLong()),
		        new LatLng(path.get(i+1).getLat(), path.get(i+1)
		          .getLong())).width(5).color(Color.RED));
		    polyline.add(line);
		   }
		   
		   final LatLngBounds.Builder builder = new LatLngBounds.Builder();
		   for (Marker marker : markers) {
		       builder.include(marker.getPosition());
		   }
		   	   
		   map.setOnCameraChangeListener(new OnCameraChangeListener() {

			    @Override
			    public void onCameraChange(CameraPosition arg0) {
			        
			    	
					   LatLngBounds bounds = builder.build();
					   
					   int padding = 70;
					   CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
					   map.animateCamera(cu);  //with animation
					  // map.moveCamera(cu); //without animation
			        // Remove listener to prevent position reset on camera move.
			        map.setOnCameraChangeListener(null);
			    }
			});
   
//		   int[] transfer_pass = new int[temp];
//		   for (int i = 0; i < temp; i++) {
//			   transfer_pass[i] = transfer[i];
//		   }
		   
		    Exp_list = (ExpandableListView)findViewById(R.id.exp_list);
			Hash_Stations = Data_Provider.getInfo(allStations, allInfo, transfer);
			Station_list = new ArrayList<String>(Hash_Stations.keySet());
			adapter = new StationsAdapter(this, Hash_Stations, Station_list);
			
			Exp_list.setAdapter(adapter);
}
	
	public void duration(){
		duration = (TextView) findViewById(R.id.txtDuration);
		double dur;
		dur = path.size()*1.5;
		
		duration.setText(Html.fromHtml("<font color=#FF3401>" + "  ~" + dur + "</font>"));
		
	}
	
	public void price() {
		price = (TextView) findViewById(R.id.txtPrice);
		boolean zone1 = false;
		boolean zone2 = false;
		boolean zone3 = false;
		int z = 0;
		
		if(path.get(0).getName().equals("YVR Airport") ||
				path.get(0).getName().equals("Sea Island Centre") ||
				path.get(0).getName().equals("Templeton")){
			price.setText(Html.fromHtml("<font color=#FF3401>" + "C$ 10.00"
					+ "</font>"));
		}
		else{
		while (z < path.size()) {
			if(path.get(z).getZone() == 1)
			zone1 = true;
			else if(path.get(z).getZone()  == 2)
			zone2 = true;
			else if(path.get(z).getZone() == 3)
			zone3 = true;
			z++;	
		}

		if (zone1 && zone2 && zone3) {
			price.setText(Html.fromHtml("<font color=#FF3401>" + "C$ 5.50"
					+ "</font>"));
		} else if (zone1 && zone2 || zone1 && zone2
				|| zone2 && zone3) {
			price.setText(Html.fromHtml("<font color=#FF3401>" + "C$ 4.00"
					+ "</font>"));
		} else {
			price.setText(Html.fromHtml("<font color=#FF3401>" + "C$ 2.75"
					+ "</font>"));
		}
		}
	}
	
	
	public void ShowHideMarker(View v)
	{

		if(mark)
		{
			for(Marker marker: markers){
				marker.setVisible(false);
			}
			for(Polyline line: polyline){
				line.setVisible(false);
			}
			
			mar.setText(getString(R.string.show_markers) + "");
			mark = false;
		}
		else
		{
			for(Marker marker: markers){
				marker.setVisible(true);
			}
			for(Polyline line: polyline){
				line.setVisible(true);
			}
			mar.setText(getString(R.string.hide_markers) + "");
			mark = true;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.route_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		Intent inten;

	    // Handle item selection
	    switch (item.getItemId()) {
	       case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        case R.id.translink:
	        	startActivity(new Intent(
	    				Intent.ACTION_VIEW,
	    				Uri.parse("http://www.translink.ca/en/Schedules-and-Maps/SkyTrain.aspx")));
	            break;
	        case R.id.map:
	        	startActivity(new Intent(Route_Output.this, SkyMap.class));
	            break;
	        case R.id.info:
	        	inten = new Intent(this, info.class);
	        	startActivity(inten);
	            break;
	        case R.id.about:
	        	 intent = new Intent(this, about.class);
	        	 startActivity(intent);
	            break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    return true;
	}


}
