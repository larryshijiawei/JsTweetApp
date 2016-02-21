package com.codepath.apps.jstweetapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.jstweetapp.CircleTransform;
import com.codepath.apps.jstweetapp.FixedWidthTransformation;
import com.codepath.apps.jstweetapp.R;
import com.codepath.apps.jstweetapp.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcel;
import org.parceler.Parcels;
import org.w3c.dom.Text;

public class TweetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ImageView iv_icon = (ImageView) findViewById(R.id.iv_tweetDetail_composerIcon);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).transform(new CircleTransform()).into(iv_icon);

        TextView tv_name = (TextView) findViewById(R.id.tv_tweetDetail_composerName);
        tv_name.setText(tweet.getUser().getName());

        TextView tv_screenName = (TextView) findViewById(R.id.tv_tweetDetail_screenName);
        tv_screenName.setText("@"+tweet.getUser().getScreenName());

        TextView tv_body = (TextView) findViewById(R.id.tv_tweetDetail_body);
        tv_body.setText(tweet.getBody());

        //int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        ImageView iv_image = (ImageView) findViewById(R.id.iv_tweetDetail_image);
        Picasso.with(this).load(tweet.getImageUrl()).transform(new FixedWidthTransformation(700)).into(iv_image);

        TextView tv_timeStamp = (TextView) findViewById(R.id.tv_tweetDetail_timeStamp);
        tv_timeStamp.setText(tweet.getTimeStamp());

    }
}
