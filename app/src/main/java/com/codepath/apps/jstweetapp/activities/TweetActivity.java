package com.codepath.apps.jstweetapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.jstweetapp.R;
import com.codepath.apps.jstweetapp.TwitterApplication;
import com.codepath.apps.jstweetapp.TwitterClient;
import com.codepath.apps.jstweetapp.models.Tweet;
import com.codepath.apps.jstweetapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

public class TweetActivity extends AppCompatActivity {
    private final String TAG = TweetActivity.class.getSimpleName();

    private User mUser;

    private ImageView iv_composerIcon;
    private TextView tv_composerName;
    private EditText et_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        mUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        et_body = (EditText) findViewById(R.id.et_composeBody);

        tv_composerName = (TextView) findViewById(R.id.tv_composerScreenName);
        tv_composerName.setText("@"+mUser.getScreenName());

        iv_composerIcon = (ImageView) findViewById(R.id.iv_composerIcon);
        iv_composerIcon.setImageResource(android.R.color.transparent);
        Picasso.with(getApplicationContext()).load(mUser.getProfileImageUrl()).into(iv_composerIcon);
    }


    public void onPostTweet(View view) {
        String body = et_body.getText().toString();

        TwitterClient client = TwitterApplication.getRestClient();
        client.postTweet(body, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJSON(response);
                Intent data = new Intent();
                data.putExtra("newTweet", Parcels.wrap(newTweet));
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, responseString);
            }
        });

    }
}
