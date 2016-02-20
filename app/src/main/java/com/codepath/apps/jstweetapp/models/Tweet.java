package com.codepath.apps.jstweetapp.models;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

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
@Table(name = "Tweets")
@Parcel(analyze = {Tweet.class})
public class Tweet extends Model {

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long uid;

    @Column(name = "Body")
    String body;

    @Column(name = "CreateOn")
    String createOn;

    @Column(name = "RetweetCount")
    int reTweetCount;

    @Column(name = "FavoriteCount")
    int favoriteCount;

    @Column(name = "ImageUrl")
    String imageUrl;

    @Column(name="User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
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

    public int getReTweetCount() {
        return reTweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public String getImageUrl() {
        return imageUrl;
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
            tweet.reTweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");

            //Fetch the image url
            JSONObject entities = jsonObject.getJSONObject("entities");
            if(entities.has("media")){
                JSONArray medias = entities.getJSONArray("media");
                if(medias.length() != 0){
                    tweet.imageUrl = medias.getJSONObject(0).getString("media_url");
                }
            }

            tweet.user = User.findOrCreateFromJson(jsonObject.getJSONObject("user"));

            tweet.save();
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
