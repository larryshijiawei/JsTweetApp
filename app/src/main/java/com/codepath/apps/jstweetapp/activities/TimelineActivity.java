package com.codepath.apps.jstweetapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.jstweetapp.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.jstweetapp.R;
import com.codepath.apps.jstweetapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.jstweetapp.TwitterApplication;
import com.codepath.apps.jstweetapp.TwitterClient;
import com.codepath.apps.jstweetapp.fragments.ComposeTweetFragment;
import com.codepath.apps.jstweetapp.fragments.ComposeTweetFragmentCallback;
import com.codepath.apps.jstweetapp.models.Tweet;
import com.codepath.apps.jstweetapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;


import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragmentCallback{
    private final String TAG = TimelineActivity.class.getSimpleName();
    private final int REQUEST_CODE = 200;

    private User mUser;

    private TwitterClient client;
    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets;

    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //prepare the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setLogo(R.drawable.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);


        //Set up the swipe refresh view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                loadTimeline();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        rvTweets = (RecyclerView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(this, tweets);
        adapter.setOnItemClickedListener(new TweetsArrayAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                //Open up the detail view
                Tweet selectedTweet = tweets.get(position);
                Intent intent = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                intent.putExtra("tweet", Parcels.wrap(selectedTweet));
                startActivity(intent);
            }

            @Override
            public void onReplyClicked(int position) {
                Tweet selectedTweet = tweets.get(position);
                openComposeDialog(selectedTweet.getUser().getScreenName());
            }
        });

        rvTweets.setAdapter(adapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //Get the id of the last tweet
                loadTimeline();
            }
        });


        client = TwitterApplication.getRestClient();

        loadUser();

        loadTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.actionComposeTweet){
            openComposeDialog(null);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostTweet(String textBody) {
        client = TwitterApplication.getRestClient();
        client.postTweet(textBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJSON(response);
                if (newTweet != null) {
                    tweets.add(0, newTweet);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, responseString);
            }
        });
    }

    private void openComposeDialog(String replyTo){
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment fragment = ComposeTweetFragment.getInstance();
        fragment.setUser(mUser);
        fragment.setReplyTo(replyTo);
        fragment.show(fm, "Fragment");
    }


    private void onlineLoadTimeline(long toId){
        client.getHomeTimeline(toId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, response.toString());
                //deserialize json, create models, load model data to listView
                ArrayList<Tweet> tweetsFromJson = Tweet.fromJSONArray(response);
                tweets.addAll(tweetsFromJson);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, errorResponse.toString());
            }
        });
    }

    private void offlineLoadTimeline(){
        //int offset = tweets.size();
        tweets.clear();
        List<Tweet> tweetsFromOffline = new Select()
                .from(Tweet.class)
                .orderBy("CreateOn")
                .execute();

        Collections.reverse(tweetsFromOffline);

        tweets.addAll(tweetsFromOffline);
        adapter.notifyDataSetChanged();
    }

    private void loadTimeline(){
        //In case of online
        if(isNetworkAvailable()){
            //Toast.makeText(getApplicationContext(), "Network is online", Toast.LENGTH_LONG).show();

            if(tweets.size() == 0){
                //In case of fresh load
                onlineLoadTimeline(-1);
            }
            else{
                long to = tweets.get(tweets.size()-1).getUid();
                onlineLoadTimeline(to);
            }
        }
        else{
            //In case of offline, load from cache
            Toast.makeText(getApplicationContext(), "Network is offline", Toast.LENGTH_LONG).show();
            offlineLoadTimeline();
        }
    }

    //Load the information of the current user
    private void loadUser(){
        if(isNetworkAvailable()){
            client.getUser(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mUser = User.fromJSON(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, errorResponse.toString());
                }
            });
        }
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
