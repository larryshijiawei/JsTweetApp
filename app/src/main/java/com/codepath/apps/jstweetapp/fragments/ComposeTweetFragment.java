package com.codepath.apps.jstweetapp.fragments;

import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jiaweishi on 2/18/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    public static class ViewHolder{
        ImageView imageView_composerIcon;
        TextView textView_composerName;
        TextView textView_composerScreenName;
        EditText et_body;
        Button btn_postTweet;
    }


    public ComposeTweetFragment(){

    }

    public static ComposeTweetFragment getInstance(){
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        return fragment;
    }
}
