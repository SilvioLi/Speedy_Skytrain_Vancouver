package com.speedy.skytrain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/*
 * Manages async thread and decodes JSON response from twitter.
 * 
 */
public class MatsTwitterMaster implements TwitterAsyncResponse{
	
	private String searchTerm = "skytrain";
	private String TAG = "TwitterMaster";
	private String messageFromTwitter;
	private String defaultMessage = "Fetching Data";
	private ArrayList<Tweet> tweetList;
	
	//RetrieveInfoTask rit = new RetrieveInfoTask();
	
	public MatsTwitterMaster(Context ctx) {
		
		//permissions for connectivity service must be enabled in manifest
		ConnectivityManager conMrg = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMrg.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected()){
			Log.d(TAG, "Connected to the net");
			
			new RetrieveInfoTask(this).execute(searchTerm);
			
			/*
			new RetrieveInfoTask(new TwitterAsyncResponse(){ //create threaded class for HTTPS API work. 
				@Override
				public void processFinish(String output) { //fill out interface
					Log.d(TAG, "THIS IS THE OUTPUT" + output );
					messageFromTwitter = output;
				}
			}).execute(searchTerm);
			
			*/
			
			//rit.asyncResponse = this;
			//getSkytrainDisplayMessage();
		} else {
			Log.d(TAG, "No connection available");
		}
	}

	@Override
	public void processFinish(String output) {
		//Log.d(TAG, output);
		messageFromTwitter = output;
		getSkytrainDisplayMessage();
		
	}
	
	public ArrayList<Tweet> getSkytrainDisplayMessage(){
		Tweet tweet = null;
		//Log.d("entranceeeeeeeeeeeeeeeeeeeee", this.messageFromTwitter);
		if(this.messageFromTwitter == null){
			Log.d("twiiiiiiiiiiiiiii", "message from twitter is null");
			ArrayList<Tweet> dMessage = new ArrayList<Tweet>();
			dMessage.add(new Tweet(null,null,defaultMessage,null,null,-1));
			return dMessage;
		} else {
			Log.d("terrrrrrrrrrrrrrrrrrrrrrrrrrrr", "twitter is not null");
			List<Tweet> arr = parseFromJson(messageFromTwitter);
			tweetList = (ArrayList<Tweet>) arr;
			for(Tweet tw: arr){
				Log.d(TAG, tw.getName() + " " + tw.getText() + " " + tw.getTimeStamp() + tw.getId());
			}
			
			tweet = parseFromJson(messageFromTwitter).get(0);
			Log.d(TAG, tweet.getText() + " " + tweet.getTimeStamp());
			return tweetList;
		}
	}
	
	private List<Tweet> parseFromJson(String jsonString){
		JSONObject job;
		List<Tweet> tweets = new ArrayList<Tweet>();
		Tweet tweet = null;
		String author = null;
		String screenName = null;
		String text = null;
		String date = null;
		List<Hashtag> tags = new ArrayList<Hashtag>();
		long id = -1;
		try {
			
			JSONArray jarr = new JSONArray(jsonString);
			
			for(int i = 0; i < jarr.length(); i++){
				job = jarr.getJSONObject(i);
				//boolean fav = job.getBoolean("favorited");
				JSONObject user = job.getJSONObject("user");
				JSONObject entities = job.getJSONObject("entities");
				JSONArray hashtags = entities.getJSONArray("hashtags");
				String hashtext = null;
				int[] indices = new int[2];
				for(int k = 0; k < hashtags.length(); k++){
					JSONObject hashbullshit = hashtags.getJSONObject(k);
					hashtext = hashbullshit.getString("text");
					JSONArray indicesbullshit = hashbullshit.getJSONArray("indices");
					indices[0] = indicesbullshit.getInt(0);
					indices[1] = indicesbullshit.getInt(1);
					//Log.e("HELP", "this has looped: " + k + " times!");
					tags.add(new Hashtag(hashtext, indices));
				}
				author = user.getString("name");
				screenName = user.getString("screen_name");
				text = job.getString("text");
				date = job.getString("created_at");
				id= Long.parseLong(job.getString("id_str"));
				//Hashtag[] yolo = (Hashtag[]) tags.toArray();
				tweet = new Tweet(author, screenName, text, date,tags, id);
				tweets.add(tweet);
			}
			
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return tweets;
	}
	
//	public List<Tweet> giveMeTweets(){
//		return new L
//	}

}
