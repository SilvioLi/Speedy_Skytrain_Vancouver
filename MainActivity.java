package com.speedy.skytrain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.speedyskytrain.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class MainActivity extends Activity {
	
	public String first[];
	public double longitude[];
	public double latitude[];
	AutoCompleteTextView start;
	AutoCompleteTextView end;
	Polyline line = null;
	JungPlay jung;
	ArrayList<Node> path;
	LatLng vancouver_center_focus = new LatLng(49.20593450433625,
			-122.94490814208984);
	MatsAwesomeXmlParser mat;
	String stations[];
	private ArrayList<TwitterDisplay> myTwitter;
	ArrayList<TwitterDisplay> myTwitter2;
	TextView text;
	TextView translinkBC;
	TextView TIME;
	public String searchTerm = "skytrain";
	String lang = "en";
	ListView twitterList;
	MatsTwitterMaster mtm;
	ArrayList<Tweet> tweets;
	MyListAdapter twitterAdapter;
	private Handler handler;
	private boolean Running = true;
	private boolean fetchDone = false;
	
	ConnectivityManager conMrg;
	NetworkInfo netInfo;
	int time = 500; //500
	String tweet_text ="";
	String dot = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		loadLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mat = new MatsAwesomeXmlParser(this);
		stations = new String[mat.getVertices().size()];
		
		for (int i = 0; i < mat.getVertices().size(); i++) 
		{
			stations[i] = mat.getVertices().get(i).getName();
		}
		
		start = (AutoCompleteTextView) findViewById(R.id.start_station);
		end = (AutoCompleteTextView) findViewById(R.id.end_station);
		start.setDropDownWidth(450);
		end.setDropDownWidth(450);
		
		end.setSelectAllOnFocus(true);
		start.setSelectAllOnFocus(true);
		start.requestFocus();

		//make expandable list smaller
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1, stations);
		start.setAdapter(adapter);
		end.setAdapter(adapter);
			
		//twitter stuff starts here
		
		myTwitter = new ArrayList<TwitterDisplay>();
		mtm = new MatsTwitterMaster(this); 
		tweets =  mtm.getSkytrainDisplayMessage();	
		
		twitterList = (ListView)findViewById(R.id.TwitterListView);
		twitterAdapter = new MyListAdapter();
		twitterList.setAdapter(twitterAdapter);	
		
		conMrg = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		netInfo = conMrg.getActiveNetworkInfo();
		handler = new Handler();
		Runnable runnable  = new Runnable(){

			@Override
			public void run() {
				while(Running){
					try{
						Thread.sleep(time);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					handler.post(new Runnable(){
						@Override
						public void run() {
							
					
							if((tweets.get(0).getText()).equals("Fetching Data"))
							{
								populateTwitterList();
							}
							else
							{
								populateAddTwitterList();							
							}
						}
						
					});
				}
			}
			
		};	
		new Thread(runnable).start();
	}
	
	
	
	
	private void populateTwitterList() 
	{
		tweets =  mtm.getSkytrainDisplayMessage();	
		Log.d("twweeeeetttsssss", (tweets.get(0).getText()) + "");
		twitterAdapter.clear();
		
		if(dot.equals("...."))
		{
			dot = "";
			mtm = new MatsTwitterMaster(this); 
		}
		
		for (int i=0; i<tweets.size(); i++) 
		{
			myTwitter.add(new TwitterDisplay(R.drawable.twitter_thumb,tweets.get(i).getText() + dot));
			
		}
		time = 1000;
		dot += ".";
		twitterAdapter.setData(myTwitter);
		twitterAdapter.notifyDataSetChanged();
	}
	
	private void populateAddTwitterList() 
	{
		fetchDone = true;
		tweets =  mtm.getSkytrainDisplayMessage();
		Log.d("populateAddTwitter","success");
//		Log.d("tweets", tweets.get(0).getText());
//		Log.d("tweet_text", tweet_text);
		if(tweet_text != tweets.get(0).getText())  //only redraw the listview when new tweet has arrived
		{
			twitterAdapter.clear();

		for (int i=0; i<tweets.size(); i++) 
		{
			myTwitter.add(new TwitterDisplay(R.drawable.twitter_thumb,tweets.get(i).getText()));
		}
		
			tweet_text = tweets.get(0).getText();
			time = 60000; //1 minute
			twitterAdapter.setData(myTwitter);
			twitterAdapter.notifyDataSetChanged();
			
		}
		else
		{
			mtm = new MatsTwitterMaster(this); 
		}

	}
	
	public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        //Log.d("languageeeeeeeeeeeeeeeee", language + "");
        changeLang(language);
    }

public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration(); 
        config.locale = myLocale; 
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

    }

public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

	private class MyListAdapter extends ArrayAdapter<TwitterDisplay>
	{
		private ArrayList<TwitterDisplay> tw = new ArrayList<TwitterDisplay>();
		public MyListAdapter() {
			super(MainActivity.this, R.layout.item_view, myTwitter);
		}
	
		public void setData(ArrayList<TwitterDisplay> myTwitter) 
		{
			tw = myTwitter;
		}
		
	
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//make sure we have a view to work with (may have been given null)
	
			if(convertView == null)
			{
				convertView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
			}
			
			if(fetchDone == true)
			{
				translinkBC = (TextView)convertView.findViewById(R.id.second);
				translinkBC.setText("TransLink BC");
				
				TIME = (TextView)convertView.findViewById(R.id.first);
					
				DateFormat formatter;
				
				  Date date = null;
				  Date now = new Date();
				  formatter = new SimpleDateFormat("EEE MMM dd hh:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
				  try {
					date = formatter.parse(tweets.get(position).getTimeStamp());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  long diff = now.getTime() - date.getTime();
				  long diffSeconds = diff / 1000;       
				  long diffMinutes = diff / (60*1000);  
				  long diffHours = diff / (60*60*1000);  
				  
				  if(diffHours <1)
				  {
					  if(diffMinutes > 1)
					  {
						  TIME.setText(String.valueOf(diffMinutes) + "min");
					  }
					  else
					  {
						  TIME.setText(String.valueOf(diffSeconds) + "sec");
					  }

				  }
				  else
				  {
						TIME.setText(String.valueOf(diffHours) + "h");
				  }
			
				
			}
			
			TwitterDisplay twitter = myTwitter.get(position);
			
			ImageView image = (ImageView)convertView.findViewById(R.id.twitter_image);
			image.setImageResource(twitter.getImage());
			
			TextView text = (TextView)convertView.findViewById(R.id.tweetText);
			text.setText(twitter.getTwitter_Info());

			return convertView;
		}
		
	}
	
	
	public void Clear(View v){
		start = (AutoCompleteTextView) findViewById(R.id.start_station);
		end = (AutoCompleteTextView) findViewById(R.id.end_station);
		
		start.setText("");
		end.setText("");
		start.requestFocus();
		
	}
		
	public void showPath(View v) 
	{

	  if (ErrorHandling()) {
		  
		  Intent intent = new Intent(this, Route_Output.class);
		  
		   // Create the bundle
		   Bundle bundle = new Bundle();
		   
		   AutoCompleteTextView start = (AutoCompleteTextView) findViewById(R.id.start_station);
		   AutoCompleteTextView end = (AutoCompleteTextView) findViewById(R.id.end_station);
		   String starts = start.getText().toString();
		   String ends = end.getText().toString();
		   // Add your data to bundle
		   bundle.putString("start", starts);
		   bundle.putString("end", ends);

		   // Add the bundle to the intent
		   intent.putExtras(bundle);

		   // Fire that second activity
		   startActivity(intent);
		   
	  } else {
	   WrongInput();
	  }

	 }

	public void tourist(View v) {
		if (ErrorHandling()) {

		} else {
			WrongInput();
		}
	}
	
	public boolean ErrorHandling() {
		boolean s = false;
		boolean e = false;
		for (int i = 0; i < mat.getVertices().size(); i++) {
			if (start.getText().toString()
					.equals(mat.getVertices().get(i).getName())) {
				s = true;
			}
		}
		for (int i = 0; i < mat.getVertices().size(); i++) {
			if (end.getText().toString()
					.equals(mat.getVertices().get(i).getName())) {
				e = true;
			}
		}
		if (start.getText().toString().equals(end.getText().toString())) {
			return false;
		}
		if (s && e) {
			return true;
		} else
			return false;
	}

	public void WrongInput() {
		text = (TextView) findViewById(R.id.scrollTextView);
		text.setText("");
		if (line != null) {
			line.remove();
		}

		Toast.makeText(this, "Please Enter valid skytrain names",
				Toast.LENGTH_SHORT).show();
	}


	public void goLink(View v) {
		startActivity(new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("http://www.translink.ca/en/Schedules-and-Maps/SkyTrain.aspx")));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		lang = "en";
		Locale locale = null;
		Configuration config;
		Intent inten;
		Intent intent;

	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.translink:
	        	startActivity(new Intent(
	    				Intent.ACTION_VIEW,
	    				Uri.parse("http://www.translink.ca/en/Schedules-and-Maps/SkyTrain.aspx")));
	            break;
	        case R.id.map:
	        	startActivity(new Intent(MainActivity.this, SkyMap.class));
	            break;
	        case R.id.portuguese:
	        	lang = "pt";	
	        	locale = new Locale(lang);
	        	saveLocale(lang);
				config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				finish();
				startActivity(getIntent());
	            break;
	        case R.id.english:
	        	lang = "en";
				locale = new Locale(lang);
	        	saveLocale(lang);
				config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				finish();
				startActivity(getIntent());
	            break;
	        case R.id.chinese:
	        	lang = "ch";
				locale = new Locale(lang);
	        	saveLocale(lang);
				config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				finish();
				startActivity(getIntent());
	            break;
	        case R.id.ukrainian:
	        	lang = "ua";
				locale = new Locale(lang);
	        	saveLocale(lang);
				config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				finish();
				startActivity(getIntent());
	            break;
	        case R.id.russian:
	        	lang = "ru";
				locale = new Locale(lang);
	        	saveLocale(lang);
				config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				finish();
				startActivity(getIntent());
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