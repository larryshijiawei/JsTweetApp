package com.codepath.apps.jstweetapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.jstweetapp.CircleTransform;
import com.codepath.apps.jstweetapp.R;
import com.codepath.apps.jstweetapp.models.User;
import com.squareup.picasso.Picasso;

/**
 * Created by jiaweishi on 2/18/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    private ImageView imageView_composerIcon;
    private TextView textView_composerName;
    private TextView textView_composerScreenName;
    private EditText et_body;
    private TextView textView_charCount;
    private Button btn_postTweet;
    private ImageView imageView_cancel;

    private User mUser;
    private String replyTo;


    public ComposeTweetFragment(){

    }

    public static ComposeTweetFragment getInstance(){
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        return fragment;
    }

    public void setUser(User user){
        mUser = user;
    }

    public void setReplyTo(String replyTo){
        this.replyTo = replyTo;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(760, 1200);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        imageView_composerIcon = (ImageView) view.findViewById(R.id.iv_composer_icon);
        Picasso.with(view.getContext()).load(mUser.getProfileImageUrl()).transform(new CircleTransform()).into(imageView_composerIcon);

        textView_composerName = (TextView) view.findViewById(R.id.tv_composer_fullName);
        textView_composerName.setText(mUser.getName());

        textView_composerScreenName = (TextView) view.findViewById(R.id.tv_composer_screenName);
        textView_composerScreenName.setText("@"+mUser.getScreenName());

        textView_charCount = (TextView) view.findViewById(R.id.tv_remainCharacters);

        btn_postTweet = (Button) view.findViewById(R.id.btn_postTweet);
        btn_postTweet.setEnabled(false);
        btn_postTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComposeTweetFragmentCallback callback = (ComposeTweetFragmentCallback) getActivity();
                callback.onPostTweet(et_body.getText().toString());
                dismiss();
            }
        });

        et_body = (EditText) view.findViewById(R.id.et_newTweet_body);
        if(replyTo != null)
            et_body.setText("@"+replyTo, TextView.BufferType.EDITABLE);

        et_body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateCharCounts(editable.toString().length());
            }
        });

        imageView_cancel = (ImageView) view.findViewById(R.id.iv_cancel);
        imageView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void updateCharCounts(int count){
        String str = Integer.toString(140-count);
        textView_charCount.setText(str);

        //update the status of button
        if(count >0 && count <= 140){
            btn_postTweet.setBackgroundResource(R.color.dark_blue);
            btn_postTweet.setTextColor(getResources().getColor(R.color.white));
            btn_postTweet.setEnabled(true);
        }
        else{
            btn_postTweet.setBackgroundResource(R.color.light_blue);
            btn_postTweet.setTextColor(getResources().getColor(R.color.dark_blue));
            btn_postTweet.setEnabled(false);
        }
    }

}
