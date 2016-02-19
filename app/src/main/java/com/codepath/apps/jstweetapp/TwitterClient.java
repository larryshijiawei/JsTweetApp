package com.codepath.apps.jstweetapp;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "XqBcQ71EE6juDltkX0XVCa1cS";       // Change this
	public static final String REST_CONSUMER_SECRET = "tN8SpRY4y4MQzmvhGdpiMzfyEQKKetIaTY2RVquv3aZO5shkWe"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}


	//Home Timeline
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/home_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(maxId != -1)
			params.put("max_id", maxId-1);

		getClient().get(url, params, handler);

	}

	//Fetch user
	public void getUser(AsyncHttpResponseHandler handler){
		String url = getApiUrl("account/verify_credentials.json");

		RequestParams params = new RequestParams();
		params.put("skip_status", "true");

		getClient().get(url, params, handler);
	}

	//Post a new Tweet
	public void postTweet(String body, AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", body);

		getClient().post(url, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}