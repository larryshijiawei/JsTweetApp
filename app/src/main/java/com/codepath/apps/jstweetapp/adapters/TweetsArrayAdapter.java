package com.codepath.apps.jstweetapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.jstweetapp.CircleTransform;
import com.codepath.apps.jstweetapp.FixedWidthTransformation;
import com.codepath.apps.jstweetapp.R;
import com.codepath.apps.jstweetapp.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jiaweishi on 2/17/16.
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickedListener{
        void onItemClicked(int position);

        void onReplyClicked(int position);
    }

    private OnItemClickedListener mOnItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.mOnItemClickedListener = listener;
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvScreenName;
        public TextView tvBody;
        public TextView tvTimeStamp;
        public TextView tvRetweetCount;
        public TextView tvFavoriteCount;
        public ImageView ivTweetImage;
        public ImageView ivReTweet;
        public ImageView ivFavorite;
        public ImageView ivReplyTo;


        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tv_retweet_count);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tv_favorite_count);
            ivTweetImage = (ImageView) itemView.findViewById(R.id.iv_tweetImage);
            ivReTweet = (ImageView) itemView.findViewById(R.id.iv_retweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
            ivReplyTo = (ImageView) itemView.findViewById(R.id.iv_reply);
        }

    }


    private Context mContext;
    private List<Tweet> tweets;

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        mContext = context;
        tweets = objects;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Tweet tweet = tweets.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());

        viewHolder.tvTimeStamp.setText(tweet.getRelativeTimeAgo());

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(mContext).load(tweet.getUser().getProfileImageUrl()).transform(new CircleTransform()).into(viewHolder.ivProfileImage);

        viewHolder.ivTweetImage.setImageResource(android.R.color.transparent);
        Picasso.with(mContext).load(tweet.getImageUrl()).transform(new FixedWidthTransformation(600)).into(viewHolder.ivTweetImage);

        viewHolder.tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));
        viewHolder.tvRetweetCount.setText(Integer.toString(tweet.getReTweetCount()));

        //Add listener to the item
        //The detail view will show up only when the body/image is clicked

        View.OnClickListener detailViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickedListener != null)
                    mOnItemClickedListener.onItemClicked(position);
            }
        };

        viewHolder.tvBody.setOnClickListener(detailViewListener);
        viewHolder.ivTweetImage.setOnClickListener(detailViewListener);

        //set up the reply to listener
        View.OnClickListener replyToListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickedListener != null)
                    mOnItemClickedListener.onReplyClicked(position);
            }
        };

        viewHolder.ivReplyTo.setOnClickListener(replyToListener);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

}
