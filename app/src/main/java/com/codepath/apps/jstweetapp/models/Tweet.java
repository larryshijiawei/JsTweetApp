package com.codepath.apps.jstweetapp.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jiaweishi on 2/17/16.
 */
@Parcel
public class Tweet {
    String body;
    long uid;
    String createOn;
    User user;


    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreateOn() {
        return createOn;
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createOn).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createOn = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> list = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                Tweet newTweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                list.add(newTweet);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return list;
    }
}