package com.speedy.skytrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

/*
 * This threaded task is used to generate HTTPS requests to the twitter
 * servers in order to gather information from the TransLink twitter feed.
 * 
 */
public class RetrieveInfoTask extends AsyncTask<String, Void, String> {

	//the secret keys must be kept secret.
	final static String CONSUMER_KEY = "qQawj72HYlgDX3zrlwccWV2M1";
	final static String CONSUMER_SECRET = "vx7zyw2zjz8EXxczJ7UrW4EZTe81lzo0VufUp4dGS7APydnN8I";
	final static String Access_Token = "3178524068-ImyWF0k94tN62qpCBRZgUoex4hOYxE5x87n8d7r";
	final static String Access_Token_Secret = "oO27Hu7V7qhfvKOpTJomwoDtFLL62pNqXqgtHUn6cQsMx";
	final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
	final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
	final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=";
	final static String TwitterUserTimeline = "https://api.twitter.com/1.1/statuses/user_timeline.json";
	final static String TwitterRateLimitStatus = "https://api.twitter.com/1.1/application/rate_limit_status.json";
	private String bearerToken;
	private String token;
	private final String SEARCH_LIMIT_STRING = "x-Rate-limit-limit";
	private final String SEARCH_REMAINING_STRING = "x-rate-limit-remaining";
	private final String SEARCH_REFRESH_STRING = "x-rate-limit-reset";
	private String searchLimit;
	private String searchRemaining;
	private String searchRefresh;
	
	private int NUMBER_OF_TWEETS = 100; //change the number of tweets from timeline including retweets.
	
	public TwitterAsyncResponse asyncResponse = null;

	
	public RetrieveInfoTask(TwitterAsyncResponse task){
		asyncResponse = task;
	}
	/*
	 * Code which runs after constructor.
	 * 
	 */
	@Override
	protected String doInBackground(String... params) {
		String res = null;
		res = getSearchString("");
		
//		if (params.length > 0) {
//			if (this.isTokenExpired()) {
//				this.invalidateToken();
//			}
//
//			res = getSearchString(params[0]);
//			Log.d("Twitter", res + "");
//			Log.d("Twitter", params[0] + "");
//			// this.invalidateToken();
//		}

		return res;
	}
	
	/*
	 * return information to main thread
	 * 
	 */
	 @Override
	   protected void onPostExecute(String result) {
	      asyncResponse.processFinish(result);
	   }

	 /*
	  * get bearer token and pass it to method which retrieves timeline tweets
	  * 
	  */
	private String getSearchString(String searchTerm) {
		String results = null;
		try {
			String encodedUrl = URLEncoder.encode(searchTerm, "UTF-8");
			String token = getToken(TwitterSearchURL + encodedUrl);
			//results = getString(TwitterSearchURL + encodedUrl);
			results = getUserTimeline();
		} catch (UnsupportedEncodingException e) {
			//Log.d("twitter", e + "sit gone wrong in getSearchString");
		} catch (IllegalStateException e) {
			//Log.d("twitter", e + "sit gone wrong in getSearchString");
		}

		return results;
	}

	/*
	 * Retrives oauth bearer token from twitter api
	 * 
	 */
	private String getToken(String bigurl) {
		String results = null;
		HttpsURLConnection c = null;
		try {
			String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
			String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");
			// String urlApiKey = URLEncoder.encode(Access_Token, "UTF-8");
			// String urlApiSecret = URLEncoder.encode(Access_Token_Secret,
			// "UTF-8");
			String parameter = "grant_type=client_credentials";
			// Concatenate the encoded consumer key, a colon character, and the
			// encoded consumer secret
			String combined = urlApiKey + ":" + urlApiSecret;
			String base64Encoded = Base64.encodeToString(combined.getBytes(),
					Base64.NO_WRAP);
			//Log.d("twitter keys", base64Encoded);
			// obtain bearer token
			// URL url = new URL(bigurl);
			URL url = new URL(TwitterTokenURL);
			c = (HttpsURLConnection) url.openConnection();
			//Log.d("twitter", c.toString());
			c.setRequestMethod("POST");
			c.setDoInput(true);
			c.setDoOutput(true);
			c.setRequestProperty("Host", "api.twitter.com");
			c.setRequestProperty("User-Agent", "anyApplication");
			c.addRequestProperty("Authorization", "Basic " + base64Encoded);
			c.setUseCaches(false);
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			//Log.d("twitter", url.toString());
			// write a request to the connection
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					c.getOutputStream()));
			wr.write(parameter);
			wr.flush();
			wr.close();
			Log.d("twitter buffered writer", wr.toString());

			// read the response not needed if only getting bearer token
			//Log.d("twitter response", c.getResponseCode() + "");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String line = "";
			StringBuilder answer = new StringBuilder();

			while ((line = br.readLine()) != null) {
				answer.append(line + System.getProperty("line.separator"));
			}
			// now the answer is the token
			//Log.d("Twitter token", "token: " + answer);

			JSONObject job = new JSONObject(answer.toString());
			String justtoken = job.getString("access_token");
			token = justtoken;
			//Log.d("twitter token", token);
			bearerToken = answer.toString();

//			if (c.getHeaderFields() != null) {
//				Map<String, List<String>> headers = c.getHeaderFields();
//				//Log.d("token headers", headers.toString());
//				//Log.d("token headers", headers.keySet().toString());
//				if (c.getHeaderField("expires_in") != null) {
//					//Log.d("expiration date", c.getHeaderField("expires_in"));
//				} else {
//					//Log.d("expiration date", "unable to get expires_in header");
//				}
//			} else {
//				//Log.d("token headers", "unable to get token headers");
//			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}

		return results;
	}

	/*
	 * Deprecated. Uses twitter search API which we will no longer be using.
	 * 
	 */
	@Deprecated
	private String getString(String bigurl) {
		String result = null;
		bigurl = "https://api.twitter.com/1.1/search/tweets.json?q=%40twitterapi";
		HttpsURLConnection c = null;
		try {
			URL url = new URL(bigurl);
			c = (HttpsURLConnection) url.openConnection();
			c.setDoInput(true);
			c.setDoOutput(false);
			c.setRequestMethod("GET");
			c.setRequestProperty("Host", "api.twitter.com");
			c.setRequestProperty("User-Agent", "anyApplication");
			// c.setRequestProperty("Authorization", "Bearer " + bearerToken);
			c.setRequestProperty("Authorization", "Bearer " + token);
			// String base64token =
			// Base64.encodeToString(bearerToken.getBytes(), Base64.NO_WRAP);
			String base64token = Base64.encodeToString(bearerToken.getBytes(),
					Base64.NO_WRAP);
			// c.setRequestProperty("Authorization", "Bearer " + base64token);
			// c.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			// c.setRequestProperty("Content-Type","application/json");
			c.setUseCaches(false);
			//Log.d("twitter response", c.getResponseCode() + "");
			//Log.d("twitter base64token", base64token);
			// read response
			String line = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader br;
			if (c.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(
						c.getErrorStream()));
			}
			while ((line = br.readLine()) != null) {
				// answer.append(line);
				answer.append(line + System.getProperty("line.separator"));
			}
			result = answer.toString();

			//Log.d("twitter returnable", answer.toString());
			
//			if (c.getHeaderFields() != null) {
//				Map<String, List<String>> headers = c.getHeaderFields();
//				Log.d("token headers", headers.toString());
//				Log.d("token headers", headers.keySet().toString());
//				
//				searchLimit = c.getHeaderField(SEARCH_LIMIT_STRING);
//				searchRemaining = c.getHeaderField(SEARCH_REMAINING_STRING);
//				searchRefresh = c.getHeaderField(SEARCH_REFRESH_STRING);
//				
//				
//				if (c.getHeaderField("expires_in") != null) {
//					Log.d("expiration date", c.getHeaderField("expires_in"));
//				} else {
//					Log.d("expiration date", "unable to get expires_in header");
//				}
//			} else {
//				Log.d("token headers", "unable to get token headers");
//			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}

		return result;
	}
	/*
	 * Will invalidate the bearer token, the next request for bearer token will
	 * return a new bearer token.
	 */
	private String invalidateToken() {
		// potentially returns 403 if attempted too frequently

		String s = "https://api.twitter.com/oauth2/invalidate_token";
		HttpsURLConnection c = null;

		try {
			String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
			String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");
			// String urlApiKey = URLEncoder.encode(Access_Token, "UTF-8");
			// String urlApiSecret = URLEncoder.encode(Access_Token_Secret,
			// "UTF-8");
			String combined = urlApiKey + ":" + urlApiSecret;
			String base64Encoded = Base64.encodeToString(combined.getBytes(),
					Base64.NO_WRAP);
			URL url = new URL(s);
			c = (HttpsURLConnection) url.openConnection();
			c.setRequestMethod("POST");
			c.setDoInput(true);
			c.setDoOutput(true);
			c.setRequestProperty("Authorization", "Basic " + base64Encoded);
			c.setRequestProperty("User-Agent", "anyApplication");
			c.setRequestProperty("Host", "api.twitter.com");
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			c.setUseCaches(false);
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					c.getOutputStream()));
			String token = "access_token=";
			JSONObject jo = new JSONObject(bearerToken);
			token += jo.getString("access_token");
			// wr.write(bearerToken);
			wr.write(token);
			wr.flush();
			wr.close();

			String line = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader br;
			if (c.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(
						c.getErrorStream()));

			}
			while ((line = br.readLine()) != null) {
				answer.append(line);
			}

			//Log.d("twitter returnable", c.getResponseCode() + answer.toString());
			//Log.d("twitter inval token", bearerToken);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}

		return s;

	}

	/*
	 * Although API rate limits are returned in the header of each reply, this method will
	 * return all the API rate limits. ...or possibly just the bearer token.
	 * 
	 */
	private void verifyCredentials() {

		String s = "https://api.twitter.com/oauth2/invalidate_token";
		HttpsURLConnection c = null;

		try {
			String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
			String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");
			// String urlApiKey = URLEncoder.encode(Access_Token, "UTF-8");
			// String urlApiSecret = URLEncoder.encode(Access_Token_Secret,
			// "UTF-8");
			String combined = urlApiKey + ":" + urlApiSecret;
			String base64Encoded = Base64.encodeToString(combined.getBytes(),
					Base64.NO_WRAP);
			URL url = new URL(s);
			c = (HttpsURLConnection) url.openConnection();
			c.setRequestMethod("POST");
			c.setDoInput(true);
			c.setDoOutput(true);
			c.setRequestProperty("Authorization", "Basic " + base64Encoded);
			c.setRequestProperty("User-Agent", "anyApplication");
			c.setRequestProperty("Host", "api.twitter.com");
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			c.setUseCaches(false);
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					c.getOutputStream()));
			String token = "access_token=";
			JSONObject jo = new JSONObject(bearerToken);
			token += jo.getString("access_token");
			// wr.write(bearerToken);
			wr.write(token);
			wr.flush();
			wr.close();

			String line = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader br;
			if (c.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(
						c.getErrorStream()));

			}
			while ((line = br.readLine()) != null) {
				answer.append(line);
			}

			//Log.d("twitter returnable", c.getResponseCode() + answer.toString());
			//Log.d("twitter inval token", bearerToken);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}

	}

	/*
	 * If bearerToken is expired it needs to be invalidated use this to check if
	 * the bearertoken is expired before sending requests
	 */
	private boolean isTokenExpired() {
		//possibly use rate_limit_context/access_token used for user stuf DO NOT USE
		//or rate_limit_context/application used for application-only USE THIS
		//application/rate_limit_status "https://api.twitter.com/1.1/application/rate_limit_status.json"
		
		//check for amount of requests
		//tokens do not typically expire. it is good practice to request a new token every 15minutes
		//only invalidate token after session ends
		//x-Rate-limit-limit rate limit ceiling for given request
		//x-rate-limi-remaining
		//x-rate-limit-rest time remaining until reset
//		searchLimit = c.getHeaderField(SEARCH_LIMIT_STRING);
//		searchRemaining = c.getHeaderField(SEARCH_REMAINING_STRING);
//		searchRefresh = c.getHeaderField(SEARCH_REFRESH_STRING);
		
		HttpsURLConnection c = null;
		
		
		
		return false;
	}
	
	
	/*
	 * retrieves the user's timeline. in this case, only translink's timeline.
	 * 
	 */
	private String getUserTimeline(){
		String results = null;
		int lowestID = 0;
		int sinceID = 0;
		HttpsURLConnection c = null;
		// all URL parameters added to collection
		ContentValues params = new ContentValues();
		//params.put("user_id", "");
		params.put("screen_name", "translink");
		params.put("count", NUMBER_OF_TWEETS + "");
		//params.put("max_id", lowestID); //returns tweets older than this ID
		//params.put("since_id", sinceID); //returns tweets younger or more recent than this ID
		
		params.put("exclude_replies", "true"); //prevent replies from being returned in timeline
		params.put("include_rts", "false"); //false will strip native retweets.
		
		// add parameters to url
		
		StringBuilder urlParams = new StringBuilder(TwitterUserTimeline);
		Object[] keys = params.keySet().toArray(); //nothing bad about this at all
		for(int i = 0; i < params.size(); i++){
			if(i==0){
				urlParams.append("?");
			} else {
				urlParams.append("&");
			}
			urlParams.append(keys[i] + "=" + params.getAsString((String) keys[i]));
			
		}
		
		
		
		
		try {
			URL url = new URL(urlParams.toString());
			c = (HttpsURLConnection) url.openConnection();
			c.setDoInput(true);
			c.setDoOutput(false);
			c.setRequestMethod("GET");
			c.setRequestProperty("Host", "api.twitter.com");
			c.setRequestProperty("User-Agent", "anyApplication");
			c.setRequestProperty("Authorization", "Bearer " + token);
			c.setUseCaches(false);
			// read response
			String line = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader br;
			if (c.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(
						c.getErrorStream()));
			}
			while ((line = br.readLine()) != null) {
				// answer.append(line);
				answer.append(line + System.getProperty("line.separator"));
			}
			results = answer.toString();

			//Log.d("twitter returnable", answer.toString());
			 
			/*
			 * get api info from headers
			 */
//			if (c.getHeaderFields() != null) {
//				Map<String, List<String>> headers = c.getHeaderFields();
//				Log.d("token headers", headers.toString());
//				Log.d("token headers", headers.keySet().toString());
//				
//				searchLimit = c.getHeaderField(SEARCH_LIMIT_STRING);
//				searchRemaining = c.getHeaderField(SEARCH_REMAINING_STRING);
//				searchRefresh = c.getHeaderField(SEARCH_REFRESH_STRING);
//				
//				
//				if (c.getHeaderField("expires_in") != null) {
//					Log.d("expiration date", c.getHeaderField("expires_in"));
//				} else {
//					Log.d("expiration date", "unable to get expires_in header");
//				}
//			} else {
//				Log.d("token headers", "unable to get token headers");
//			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}

		return results;
	}

}
