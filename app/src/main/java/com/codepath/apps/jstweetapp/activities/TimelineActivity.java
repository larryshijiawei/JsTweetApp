package com.codepath.apps.jstweetapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.jstweetapp.EndlessRecyclerViewScrollListener;
import com.codepath.apps.jstweetapp.R;
import com.codepath.apps.jstweetapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.jstweetapp.TwitterApplication;
import com.codepath.apps.jstweetapp.TwitterClient;
import com.codepath.apps.jstweetapp.fragments.ComposeTweetFragment;
import com.codepath.apps.jstweetapp.models.Tweet;
import com.codepath.apps.jstweetapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;


public class TimelineActivity extends AppCompatActivity {
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

        //Set up the swipe refresh view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                loadMoreTimeline(-1);
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
        rvTweets.setAdapter(adapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //Get the id of the last tweet
                long sinceId = tweets.get(tweets.size()-1).getUid();
                loadMoreTimeline(sinceId);
            }
        });


        client = TwitterApplication.getRestClient();

        loadUser();

        loadMoreTimeline(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.actionComposeTweet){
            Toast.makeText(getApplicationContext(), "Compose Tweet", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, TweetActivity.class);
//            if(mUser != null)
//                intent.putExtra("user", Parcels.wrap(mUser));
//            //startActivity(intent);
//            startActivityForResult(intent, REQUEST_CODE);

            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment fragment = ComposeTweetFragment.getInstance();
            fragment.show(fm, "Fragment");

            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("newTweet"));
            if(newTweet != null){
                tweets.add(0, newTweet);
                adapter.notifyDataSetChanged();
            }
        }
    }

    //The load will give back the tweets which is older than the toId
    //-1 means to get the very first page
    private void loadMoreTimeline(long toId){
        client.getHomeTimeline(toId,new JsonHttpResponseHandler(){
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

    //Load the information of the current user
    private void loadUser(){
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
