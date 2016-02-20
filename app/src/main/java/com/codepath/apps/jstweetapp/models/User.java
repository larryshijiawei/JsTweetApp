package com.codepath.apps.jstweetapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by jiaweishi on 2/17/16.
 */

@Table(name= "Users")
@Parcel(analyze = {User.class})
public class User extends Model{
    @Column(name = "remote_id", unique = true)
    long uid;

    @Column(name = "Name")
    String name;

    @Column(name = "ScreenName")
    String screenName;

    @Column(name = "ProfileImageUrl")
    String profileImageUrl;



    public User(){
        super();
    }


    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");

            //save to DB
            //user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User findOrCreateFromJson(JSONObject json){
        long rid = 0;
        try {
            rid = json.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        User existingUser = new Select().from(User.class).where("remote_id = ?", rid).executeSingle();
        if(existingUser != null)
            return existingUser;
        else{
            User user = User.fromJSON(json);
            user.save();
            return user;
        }
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
